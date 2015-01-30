package com.cnezsoft.zentao.data;

import android.database.Cursor;

/**
 * Created by Catouse on 2015/1/29.
 */
public class DataEntryFactory {

    public static DataEntry create(EntryType type) {
        switch (type) {
            case Todo:
                return new Todo();
            case Task:
                return new Task();
            default:
                return new DataEntry();
        }
    }

    public static DataEntry create(EntryType type, Cursor cursor) {
        if(cursor.moveToNext()) {
            switch (type) {
                case Todo:
                    return new Todo(cursor);
                default:
                    return new DataEntry(cursor);
            }
        }
        return null;
    }
}
