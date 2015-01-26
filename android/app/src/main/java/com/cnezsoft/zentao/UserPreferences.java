package com.cnezsoft.zentao;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * User preferences
 * Created by Catouse on 2015/1/14.
 */
public class UserPreferences {
    private final String PREFERENCES_NAME = "com.cnezsoft.zentao.userdata";
    private final String CURRENT_IDENTIFY = "#CURRENT_IDENTIFY";
    private final String USER_LIST = "#USER_LIST";
    private SharedPreferences preferences;
    private String identify;
    private SharedPreferences.Editor editor;

    /**
     * Create a Editor if haven't.
     * @return
     */
    public UserPreferences edit() {
        if(editor == null) editor = preferences.edit();
        return this;
    }

    /**
     * Set a string value in the preferences editor
     * @param key
     * @param value
     * @return
     */
    public UserPreferences putString(String key, String value) {
        editor.putString(identify + "::" + key, value);
        return this;
    }

    /**
     * Set a set of string values in the preferences editor
     * @param key
     * @param values
     * @return
     */
    public UserPreferences putStringSet(String key, Set<String> values) {
        editor.putStringSet(identify + "::" + key, values);
        return this;
    }

    /**
     * Set a int value in the preferences editor
     * @param key
     * @param value
     * @return
     */
    public UserPreferences putInt(String key, int value) {
        editor.putInt(identify + "::" + key, value);
        return this;
    }

    /**
     * Set a long value in the preferences editor
     * @param key
     * @param value
     * @return
     */
    public UserPreferences putLong(String key, long value) {
        editor.putLong(identify + "::" + key, value);
        return this;
    }

    /**
     * Set a float value in the preferences editor
     * @param key
     * @param value
     * @return
     */
    public UserPreferences putFloat(String key, float value) {
        editor.putFloat(identify + "::" + key, value);
        return this;
    }

    /**
     * Set a boolean value in the preferences editor
     * @param key
     * @param value
     * @return
     */
    public UserPreferences putBoolean(String key, boolean value) {
        editor.putBoolean(identify + "::" + key, value);
        return this;
    }

    /**
     * Mark in the editor that a user preference value should be removed
     * @param key
     * @return
     */
    public UserPreferences remove(String key) {
        editor.remove(identify + "::" + key);
        return this;
    }

    /**
     * Clear all users's data
     * @return
     */
    public UserPreferences clear() {
        editor.clear();
        return this;
    }

    /**
     * Commit user preferences changes back from this Editor to the
     * {@link SharedPreferences} object it is editing.
     * @return
     */
    public UserPreferences commit() {
        editor.commit();
        return this;
    }

    /**
     * Set the identify, if editor is available then put in the editor
     * @param identify
     * @return
     */
    public UserPreferences setIdentify(String identify) {
        this.identify = identify;
        if(editor != null) editor.putString(CURRENT_IDENTIFY, identify);
        return this;
    }

    /**
     * Get the current identify
     * @param load
     * @return
     */
    public String getIdentify(boolean load) {
        if(load)
        {
            identify = preferences.getString(CURRENT_IDENTIFY, null);
        }
        return identify;
    }

    /**
     * Get the current identify quickly
     * @return
     */
    public String getIdentify() {
        return getIdentify(false);
    }

    /**
     * Get all users list as a strings set
     * @return
     */
    public Set<String> getUserList() {
        return getStringSet(USER_LIST, null);
    }

    /**
     * Constructor with application context
     * @param context
     */
    public UserPreferences(Context context) {
        preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        getIdentify(true);
    }

    /**
     * Constructor with application context and name
     * @param context
     */
    public UserPreferences(Context context, String name) {
        preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        getIdentify(true);
    }

    /**
     * Register on user change listener
     * @param listener
     */
    public void registerOnUserChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    /**
     * Unregister on user change listener
     * @param listener
     */
    public void unregisterOnUserChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        preferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    /**
     * Retrieve a string value from the user preferences.
     * @param key
     * @param defValue
     * @return
     */
    public String getString(String key, String defValue) {
        return preferences.getString(identify + "::" + key, defValue);
    }

    /**
     * Retrieve a set of String values from the user preferences.
     * @param key
     * @param defValues
     * @return
     */
    public Set<String> getStringSet(String key, Set<String> defValues) {
        return preferences.getStringSet(identify + "::" + key, defValues);
    }

    /**
     * Retrieve a int value from the user preferences.
     * @param key
     * @param defValue
     * @return
     */
    public int getInt(String key, int defValue) {
        return preferences.getInt(identify + "::" + key, defValue);
    }

    /**
     * Retrieve a long value from the user preferences.
     * @param key
     * @param defValue
     * @return
     */
    public long getLong(String key, long defValue) {
        return preferences.getLong(identify + "::" + key, defValue);
    }

    /**
     * Retrieve a float value from the user preferences.
     * @param key
     * @param defValue
     * @return
     */
    public float getFloat(String key, float defValue) {
        return preferences.getFloat(identify + "::" + key, defValue);
    }

    /**
     * Retrieve a boolean value from the user preferences.
     * @param key
     * @param defValue
     * @return
     */
    public boolean getBoolean(String key, boolean defValue) {
        return preferences.getBoolean(identify + "::" + key, defValue);
    }

    /**
     * Checks whether the user preferences contains a value.
     * @param key
     * @return
     */
    public boolean contains(String key) {
        return preferences.contains(identify + "::" + key);
    }
}
