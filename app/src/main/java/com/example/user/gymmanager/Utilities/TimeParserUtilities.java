package com.example.user.gymmanager.Utilities;

/**
 * 用來轉換時間型態的類別。
 */
public class TimeParserUtilities {

    public static final String SEPARATOR = ":";

    public static int parseHour(String time) {
        int index = time.indexOf(SEPARATOR);
        String hour = time.substring(0, index);
        return Integer.parseInt(hour);
    }

    public static int parseMinute(String time) {
        int index = time.indexOf(SEPARATOR);
        String minute = time.substring(index + SEPARATOR.length(), time.length());
        return Integer.parseInt(minute);
    }
}
