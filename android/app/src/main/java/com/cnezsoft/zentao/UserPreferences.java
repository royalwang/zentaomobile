package com.cnezsoft.zentao;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.cnezsoft.zentao.data.DataType;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * UserOld preferences
 * Created by Catouse on 2015/1/14.
 */
public class UserPreferences {

    /**
     * On user attribute change listner
     */
    public interface  OnUserAttrChangeListener {
        void onUserAttrChange(String name, Object value);
    }

    private final String PREFERENCES_NAME = "com.cnezsoft.zentao.userdata";
    private final String CURRENT_IDENTIFY = "#CURRENT_IDENTIFY";
    private final String USER_LIST = "#USER_LIST";
    public final String COMMON_LISTENER_NAME = "?";
    private SharedPreferences preferences;
    private String identify;
    private SharedPreferences.Editor editor;
    private HashMap<String, ArrayList<OnUserAttrChangeListener>> userAttrChangeListeners;

    private String getIdentifiedKey(String key) {
        if(key == null) {
            throw new IllegalArgumentException("Key is null");
        }
        return identify.toString() + "::" + key;
    }

    private String getKeyFromIdentifyKey(String identifyKey) {
        String prefix = identify.toString() + "::";
        if(identifyKey.startsWith(prefix)) {
            return identifyKey.substring(prefix.length());
        }
        return identifyKey;
    }

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
        editor.putString(getIdentifiedKey(key), value);
        return this;
    }

    /**
     * Set a set of string values in the preferences editor
     * @param key
     * @param values
     * @return
     */
    public UserPreferences putStringSet(String key, Set<String> values) {
        editor.putStringSet(getIdentifiedKey(key), values);
        return this;
    }

    /**
     * Set a int value in the preferences editor
     * @param key
     * @param value
     * @return
     */
    public UserPreferences putInt(String key, int value) {
        editor.putInt(getIdentifiedKey(key), value);
        return this;
    }

    /**
     * Set a long value in the preferences editor
     * @param key
     * @param value
     * @return
     */
    public UserPreferences putLong(String key, long value) {
        editor.putLong(getIdentifiedKey(key), value);
        return this;
    }

    /**
     * Set a float value in the preferences editor
     * @param key
     * @param value
     * @return
     */
    public UserPreferences putFloat(String key, float value) {
        editor.putFloat(getIdentifiedKey(key), value);
        return this;
    }

    /**
     * Set a boolean value in the preferences editor
     * @param key
     * @param value
     * @return
     */
    public UserPreferences putBoolean(String key, boolean value) {
        editor.putBoolean(getIdentifiedKey(key), value);
        return this;
    }

    /**
     * Mark in the editor that a user preference value should be removed
     * @param key
     * @return
     */
    public UserPreferences remove(String key) {
        editor.remove(getIdentifiedKey(key));
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
        if(identify !=null && !identify.equals(this.identify)) {
            this.identify = identify;
            edit();
            editor.putString(CURRENT_IDENTIFY, identify);
            commit();
            Log.v("User preferences", "Set identify: " + identify);
        }
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
            Log.v("User preferences", "Get identify: " + identify);
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
        preferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if(userAttrChangeListeners != null) {
                    String cleanKey = getKeyFromIdentifyKey(key);
                    callListeners(cleanKey, userAttrChangeListeners.get(COMMON_LISTENER_NAME));
                    if(!cleanKey.equals(key)) {
                        callListeners(key, userAttrChangeListeners.get(key));
                    }
                    callListeners(key, userAttrChangeListeners.get(cleanKey));
                }
            }

            private void callListeners(String key, ArrayList<OnUserAttrChangeListener> listeners) {
                if(listeners != null) {
                    UserAttr attr = Helper.getEnumValueFromName(UserAttr.class, key, null);
                    if(attr != null) {
                        for(OnUserAttrChangeListener listener: listeners) {
                            listener.onUserAttrChange(key, get(attr));
                        }
                    } else {
                        for(OnUserAttrChangeListener listener: listeners) {
                            listener.onUserAttrChange(key, getString(key, null));
                        }
                    }
                }
            }

        });
        getIdentify(true);
    }

    public void setOnUserAttrChangeListener(String name, OnUserAttrChangeListener listener) {
        if(userAttrChangeListeners == null) {
            userAttrChangeListeners = new HashMap<>(1);
        }
        ArrayList<OnUserAttrChangeListener> listeners = userAttrChangeListeners.get(name);
        if(listeners == null) {
            listeners = new ArrayList<>();
            userAttrChangeListeners.put(name, listeners);
        }
        listeners.add(listener);
    }

    public void setOnUserAttrChangeListener(String[] names, OnUserAttrChangeListener listener) {
        for(String name: names) {
            setOnUserAttrChangeListener(name, listener);
        }
    }

    /**
     * Retrieve a string value from the user preferences.
     * @param key
     * @param defValue
     * @return
     */
    public String getString(String key, String defValue) {
        return preferences.getString(getIdentifiedKey(key), defValue);
    }

    /**
     * Retrieve a set of String values from the user preferences.
     * @param key
     * @param defValues
     * @return
     */
    public Set<String> getStringSet(String key, Set<String> defValues) {
        return preferences.getStringSet(getIdentifiedKey(key), defValues);
    }

    /**
     * Retrieve a int value from the user preferences.
     * @param key
     * @param defValue
     * @return
     */
    public int getInt(String key, int defValue) {
        return preferences.getInt(getIdentifiedKey(key), defValue);
    }

    /**
     * Retrieve a long value from the user preferences.
     * @param key
     * @param defValue
     * @return
     */
    public long getLong(String key, long defValue) {
        return preferences.getLong(getIdentifiedKey(key), defValue);
    }

    /**
     * Retrieve a float value from the user preferences.
     * @param key
     * @param defValue
     * @return
     */
    public float getFloat(String key, float defValue) {
        return preferences.getFloat(getIdentifiedKey(key), defValue);
    }

    /**
     * Retrieve a boolean value from the user preferences.
     * @param key
     * @param defValue
     * @return
     */
    public boolean getBoolean(String key, boolean defValue) {
        return preferences.getBoolean(getIdentifiedKey(key), defValue);
    }

    /**
     * Checks whether the user preferences contains a value.
     * @param key
     * @return
     */
    public boolean contains(String key) {
        return preferences.contains(getIdentifiedKey(key));
    }

    public Enum getEnum(Class<? extends Enum> emumType, String key, Enum delValue) {
        String enumValue = getString(key, null);
        try {
            return enumValue != null ? Enum.valueOf(emumType, enumValue) : delValue;
        } catch (Exception e) {
            return delValue;
        }
    }

    public Object get(DataType type, String key, Object defValue) {
        switch (type) {
            case DATETIME:
                long value = getLong(key, -1);
                return value > 0 ? new Date(value) : defValue;
            case LONG:
                return getLong(key, (long) defValue);
            case BOOLEAN:
                return getBoolean(key, (boolean) defValue);
            case INT:
                return getInt(key, (int) defValue);
            case DOUBLE:
            case FLOAT:
                return getFloat(key, (float) defValue);
            case ENUM:
            case HTML:
            case STRING:
            default:
                return getString(key, defValue == null ? null : defValue.toString());
        }
    }

    public Object get(UserAttr attr) {
        return get(attr.getType(), attr.name(), attr.getDefaultValue());
    }

    public UserPreferences put(DataType type, String key, Object value) {
        Log.v("User Preferences", type + ", key=" + key + ", object=" + value);
        if(value == null) {
            remove(key);
        } else {
            switch (type) {
                case DATETIME:
                    long date;
                    try {
                        date = ((Date) value).getTime();
                    } catch (ClassCastException e) {
                        try {
                            date = (long) value;
                        } catch (ClassCastException e2) {
                            date = (int) value;
                        }
                    }
                    putLong(key, date);
                    break;
                case LONG:
                    putLong(key, (long) value);
                    break;
                case BOOLEAN:
                    putBoolean(key, (boolean) value);
                    break;
                case INT:
                    putInt(key, (int) value);
                    break;
                case DOUBLE:
                case FLOAT:
                    putFloat(key, (float) value);
                    break;
                case ENUM:
                case HTML:
                case STRING:
                default:
                    putString(key, value.toString());
            }
        }
        return this;
    }

    public UserPreferences put(UserAttr attr, Object value) {
        return put(attr.getType(), attr.name(), value);
    }

    public User getUser() {
        return getUser(null);
    }

    public User getUser(String identify) {
        if(identify == null) {
            identify = getIdentify(true);
        } else {
            setIdentify(identify);
        }
        User user = new User();
        if(identify != null) {
            user.setStatus(User.Status.OFFLINE);
            for(UserAttr attr: UserAttr.values()) {
                user.put(attr, get(attr));
            }
        }
        Log.v("UserPreferences", "Load User with identify=" + identify + ": " + user.toJSONString());
        return user;
    }

    public void saveUser(User user) {
        if(user.getIdentify() == null) {
            Log.w("UserPreferences", "User save failed, unknown user.");
            return;
        }
        edit();
        setIdentify(user.getIdentify());
        for(Map.Entry<UserAttr, Object> entry: user.getValues().entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
        commit();
        Log.v("UserPreferences", "Save User: " + user.toJSONString());
    }

    public void saveUserAttr(UserAttr attr, Object value) {
        edit().put(attr, value).commit();
    }
}
