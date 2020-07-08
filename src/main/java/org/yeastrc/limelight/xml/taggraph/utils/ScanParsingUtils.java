package org.yeastrc.limelight.xml.taggraph.utils;

public class ScanParsingUtils {

    public static String getScanFilePrefixFromScanF(String scanF) {
        String[] fields = scanF.split(":");
        return fields[0];
    }

    public static String getScanNumberFromScanF(String scanF) {
        String[] fields = scanF.split(":");
        return fields[1];
    }

}
