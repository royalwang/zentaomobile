package com.cnezsoft.zentao.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Set;

/**
 * Created by Catouse on 2015/1/15.
 */
public class DAO {
    private DbHelper helper;
    private SQLiteDatabase db;

    public DAO(Context context) {
        helper = new DbHelper(context);
        db = helper.getWritableDatabase();
    }

    public long add(DataEntry entry) {
        return db.insert(entry.getType().name(), null, entry.getValues());
    }

    public void add(Set<DataEntry> entries) {
        db.beginTransaction();
        try {
            for(DataEntry entry: entries) {
                add(entry);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public long update(DataEntry entry) {
        return db.update(entry.typeName(), entry.getValues(),
                entry.keyName() + " = ?", new String[]{entry.key()});
    }

    public void update(Set<DataEntry> entries) {
        db.beginTransaction();
        try {
            for(DataEntry entry: entries) {
                update(entry);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public long save(DataEntry entry) {
        if(contains(entry)) {
            return update(entry);
        }
        else {
            return add(entry);
        }
    }

    public void save(Set<DataEntry> entries) {
        db.beginTransaction();
        try {
            for(DataEntry entry: entries) {
                save(entry);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public long delete(EntryType type, String key) {
        return db.delete(type.name(), type.primaryKey().name() + " = ?",
                new String[]{key});
    }

    public long delete(DataEntry entry) {
        return delete(entry.getType(), entry.key());
    }

    public void delete(Set<DataEntry> entries) {
        db.beginTransaction();
        try {
            for(DataEntry entry: entries) {
                delete(entry);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public long count(EntryType type) {
        Cursor cursor = db.rawQuery("SELECT count(*) FROM " + type.name(), null);
        if (cursor.moveToNext()) {
            return cursor.getLong(0);
        }
        return 0;
    }

    public boolean contains(EntryType type, String key) {
        Cursor cursor = db.rawQuery("SELECT count(*) FROM " + type.name()
                + " WHERE " + type.primaryKey() + " = " + key, null);
        if (cursor.moveToNext()) {
            return true;
        }
        return false;
    }

    public boolean contains(DataEntry entry) {
        return contains(entry.getType(), entry.key());
    }

    public Cursor query(EntryType type, String selection, String[] selectionArgs,
                        String groupBy, String having, String orderBy, String limit) {
        Cursor cursor = db.query(true, type.name(), type.getColumnNames(), selection,
                selectionArgs, groupBy, having, orderBy, limit);
        return cursor;
    }

    public void close() {
        db.close();
    }
}
