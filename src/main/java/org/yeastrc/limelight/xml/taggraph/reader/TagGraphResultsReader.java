package org.yeastrc.limelight.xml.taggraph.reader;

import com.opencsv.CSVReader;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import org.yeastrc.limelight.xml.taggraph.objects.TagGraphPSM;
import org.yeastrc.limelight.xml.taggraph.objects.TagGraphReportedPeptide;
import org.yeastrc.limelight.xml.taggraph.objects.TagGraphResults;
import org.yeastrc.limelight.xml.taggraph.utils.ReportedPeptideUtils;
import org.yeastrc.limelight.xml.taggraph.utils.ScanParsingUtils;

import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class TagGraphResultsReader {

    public static TagGraphResults getTagGraphResults(File resultsFile, Map<String, BigDecimal> staticMods, Map<String, Collection<BigDecimal>> variableMods) throws Exception {

        TagGraphResultsReader reader = new TagGraphResultsReader(resultsFile, staticMods, variableMods);
        return reader.readTagGraphResults();

    }

    private TagGraphResultsReader(File resultsFile, Map<String, BigDecimal> staticMods, Map<String, Collection<BigDecimal>> variableMods) {
        this.resultsFile = resultsFile;
        this.staticMods = staticMods;
        this.variableMods = variableMods;
    }

    private TagGraphResults readTagGraphResults() throws Exception {

        TagGraphResults results = new TagGraphResults();
        results.setVersion("Unknown");
        results.setStaticMods(staticMods);
        results.setPeptidePSMMap(getPeptidePSMMap());

        return results;
    }

    /**
     * Read all the PSMs into the main data structure for results
     *
     * @return
     * @throws Exception
     */
    private Map<TagGraphReportedPeptide, Collection<TagGraphPSM>> getPeptidePSMMap() throws Exception {

        Map<TagGraphReportedPeptide, Collection<TagGraphPSM>> peptidePsmMap = new HashMap<>();

        for(TagGraphPSM psm = readNextPSM(); psm != null; psm = readNextPSM()) {
            TagGraphReportedPeptide reportedPeptide = getReportedPeptideForPsm(psm);

            if(!peptidePsmMap.containsKey(reportedPeptide)) {
                peptidePsmMap.put(reportedPeptide, new HashSet<>());
            }

            peptidePsmMap.get(reportedPeptide).add(psm);
        }

        // close the reader
        this.close();

        return peptidePsmMap;
    }

    private TagGraphReportedPeptide getReportedPeptideForPsm(TagGraphPSM psm) throws Exception {
        return ReportedPeptideUtils.getReportedPeptideForPSM(psm);
    }


    private TagGraphPSM readNextPSM() throws Exception {

        if( this.isClosed )
            throw new Exception( "Called readNextResult() on closed result file reader." );

        if( this.isDone )
            return null;

        if( this.csvReader == null ) {
            this.csvReader = new CSVReader( new FileReader( this.resultsFile ), '\t' );

            this.csvReader.readNext();	// throw away header
        }


        String[] fields = this.csvReader.readNext();

        if( fields == null ) {
            this.isDone = true;
            return null;
        }

        TagGraphPSM psm = new TagGraphPSM();

        psm.setScanFilePrefix(ScanParsingUtils.getScanFilePrefixFromScanF(fields[0]));
        psm.setScanNumber(Integer.parseInt(ScanParsingUtils.getScanNumberFromScanF(fields[0])));
        psm.setCharge(Integer.parseInt(fields[1]));

        BigDecimal retentionTime = BigDecimal.valueOf(Double.parseDouble(fields[2] ) * 60);	// rt is reported as minutes, we want seconds
        psm.setRetentionTime(retentionTime);

        psm.setObsMH(new BigDecimal(fields[3]));
        psm.setTheoMH(new BigDecimal(fields[4]));

        psm.setPpm(new BigDecimal(fields[5]));
        psm.setFdr(new BigDecimal(fields[9]));
        psm.setEmProbability(new BigDecimal(fields[10]));
        psm.setOneMinusLog10Em(new BigDecimal(fields[11]));
        psm.setSpectrumScore(new BigDecimal(fields[12]));
        psm.setAlignmentScore(new BigDecimal(fields[13]));
        psm.setCompositeScore(new BigDecimal(fields[14]));

        psm.setPeptideSequence(getPeptideSequenceForPSM(fields[18]));
        psm.setModifications(getModsForPSM(fields[20], psm.getPeptideSequence()));

        return psm;
    }



    /**
     * Get the dynamic mods reported for this PSM. The python data structure being parsed is:
     * [(('Insertion', 345.172225, 0), 'VVM', 0), (('Ala->Arg', 85.063997, 0.0), ('A', 'Anywhere'), 3)]
     *
     * @param sequence
     * @return
     * @throws Exception
     */
    private Map<Integer, BigDecimal> getModsForPSM(String modString, String sequence) throws Exception {

        Map<Integer, BigDecimal> mods = new HashMap<>();

        if(interp == null)
            interp = new PythonInterpreter();

        PyObject x = interp.eval(modString);
        List<List> modList = ((List)x);

        for(List individualModList : modList ) {
            List<Object> modDefinition = (List)individualModList.get(0);
            Double modMass = null;
            Double modMassError = 0.0;

            try {
                modMass = (Double)(modDefinition.get(1));
            } catch(Exception e) {
                modMass = Double.valueOf((Integer)(modDefinition.get(1)));
            }

            try {
                modMassError = (Double)(modDefinition.get(2));
            } catch(Exception e) {
                try {
                    modMassError = Double.valueOf((Integer) (modDefinition.get(2)));
                } catch(Exception e2) {
                    ;
                }
            }

            if(modMassError == null)
                modMassError = 0.0;

            modMass = modMass + modMassError;

            if(modMass == 0.0) {
                continue;
            }

            int position = (Integer)(individualModList.get(2)) + 1; // convert to starts-with-1

            if(isNTerminal(individualModList)) {
                position = 0;
            }

            if(isCTerminal(individualModList)) {
                position = sequence.length() + 1;
            }

            mods.put(position, BigDecimal.valueOf(modMass).setScale(4, RoundingMode.HALF_UP));
        }


        return mods;
    }

    private boolean isNTerminal(List<Object> modDefinitionList) {

        try {
            List<Object> termDefinitionList = ((List)modDefinitionList.get(1));
            String termDefinition = (String)(termDefinitionList.get(0));
            if(termDefinition.equals("N-term")) {
                return true;
            }
        } catch (Exception e) { ; }

        return false;
    }

    private boolean isCTerminal(List<Object> modDefinitionList) {

        try {
            List<Object> termDefinitionList = ((List)modDefinitionList.get(1));
            String termDefinition = (String)(termDefinitionList.get(0));
            if(termDefinition.equals("C-term")) {
                return true;
            }
        } catch (Exception e) { ; }

        return false;
    }


    /**
     * Convert F.PEPTIDE.R to PEPTIDE and return it
     * @param peptideWithMods
     * @return
     */
    private String getPeptideSequenceForPSM(String peptideWithMods) {

        peptideWithMods = peptideWithMods.replaceAll("^[\\w\\-]\\.", "");
        peptideWithMods = peptideWithMods.replaceAll("\\.[\\w\\-]$", "");

        return peptideWithMods;
    }

    /**
     * Close this reader, be sure to do this
     */
    public void close() {
        this.isClosed = true;

        if( this.csvReader != null ) {
            try { this.csvReader.close(); this.csvReader = null; }
            catch( Exception e ) { ; }
        }
    }


    private CSVReader csvReader;
    private boolean isDone = false;
    private boolean isClosed = false;

    private PythonInterpreter interp;
    private File resultsFile;
    private Map<String, BigDecimal> staticMods;
    private Map<String, Collection<BigDecimal>> variableMods;

}
