package com.cnezsoft.zentao.data;

import android.content.ContentValues;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by Catouse on 2015/1/15.
 */
public abstract class DataEntry {

    public enum Types {
        UNKNOWN,
        PRODUCT,
        PROJECT,
        TODO,
        TASK,
        STORY,
        BUG
    }

    public static final String TYPE_STRING = "string";
    public static final String TYPE_BOOLEAN = "bool";
    public static final String TYPE_INT = "int";
    public static final String TYPE_LONG = "long";
    public static final String TYPE_FLOAT = "flat";
    public static final String TYPE_DOUBLE = "double";
    public static final String TYPE_DATETIME = "datetime";

    private ContentValues values;
    protected HashMap<String, String> columns = new HashMap<>();
    protected Types type = Types.UNKNOWN;

    protected DataEntry() {
        createColumns();
        values = new ContentValues();
    }

    protected DataEntry(ContentValues values) {
        createColumns();
        this.values = values;
    }

    protected DataEntry(JSONObject json) {
        this();
        fromJSON(json);
    }

    public ContentValues getValues() {
        return values;
    }

    public int getId() {
        if(values != null)
        {
            return values.getAsInteger("id");
        }
        return -1;
    }

    public Object get(String key) {
        return values.get(key);
    }

    public String getAsString(String key) {
        return values.getAsString(key);
    }

    public int getAsInteger(String key) {
        return values.getAsInteger(key);
    }

    public long getAsLong(String key) {
        return values.getAsLong(key);
    }

    public float getAsFloat(String key) {
        return values.getAsFloat(key);
    }

    public double getAsDouble(String key) {
        return values.getAsDouble(key);
    }

    public boolean getAsBoolean(String key) {
        return values.getAsBoolean(key);
    }

    public Date getAsDate(String key) {
        return new Date(values.getAsLong(key));
    }

    public DataEntry put(String key, Object value) {
        return put(key, value.toString());
    }

    public DataEntry put(String key, String value) {
        values.put(key, value);
        return this;
    }

    public DataEntry put(String key, int value) {
        values.put(key, value);
        return this;
    }

    public DataEntry put(String key, float value) {
        values.put(key, value);
        return this;
    }

    public DataEntry put(String key, double value) {
        values.put(key, value);
        return this;
    }

    public DataEntry put(String key, long value) {
        values.put(key, value);
        return this;
    }

    public DataEntry put(String key, boolean value) {
        values.put(key, value);
        return this;
    }

    public DataEntry put(String key, Date value) {
        return put(key, value.getTime());
    }

    protected void createColumns(HashMap<String, String> columns) {
        values.put("id", TYPE_INT);
    }

    private void createColumns() {
        createColumns(columns);
    }

    protected void afterFromJSON(JSONObject json, HashSet<String> excepts) {

    }

    public void fromJSON(JSONObject json) {
        String name;
        HashSet<String> exceptColumns = null;
        for (Map.Entry<String, String> entry : columns.entrySet()) {
            name = entry.getKey();
            try {
                switch (entry.getValue()) {
                    case TYPE_LONG:
                    case TYPE_DATETIME:
                        values.put(name, json.getLong(name));
                        break;
                    case TYPE_INT:
                        values.put(name, json.getInt(name));
                        break;
                    case TYPE_FLOAT:
                    case TYPE_DOUBLE:
                        values.put(name, json.getDouble(name));
                        break;
                    case TYPE_BOOLEAN:
                        values.put(name, json.getBoolean(name));
                        break;
                    case TYPE_STRING:
                    default:
                        values.put(name, json.getString(name));
                        break;
                }
            } catch (JSONException e) {
                if(exceptColumns == null) exceptColumns = new HashSet<>();
                exceptColumns.add(name);
                Log.w("DataEntry", type.toString() + "->fromJSON(): value of '" + name
                        + "' can't convert to " + entry.getValue());
            }
        }

        afterFromJSON(json, exceptColumns);
    }

    public String toJSONString() {
        JSONStringer jsonStringer = null;
        try {
            jsonStringer = new JSONStringer().object();
            String name;
            for (Map.Entry<String, String> entry : columns.entrySet()) {
                name = entry.getKey();
                jsonStringer.key(name);
                switch (entry.getValue()) {
                    case TYPE_LONG:
                    case TYPE_DATETIME:
                        jsonStringer.value(getAsLong(name));
                        break;
                    case TYPE_INT:
                        jsonStringer.value(getAsInteger(name));
                        break;
                    case TYPE_BOOLEAN:
                        jsonStringer.value(getAsBoolean(name));
                        break;
                    case TYPE_FLOAT:
                        jsonStringer.value(getAsFloat(name));
                        break;
                    case TYPE_DOUBLE:
                        jsonStringer.value(getAsDouble(name));
                        break;
                    case TYPE_STRING:
                        jsonStringer.value(getAsString(name));
                        break;
                    default:
                        jsonStringer.value(get(name));
                }
            }

            return jsonStringer.endObject().toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "{}";
        }
    }
}
