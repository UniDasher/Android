package com.uni.unidasher;

/**
 * This class contains constants for application usage. Here are also stored settings for both versions:
 * QA/dev ({@code BuildConfig.DEBUG} and release ({@code BuildConfig.RELEASE}).
 */

public class AppConstants {

    /**
     * 域名
     */
    public static final String HOST;

    static {
        if (BuildConfig.DEBUG) {
//            HOST = "http://192.168.1.3:8080";
            HOST = "http://122.114.44.116:8080/dasher";
        } else {
            HOST = "";
        }
    }
}
