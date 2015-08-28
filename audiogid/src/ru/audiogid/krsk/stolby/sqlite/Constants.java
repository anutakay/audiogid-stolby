package ru.audiogid.krsk.stolby.sqlite;

import android.annotation.SuppressLint;

public class Constants {

    public static final String PACKAGE = "ru.audiogid.krsk.stolby";

    @SuppressLint("SdCardPath")
    public static final String DB_PATH = "/data/data/" + PACKAGE
            + "/databases/";

    public static final String EXT = ".sqlite";

    public static final double HOME_LON = 92.738501;
    public static final double HOME_LAT = 55.975218;
}
