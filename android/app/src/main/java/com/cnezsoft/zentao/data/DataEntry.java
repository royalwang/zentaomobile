package com.cnezsoft.zentao.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.cnezsoft.zentao.DateFormatType;
import com.cnezsoft.zentao.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Data entry
 *
 * Created by Catouse on 2015/1/15.
 */
public class DataEntry {

    private ContentValues values;

    /**
     * Entry type
     */
    protected EntryType type = EntryType.Default;

    /**
     * Get entry type
     * @return
     */
    public EntryType getType() {
        return type;
    }

    /**
     * Set entry type
     * @param type
     */
    protected void setType(EntryType type) {
        this.type = type;
    }

    /**
     * Set entry type by name
     * @param typeName
     */
    protected void setType(String typeName) {
        setType(Enum.valueOf(EntryType.class, typeName.trim().toUpperCase()));
    }

    /**
     * Constructor without params
     */
    public DataEntry() {
        onCreate();
        values = new ContentValues();
    }

    public DataEntry(EntryType type) {
        this();
        setType(type);
    }

    /**
     * Constructor with Contentvalues
     * @param values
     */
    public DataEntry(ContentValues values) {
        onCreate();
        this.values = values;
    }

    public DataEntry(JSONArray jsonArray, String[] keys) {
        this();
        fromJSONArray(jsonArray, keys);
    }

    /**
     * Constructor with json
     * @param json
     */
    public DataEntry(JSONObject json) {
        this();
        fromJSON(json);
    }

    /**
     * Constructor with cursor
     * @param cursor
     */
    public DataEntry(Cursor cursor) {
        this();
        fromCursor(cursor);
    }

    /**
     * Get values
     * @return
     */
    public ContentValues getValues() {
        return values;
    }

    public void markDeleting() {
        values.put("deleting", true);
    }

    public boolean deleting() {
        Boolean result = values.getAsBoolean("deleting");
        return result != null && result == true;
    }

    public int getAccentPri() {
        Integer pri = values.getAsInteger("pri");
        if(pri == null) return -1;
        else return pri;
    }

    /**
     * Get type name
     * @return
     */
    public String typeName() {
        return type.name();
    }

    /**
     * Get key attribute name
     * @return
     */
    public String keyName() {
        return type.primaryKey().name();
    }

    /**
     * Get key attribute value
     * @return
     */
    public String key() {
        return getAsString(type.primaryKey());
    }

    /**
     * Get value
     * @param col
     * @return
     */
    public Object get(IColumn col) {
        return values.get(col.name());
    }

    public String getFriendlyString(IColumn col) {
        switch (col.type()) {
            case DATETIME:
                return Helper.formatDate(getAsDate(col), DateFormatType.DateTime);
            default:
                Object value = get(col);
                return value != null ? value.toString() : null;
        }
    }

    /**
     * Get attribute value as string
     * @param col
     * @return
     */
    public String getAsString(IColumn col) {
        if(col == null) return null;
        return values.getAsString(col.name());
    }

    /**
     * Get attribute value as integer
     * @param col
     * @return
     */
    public Integer getAsInteger(IColumn col) {
        return values.getAsInteger(col.name());
    }

    /**
     * Get attribute value as long
     * @param col
     * @return
     */
    public Long getAsLong(IColumn col) {
        return values.getAsLong(col.name());
    }

    /**
     * Get attribute value as float
     * @param col
     * @return
     */
    public Float getAsFloat(IColumn col) {
        return values.getAsFloat(col.name());
    }

    /**
     * Get attribute value as double
     * @param col
     * @return
     */
    public Double getAsDouble(IColumn col) {
        return values.getAsDouble(col.name());
    }

    /**
     * Get attribute value as boolean
     * @param col
     * @return
     */
    public Boolean getAsBoolean(IColumn col) {
        return values.getAsBoolean(col.name());
    }

    /**
     * Get attribute value as date
     * @param col
     * @return
     */
    public Date getAsDate(IColumn col) {
        return new Date(getAsLong(col));
    }

    /**
     * Put an object value to attribute
     * @param col
     * @param value
     * @return
     */
    public DataEntry put(IColumn col, Object value) {
        return put(col, value.toString());
    }

    /**
     * Put a string value to attribute
     * @param col
     * @param value
     * @return
     */
    public DataEntry put(IColumn col, String value) {
        values.put(col.name(), value);
        return this;
    }

    /**
     * Put an integer value to attribute
     * @param col
     * @param value
     * @return
     */
    public DataEntry put(IColumn col, Integer value) {
        values.put(col.name(), value);
        return this;
    }

    /**
     * Put a float value to attribute
     * @param col
     * @param value
     * @return
     */
    public DataEntry put(IColumn col, Float value) {
        values.put(col.name(), value);
        return this;
    }

    /**
     * Put double value to attribute
     * @param col
     * @param value
     * @return
     */
    public DataEntry put(IColumn col, Double value) {
        values.put(col.name(), value);
        return this;
    }

    /**
     * Put long value to attribute
     * @param col
     * @param value
     * @return
     */
    public DataEntry put(IColumn col, Long value) {
        values.put(col.name(), value);
        return this;
    }

    /**
     * Put boolean value to attribute
     * @param col
     * @param value
     * @return
     */
    public DataEntry put(IColumn col, Boolean value) {
        values.put(col.name(), value);
        return this;
    }

    /**
     * Put date value to attribute
     * @param col
     * @param value
     * @return
     */
    public DataEntry put(IColumn col, Date value) {
        return put(col, value.getTime());
    }

    /**
     * The method for inherited classes to init settings
     */
    protected void onCreate() {

    }

    /**
     * The method for inherited classes to handle excepts attributes after init form JSONObject
     * @param json
     * @param excepts
     */
    protected void afterFromJSON(JSONObject json, HashSet<IColumn> excepts) {
    }

    /**
     * Init attributes from JSONObject
     * @param json
     */
    public void fromJSON(JSONObject json) {
        String name;
        HashSet<IColumn> exceptColumns = null;
        for(IColumn column: type.columns()) {
            name = column.name();
            try {
                switch (column.type()) {
                    case LONG:
                    case DATETIME:
                        Long d = json.getLong(name);
                        if(d == null) {
                            try {
                                d = DateFormat.getDateTimeInstance().parse(json.getString(name)).getTime();
                            } catch (ParseException e) {
                                continue;
                            }
                        } else if(d < 100000000000l) {
                            d *= 1000;
                        }
                        values.put(name, d);
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
                    case ENUM:
                        values.put(name, json.optString(name, "_"));
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

    public void fromJSONArray(JSONArray jsonArray, String[] keys) {
        HashMap<String, Integer> keysMap = new HashMap<>();
        String key;
        for(int i = 0; i < keys.length; ++i) {
            key = keys[i];
            if(key.equals("id")) {
                key = "_id";
            } else if(key.equals("deleted") && jsonArray.optInt(i) > 0) {
                markDeleting();
                return;
            }
            keysMap.put(key, i);
        }

        String name;
        Integer index;
        for(IColumn column: type.columns()) {
            name = column.name();
            index = keysMap.get(name);
            if(index == null) {
                continue;
            }
            try {
                switch (column.type()) {
                    case LONG:
                    case DATETIME:
                        Long d = jsonArray.optLong(index);
                        if(d == 0) {
                            try {
                                d = DateFormat.getDateTimeInstance().parse(jsonArray.getString(index)).getTime();
                            } catch (ParseException e) {
                                continue;
                            }
                        } else if(d < 100000000000l) {
                            d *= 1000;
                        }
                        values.put(name, d);
                        break;
                    case INT:
                        values.put(name, jsonArray.getInt(index));
                        break;
                    case FLOAT:
                    case DOUBLE:
                        values.put(name, jsonArray.getDouble(index));
                        break;
                    case BOOLEAN:
                        try {
                            values.put(name, jsonArray.getBoolean(index));
                        } catch (JSONException e) {
                            values.put(name, jsonArray.getInt(index) > 0);
                        }
                        break;
                    case ENUM:
                        String eStr;
                        try {
                            eStr = jsonArray.getString(index);
                        } catch (JSONException e) {
                            eStr = "_";
                        }
//                        if(Helper.isNullOrEmpty(eStr)) {
//                            eStr = "_";
//                        }
//                        else if(eStr.equals("interface")) {
//                            eStr = "_" + eStr;
//                        }
                        values.put(name, eStr);
                        break;
                    case STRING:
                    default:
                        values.put(name, jsonArray.getString(index));
                        break;
                }
            } catch (JSONException e) {
                Log.w("DataEntry", type.toString() + "->fromJSONArray(): value of '" + name
                        + "' can't convert to " + column.type());
            }
        }
    }

    /**
     * Init attributes form cursor
     * @param cursor
     */
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

    /**
     * Convert to JSON string
     * @return
     */
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
