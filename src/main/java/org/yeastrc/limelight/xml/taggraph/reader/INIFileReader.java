package org.yeastrc.limelight.xml.taggraph.reader;

import org.apache.commons.configuration2.SubnodeConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.yeastrc.limelight.xml.taggraph.ini.ParsedINIFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class INIFileReader {

    public static INIFileReader getInstance(File iniFile) throws IOException, ConfigurationException {
        return new INIFileReader(new ParsedINIFile(iniFile));
    }

    private INIFileReader(ParsedINIFile parsedINIFile) {
        this.parsedINIFile = parsedINIFile;
    }

    /**
     * Return the static mods found in the ini file.
     *
     * @return
     * @throws Exception
     */
    public Map<String, BigDecimal> getStaticMods() throws Exception {
        Map<String, BigDecimal> staticMods = new HashMap<>();

        SubnodeConfiguration section = this.parsedINIFile.getConfig().getSection("Static Mods");

        if(section != null) {

            Iterator<String> modIter = section.getKeys();

            if(modIter != null) {
                while(modIter.hasNext()) {
                    String value = section.getString(modIter.next());

                    if(value != null) {
                        String[] values = value.split("\\s+");
                        if(values.length != 2) {
                            throw new Exception("Got unexpected syntax for static mod. Got: " + value);
                        }

                        staticMods.put(values[0], new BigDecimal(values[1]));
                    }
                }
            }
        }


        return staticMods;
    }

    public Map<String, Collection<BigDecimal>> getVariableMods() throws Exception {
        Map<String, Collection<BigDecimal>> variableMods = new HashMap<>();

        SubnodeConfiguration section = this.parsedINIFile.getConfig().getSection("Diff Mods");

        if(section != null) {

            Iterator<String> modIter = section.getKeys();

            if(modIter != null) {
                while(modIter.hasNext()) {
                    String value = section.getString(modIter.next());

                    if(value != null) {
                        String[] values = value.split("\\s+");
                        if(values.length != 3) {
                            throw new Exception("Got unexpected syntax for variable mod. Got: " + value);
                        }

                        if(!variableMods.containsKey(values[0])) {
                            variableMods.put(values[0], new HashSet<>());
                        }

                        variableMods.get(values[0]).add(new BigDecimal(values[1]));
                    }
                }
            }
        }


        return variableMods;
    }


    private ParsedINIFile parsedINIFile;

}
