package com.cnezsoft.zentao.data;

import android.content.ContentValues;
import android.database.Cursor;
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

    protected DataEntry(Cursor cursor) {
        this();
        fromCursor(cursor);
    }

    public ContentValues getValues() {
        return values;
    }

    public String typeName() {
        return type.name();
    }

    public String keyName() {
        return type.primaryKey().name();
    }

    public String key() {
        return getAsString(type.primaryKey());
    }

    public Object get(IColumn col) {
        return values.get(col.name());
    }

    public String getAsString(IColumn col) {
        return values.getAsString(col.name());
    }

    public Integer getAsInteger(IColumn col) {
        return values.getAsInteger(col.name());
    }

    public Long getAsLong(IColumn col) {
        return values.getAsLong(col.name());
    }

    public Float getAsFloat(IColumn col) {
        return values.getAsFloat(col.name());
    }

    public Double getAsDouble(IColumn col) {
        return values.getAsDouble(col.name());
    }

    public Boolean getAsBoolean(IColumn col) {
        return values.getAsBoolean(col.name());
    }

    public Date getAsDate(IColumn col) {
        return new Date(getAsLong(col));
    }

    public DataEntry put(IColumn col, Object value) {
        return put(col, value.toString());
    }

    public DataEntry put(IColumn col, String value) {
        values.put(col.name(), value);
        return this;
    }

    public DataEntry put(IColumn col, Integer value) {
        values.put(col.name(), value);
        return this;
    }

    public DataEntry put(IColumn col, Float value) {
        values.put(col.name(), value);
        return this;
    }

    public DataEntry put(IColumn col, Double value) {
        values.put(col.name(), value);
        return this;
    }

    public DataEntry put(IColumn col, Long value) {
        values.put(col.name(), value);
        return this;
    }

    public DataEntry put(IColumn col, Boolean value) {
        values.put(col.name(), value);
        return this;
    }

    public DataEntry put(IColumn col, Date value) {
        return put(col, value.getTime());
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

    public void fromCursor(Cursor cursor) {
        String name;
        int index;
        for(IColumn column: type.columns()) {
            name = column.name();
            index = cursor.getColumnIndex(name);
            if(index < 0) continue;

            switch (column.type()) {
                case LONG:
                case DATETIME:
                    values.put(name, cursor.getLong(index));
                    break;
                case INT:
                    values.put(name, cursor.getInt(index));
                    break;
                case FLOAT:
                case DOUBLE:
                    values.put(name, cursor.getDouble(index));
                    break;
                case BOOLEAN:
                    values.put(name, cursor.getInt(index) > 0);
                    break;
                case STRING:
                default:
                    values.put(name, cursor.getString(index));
                    break;
            }
        }
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
                        jsonStringer.value(getAsLong(column));
                        break;
                    case DATETIME:
                        jsonStringer.value(new Date(getAsLong(column)));
                        break;
                    case INT:
                        jsonStringer.value(getAsInteger(column));
                        break;
                    case BOOLEAN:
                        jsonStringer.value(getAsBoolean(column));
                        break;
                    case FLOAT:
                        jsonStringer.value(getAsFloat(column));
                        break;
                    case DOUBLE:
                        jsonStringer.value(getAsDouble(column));
                        break;
                    case STRING:
                        jsonStringer.value(getAsString(column));
                        break;
                    default:
                        jsonStringer.value(get(column));
                }
            }

            return jsonStringer.endObject().toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "{}";
        }
    }
}
