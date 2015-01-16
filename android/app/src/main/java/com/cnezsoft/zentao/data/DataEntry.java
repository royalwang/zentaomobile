package com.cnezsoft.zentao.data;

import android.content.ContentValues;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.Date;
import java.util.HashSet;

/**
 * Created by Catouse on 2015/1/15.
 */
public abstract class DataEntry {

    private ContentValues values;
    protected EntryType type = EntryType.Unknown;

    public EntryType getType() {
        return type;
    }

    protected void setType(EntryType type) {
        this.type = type;
    }

    protected void setType(String typeName) {
        setType(Enum.valueOf(EntryType.class, typeName.trim().toUpperCase()));
    }

    protected DataEntry() {
        onCreate();
        values = new ContentValues();
    }

    protected DataEntry(ContentValues values) {
        onCreate();
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
        return values.getAsInteger("id");
    }

    public String typeName() {
        return type.name();
    }

    public String keyName() {
        return type.primaryKey().name();
    }

    public String key() {
        return getAsString(keyName());
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

    public abstract void onCreate();

    protected void afterFromJSON(JSONObject json, HashSet<IColumn> excepts) {
    }

    public void fromJSON(JSONObject json) {
        String name;
        HashSet<IColumn> exceptColumns = null;
        for(IColumn column: type.columns()) {
            name = column.name();
            try {
                switch (column.type()) {
                    case LONG:
                    case DATETIME:
                        values.put(name, json.getLong(name));
                        break;
                    case INT:
                        values.put(name, json.getInt(name));
                        break;
                    case FLOAT:
                    case DOUBLE:
                        values.put(name, json.getDouble(name));
                        break;
                    case BOOLEAN:
                        values.put(name, json.getBoolean(name));
                        break;
                    case STRING:
                    default:
                        values.put(name, json.getString(name));
                        break;
                }
            } catch (JSONException e) {
                if(exceptColumns == null) exceptColumns = new HashSet<>();
                exceptColumns.add(column);
                Log.w("DataEntry", type.toString() + "->fromJSON(): value of '" + name
                        + "' can't convert to " + column.type());
            }
        }

        afterFromJSON(json, exceptColumns);
    }

    public String toJSONString() {
        JSONStringer jsonStringer = null;
        try {
            jsonStringer = new JSONStringer().object();
            String name;
            for(IColumn column: type.columns()) {
                name = column.name();
                jsonStringer.key(name);
                switch (column.type()) {
                    case LONG:
                    case DATETIME:
                        jsonStringer.value(getAsLong(name));
                        break;
                    case INT:
                        jsonStringer.value(getAsInteger(name));
                        break;
                    case BOOLEAN:
                        jsonStringer.value(getAsBoolean(name));
                        break;
                    case FLOAT:
                        jsonStringer.value(getAsFloat(name));
                        break;
                    case DOUBLE:
                        jsonStringer.value(getAsDouble(name));
                        break;
                    case STRING:
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
