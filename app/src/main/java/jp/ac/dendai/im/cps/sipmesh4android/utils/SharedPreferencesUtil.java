package jp.ac.dendai.im.cps.sipmesh4android.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {
    private static final String TAG = SharedPreferencesUtil.class.getSimpleName();
    private static final String NAME = "name";
    private static final String BOOL = "bool";

    public static void putInt(String key, int value, Context context){
        SharedPreferences data = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void putString(String key, String value, Context context){
        SharedPreferences data = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void putName(int key, String value, Context context) {
        SharedPreferences data = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        editor.putString(NAME + String.valueOf(key), value);
        editor.apply();
    }

    public static void putBool(int key, boolean value, Context context) {
        SharedPreferences data = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        editor.putString(BOOL + String.valueOf(key), String.valueOf(value));
        editor.apply();
    }

    public static int getInt(String key, Context context){
        SharedPreferences data = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return data.getInt(key, -1);
    }

    public static String getString(String key, Context context){
        SharedPreferences data = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return data.getString(key, "");
    }

    public static String getName(int key, Context context){
        SharedPreferences data = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return data.getString(NAME + String.valueOf(key), "");
    }

    public static boolean getBool(int key, Context context){
        SharedPreferences data = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        String str = data.getString(BOOL + String.valueOf(key), "");
        if (str.equals("")) {
            return false;
        }
        return Boolean.parseBoolean(str);
    }
}
