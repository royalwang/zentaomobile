package com.cnezsoft.zentao.data;

import android.database.Cursor;

import org.json.JSONObject;

/**
 * DataEntry factor
 * Created by Catouse on 2015/1/29.
 */
public class DataEntryFactory {

    /**
     * Create entry
     * @param type
     * @return
     */
    public static DataEntry create(EntryType type) {
        switch (type) {
            case Todo:
                return new Todo();
            case Task:
                return new Task();
            case Bug:
                return new Bug();
            case Story:
                return new Story();
            default:
                return new DataEntry();
        }
    }

    /**
     * Create entry
     * @param type
     * @return
     */
    public static DataEntry create(EntryType type, JSONObject jsonObject) {
        switch (type) {
            case Todo:
                return new Todo(jsonObject);
            case Task:
                return new Task(jsonObject);
            case Bug:
                return new Bug(jsonObject);
            case Story:
                return new Story(jsonObject);
            case Product:
                return new Product(jsonObject);
            case Project:
                return new Project(jsonObject);
            default:
                return new DataEntry(jsonObject);
        }
    }

    /**
     * Create entry with cursor
     * @param type
     * @param cursor
     * @return
     */
    public static DataEntry create(EntryType type, Cursor cursor) {
        if(cursor.moveToNext()) {
            switch (type) {
                case Todo:
                    return new Todo(cursor);
                case Task:
                    return new Task(cursor);
                case Bug:
                    return new Bug(cursor);
                case Story:
                    return new Story(cursor);
                case Product:
                    return new Product(cursor);
                case Project:
                    return new Project(cursor);
                default:
                    return new DataEntry(cursor);
            }
        }
        return null;
    }
}
