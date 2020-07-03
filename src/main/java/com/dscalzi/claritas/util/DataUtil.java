package com.dscalzi.claritas.util;

public class DataUtil {

    public static String getPackage(String name) {
        return name.substring(0, name.substring(0, name.lastIndexOf('.')).lastIndexOf('.'));
    }

}
