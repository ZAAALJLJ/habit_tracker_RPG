package com.example.habittracker;

import android.content.Context;
import android.content.SharedPreferences;

public class ProgressUtil {
    private static final String PREFS_NAME = "MyPrefs";
    private static final String PROGRESS_KEY_EXP = "expProgress";
    private static final String PROGRESS_KEY_RANK = "rankProgress";
    private static final String PROGRESS_KEY_HEALTH = "healthProgress";
    private static final String HEALTH_INITIALIZED_KEY = "healthInitialized";
    private static final String TEXT_KEY = "textKey";
    private static final String EXP_KEY = "expKey";
    private static final String PROGRESS_KEY = "progressKey";
    private static final String ADVANCEMENT_KEY = "advancementKey";
    private static final String CUSTOM_KEY = "character_customized";
    private static final String NAME_KEY = "character_name";


    public static void saveName(Context context, String name){
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(NAME_KEY, name);
        editor.apply();
    }
    public static String getName(Context context){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(NAME_KEY, null);
    }
    public static void saveAdvancement(Context context, String advance){
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ADVANCEMENT_KEY, advance);
        editor.apply();
    }
    public static String getAdvancement(Context context){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(ADVANCEMENT_KEY, null);
    }
    public static void saveCustomized(Context context, boolean customized){
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(CUSTOM_KEY, customized);
        editor.apply();
    }

    public static boolean getCustomized(Context context){
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(CUSTOM_KEY, false);
    }

    public static void expProgress(Context context, int progress) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PROGRESS_KEY_EXP, progress);
        editor.apply();
    }

    public static int getExpProgress(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(PROGRESS_KEY_EXP, 0);
    }

    public static void rankProgress(Context context, int progress) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PROGRESS_KEY_RANK, progress);
        editor.apply();
    }

    public static int getRankProgress(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(PROGRESS_KEY_RANK, 0);
    }

    public static void healthProgress(Context context, int progress) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(PROGRESS_KEY_HEALTH, progress);
        editor.apply();
    }

    public static int getHealthProgress(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(PROGRESS_KEY_HEALTH, 0);
    }

    public static boolean isHealthInitialized(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(HEALTH_INITIALIZED_KEY, false);
    }

    public static void setHealthInitialized(Context context, boolean initialized) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(HEALTH_INITIALIZED_KEY, initialized);
        editor.apply();
    }

    public static void saveText(Context context, String text) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(TEXT_KEY, text);
        editor.apply();
    }
    public static String getSavedText(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(TEXT_KEY, "");
    }
    public static String getSavedText2(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(EXP_KEY, "");
    }
    public static void saveText2(Context context, String text) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(EXP_KEY, text);
        editor.apply();
    }

    public static String getSavedText3(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(PROGRESS_KEY, "");
    }
    public static void saveText3(Context context, String text) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROGRESS_KEY, text);
        editor.apply();
    }

    public static int getMaxProgressFromPreferences(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        // The second parameter (0) is the default value in case the key is not found
        return preferences.getInt("maxProgress", 5); // 5 is just an example default value
    }

    public static void saveMaxProgressToPreferences(Context context, int maxProgress) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("maxProgress", maxProgress);
        editor.apply();
    }


}

