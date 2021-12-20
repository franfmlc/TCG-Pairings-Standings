package com.southernsoft.tcgtournament.util;

public class RoundTimeUtils {
    private static final String baseTime = "%1$02d:%2$02d";

    public static String formatElapsedTime(int elapsedSeconds) {
        int minutes = 0;
        if (elapsedSeconds >= 60) {
            minutes = elapsedSeconds / 60;
            elapsedSeconds -= minutes * 60;
        }
        return String.format(baseTime, minutes, elapsedSeconds);
    }
}