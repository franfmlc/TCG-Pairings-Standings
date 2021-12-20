package com.southernsoft.tcgtournament;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class SharedPreferencesHelper {
    private final String packageName;
    private final Context context;
    private final String darkModeKey = "dark_mode";

    @Inject
    public SharedPreferencesHelper(@ApplicationContext Context appContext) {
        context = appContext;
        packageName = context.getPackageName();
    }

    public void saveThemeMode(boolean isDarkMode) {
        SharedPreferences sharedPref = context.getSharedPreferences(packageName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(darkModeKey, isDarkMode);
        editor.apply();
    }

    public boolean getThemeMode() {
        SharedPreferences sharedPref = context.getSharedPreferences(packageName, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(darkModeKey, false);
    }
}