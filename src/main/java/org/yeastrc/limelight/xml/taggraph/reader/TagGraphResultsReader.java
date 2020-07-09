package org.yeastrc.limelight.xml.taggraph.reader;

import com.opencsv.CSVReader;
import org.yeastrc.limelight.xml.taggraph.objects.TagGraphPSM;
import org.yeastrc.limelight.xml.taggraph.objects.TagGraphReportedPeptide;
import org.yeastrc.limelight.xml.taggraph.objects.TagGraphResults;
import org.yeastrc.limelight.xml.taggraph.utils.ReportedPeptideUtils;
import org.yeastrc.limelight.xml.taggraph.utils.ScanParsingUtils;
import org.yeastrc.proteomics.mass.MassUtils;
import org.yeastrc.proteomics.peptide.aminoacid.AminoAcid;
import org.yeastrc.proteomics.peptide.aminoacid.AminoAcidUtils;

import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        psm.setModifications(getModsForPSM(fields, psm.getPeptideSequence()));

        return psm;
    }

    /**
     * Return true if this string represents an amino acid sub. in the form of "phe->ala"
     * @param modString
     * @return
     */
    private boolean isAminoAcidSubstitution(String modString) {
        Pattern p = Pattern.compile("^\\w{3}->\\w{3}$");
        Matcher m = p.matcher(modString);
        return m.matches();
    }

    /**
     * Get the mass diff corresponding to the given amino acid substitution
     * @param modString
     * @return
     * @throws Exception
     */
    private BigDecimal getAminoAcidSubstitutionMassShift(String modString) throws Exception {
        String[] aminoAcids = modString.split("->");
        if(aminoAcids.length != 2) {
            throw new Exception("Got invalid syntax for amino acid subst.: " + modString);
        }

        AminoAcid startAminoAcid = AminoAcidUtils.getAminoAcidByAbbreviation(aminoAcids[0].toLowerCase());
        AminoAcid endAminoAcid = AminoAcidUtils.getAminoAcidByAbbreviation(aminoAcids[1].toLowerCase());

        if(startAminoAcid == null) {
            throw new Exception("Could not find amino acid for: " + aminoAcids[0]);
        }

        if(endAminoAcid == null) {
            throw new Exception("Could not find amino acid for: " + aminoAcids[1]);
        }

        double massDiff = endAminoAcid.getMass(MassUtils.MassType.MONOISOTOPIC) - startAminoAcid.getMass(MassUtils.MassType.MONOISOTOPIC);
        return BigDecimal.valueOf(massDiff).setScale(4, RoundingMode.HALF_UP);
    }

    /**
     * Get the dynamic mods reported for this PSM
     *
     * @param fields
     * @param sequence
     * @return
     * @throws Exception
     */
    private Map<Integer, BigDecimal> getModsForPSM(String[] fields, String sequence) throws Exception {

        final int startingIndex = 31;
        Map<Integer, BigDecimal> mods = new HashMap<>();

        System.out.println(fields[18]);
        System.out.println(fields[29]);

        for(int i = startingIndex; i < fields.length; i += 4) {

            // we're done reading mods
            if(fields[i] == null || fields[i].length() < 1) {
                break;
            }

            int position = Integer.parseInt(fields[i]) + 1;

            BigDecimal mass = null;

            System.out.println(fields[i]);
            System.out.println(fields[i+1]);
            System.out.println(fields[i+2]);
            System.out.println(fields[i+3]);
            System.out.println(sequence + "\n");
            if(!isAminoAcidSubstitution(fields[i+1]))
                mass = new BigDecimal(fields[i+3]);
            else
                mass = getAminoAcidSubstitutionMassShift(fields[i+1]);

            // don't include static mods
            if(isStaticMod(position, mass, sequence))
                continue;

            // handle N- and C-terminal mods
            if(fields[i+2].equals("N-term")) {
                position = 0;
            } else if(fields[i+2].equals("C-term")) {
                position = sequence.length() + 1;
            }

            mods.put(position, mass);
        }

        return mods;
    }

    private boolean isStaticMod(Integer position, BigDecimal mass, String sequence) throws Exception {

        if(this.staticMods == null || this.staticMods.size() < 1) {
            return false;
        }

//        if(position == 0 || position == sequence.length() + 1) {
//            return false;
//        }

        String moddedResidue = sequence.substring(position - 1, position);

        if(!this.staticMods.containsKey(moddedResidue))
            return false;

        BigDecimal mass1 = this.staticMods.get(moddedResidue).setScale(2, RoundingMode.HALF_UP);
        BigDecimal mass2 = mass.setScale(2, RoundingMode.HALF_UP);

        if(!mass1.equals(mass2)) {
            return false;
        }

        return true;
    }


    /**
     * Convert F.PEPTIDE.R to PEPTIDE and return it
     * @param peptideWithMods
     * @return
     */
    private String getPeptideSequenceForPSM(String peptideWithMods) {

        peptideWithMods = peptideWithMods.replaceAll("^\\w\\.", "");
        peptideWithMods = peptideWithMods.replaceAll("\\.\\w$", "");

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

    private File resultsFile;
    private Map<String, BigDecimal> staticMods;
    private Map<String, Collection<BigDecimal>> variableMods;

}
