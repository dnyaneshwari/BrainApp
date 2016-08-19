package com.nishitadutta.brainapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;
import java.util.Set;

/**
 * Created by Nishita on 18-08-2016.
 */
public class SharedPreferenceUtils {

    private static final int DEFAULT_INT_VALUE = 0;
    private static final String DEFAULT_STRING_VALUE = null;
    private static final float DEFAULT_FLOAT_VALUE = 0.0f;
    private static final boolean DEFAULT_BOOLEAN_VALUE = false;
    private static final long DEFAULT_LONG_VALUE = 0;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static SharedPreferenceUtils utilInstance;
    public static String PREF_NAME = "sharedPreferences";
    private Context mContext;

    private SharedPreferenceUtils(Context context) {
        mContext = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static SharedPreferenceUtils getInstance(Context context) {
        if (utilInstance == null) {
            utilInstance = new SharedPreferenceUtils(context.getApplicationContext());
        }
        return utilInstance;
    }

    public void setValue(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public void setValue(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    public void setValue(String key, float value) {
        editor.putFloat(key, value);
        editor.apply();
    }

    public void setValue(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void setValue(String key, long value) {
        editor.putLong(key, value);
        editor.apply();
    }

    public void setValue(String key, Set<String> values) {
        editor.putStringSet(key, values);
        editor.apply();
    }

    public String getStringValue(String key, String defaultVal) {
        return sharedPreferences.getString(key, defaultVal);
    }

    public String getStringValue(String key) {
        return sharedPreferences.getString(key, DEFAULT_STRING_VALUE);
    }

    public int getIntValue(String key, int defaultVal) {
        return sharedPreferences.getInt(key, defaultVal);
    }

    public int getIntValue(String key) {
        return sharedPreferences.getInt(key, DEFAULT_INT_VALUE);
    }

    public float getFloatValue(String key, float defaultVal) {
        return sharedPreferences.getFloat(key, defaultVal);
    }

    public float getFloatValue(String key) {
        return sharedPreferences.getFloat(key, DEFAULT_FLOAT_VALUE);
    }

    public boolean getBooleanValue(String key, boolean defaultVal) {
        return sharedPreferences.getBoolean(key, defaultVal);
    }

    public boolean getBooleanValue(String key) {
        return sharedPreferences.getBoolean(key, DEFAULT_BOOLEAN_VALUE);
    }

    public long getLongValue(String key, long defaultVal) {
        return sharedPreferences.getLong(key, defaultVal);
    }

    public long getlongValue(String key) {
        return sharedPreferences.getLong(key, DEFAULT_LONG_VALUE);
    }

    public Set<String> getSetValue(String key, Set<String> defValues) {
        return sharedPreferences.getStringSet(key, defValues);
    }

    public Map<String, ?> getAllValues() {
        return sharedPreferences.getAll();
    }

    public void clearPrefs() {
        editor.clear().apply();
    }

    public void removeKey(String key) {
        editor.remove(key).apply();
    }

    public void registerListener(SharedPreferences.OnSharedPreferenceChangeListener changeListener) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(changeListener);
    }

    public void unregisterListener(SharedPreferences.OnSharedPreferenceChangeListener changeListener) {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(changeListener);
    }
}
