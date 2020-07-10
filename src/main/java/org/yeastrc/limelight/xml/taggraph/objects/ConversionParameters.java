package org.yeastrc.limelight.xml.taggraph.objects;

import java.io.File;
import java.util.List;

public class ConversionParameters {

    public ConversionParameters(File resultsFile, File fastaFile, File iniFile, File runIniFile, String outputFilePath, ConversionProgramInfo conversionProgramInfo) {
        this.outputFilePath = outputFilePath;
        this.conversionProgramInfo = conversionProgramInfo;
        this.resultsFile = resultsFile;
        this.iniFile = iniFile;
        this.runIniFile = runIniFile;
        this.fastaFile = fastaFile;
    }

    public String getOutputFilePath() {
        return outputFilePath;
    }

    public ConversionProgramInfo getConversionProgramInfo() {
        return conversionProgramInfo;
    }

    public File getFastaFile() {
        return fastaFile;
    }

    public File getIniFile() {
        return iniFile;
    }

    public File getResultsFile() {
        return resultsFile;
    }

    public File getRunIniFile() {
        return runIniFile;
    }

    private String outputFilePath;
    private ConversionProgramInfo conversionProgramInfo;
    private File fastaFile;
    private File iniFile;
    private File runIniFile;
    private File resultsFile;

}
