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
            case Bug:
                return new Bug();
            case Story:
                return new Story();
            default:
                return new DataEntry();
        }
    }

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
                default:
                    return new DataEntry(cursor);
            }
        }
        return null;
    }
}