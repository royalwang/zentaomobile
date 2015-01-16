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

    public interface IColumn {
        public DataType getType();
        public String name();
    }

    public enum DataType {
        STRING,
        BOOLEAN,
        INT,
        LONG,
        FLOAT,
        DOUBLE,
        DATETIME
    }

    public enum TodoColumns implements IColumn {
        id(DataType.INT),
        pri(DataType.INT),
        begin(DataType.DATETIME),
        end(DataType.DATETIME),
        type(DataType.STRING),
        name(DataType.STRING),
        status(DataType.STRING);

        private DataType dataType;

        public DataType getType() {
            return dataType;
        }

        TodoColumns(DataType dataType) {
            this.dataType = dataType;
        }
    }

    public enum Types {
        Unknown,
        Product,
        Project,
        Todo,
        Task,
        Story,
        Bug;

        private IColumn[] columns = null;

        public IColumn[] getColumns()
        {
            if(columns == null)
            {
                switch (this)
                {
                    case Todo:
                        columns = TodoColumns.values();
                        break;
                }
            }
            return columns;
        }
    }

    private ContentValues values;
    protected Types type = Types.Unknown;

    public Types getType() {
        return type;
    }

    protected void setType(Types type) {
        this.type = type;
    }

    protected void setType(String typeName) {
        setType(Enum.valueOf(Types.class, typeName.trim().toUpperCase()));
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

    public abstract void onCreate();

    protected void afterFromJSON(JSONObject json, HashSet<IColumn> excepts) {
    }

    public void fromJSON(JSONObject json) {
        String name;
        HashSet<IColumn> exceptColumns = null;
        for(IColumn column: type.getColumns()) {
            name = column.name();
            try {
                switch (column.getType()) {
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
                        + "' can't convert to " + column.getType());
            }
        }

        afterFromJSON(json, exceptColumns);
    }

    public String toJSONString() {
        JSONStringer jsonStringer = null;
        try {
            jsonStringer = new JSONStringer().object();
            String name;
            for(IColumn column: type.getColumns()) {
                name = column.name();
                jsonStringer.key(name);
                switch (column.getType()) {
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
