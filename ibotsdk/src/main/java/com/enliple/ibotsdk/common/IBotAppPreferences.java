package com.enliple.ibotsdk.common;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class IBotAppPreferences {
    public static final String NAME = "_IBOT";

    public static final String IBOT_BUTTON_BG_COLOR = "IBOT_BUTTON_BG_COLOR";
    public static final String IBOT_TEXT_COLOR = "IBOT_TEXT_COLOR";
    public static final String IBOT_TEXT = "IBOT_TEXT";
    public static final String IBOT_REG_DATE = "IBOT_REG_DATE";
    public static final String IBOT_ANIMATION_TYPE = "IBOT_ANIMATION_TYPE";
    public static final String IBOT_CLOSE_TIME = "IBOT_CLOSE_TIME";

    public static String getString(Context context, String name) {
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName() + NAME, MODE_PRIVATE);
        return preferences.getString(name, "");
    }

    public static long getLong(Context context, String name) {
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName() + NAME, MODE_PRIVATE);
        return preferences.getLong(name, -1);
    }

    public static void setString(Context context, String name, String value) {
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName() + NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(name, value);
        editor.commit();
    }

    public static void setLong(Context context, String name, long value) {
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName() + NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(name, value);
        editor.commit();
    }
}
