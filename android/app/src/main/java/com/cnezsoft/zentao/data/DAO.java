package com.cnezsoft.zentao.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Collection;

/**
 * Database access object
 * Created by Catouse on 2015/1/15.
 */
public class DAO {
    protected DbHelper helper;
    protected SQLiteDatabase db;

    /**
     * Constructor with context
     * @param context
     */
    public DAO(Context context) {
        helper = new DbHelper(context);
        db = helper.getWritableDatabase();
    }

    /**
     * Add entry to database
     * @param entry
     * @return
     */
    public long add(DataEntry entry) {
        return db.insert(entry.getType().name(), null, entry.getValues());
    }

    /**
     * Add entries to database
     * @param entries
     */
    public boolean add(Collection<DataEntry> entries) {
        boolean result = false;
        db.beginTransaction();
        try {
            for(DataEntry entry: entries) {
                add(entry);
            }
            db.setTransactionSuccessful();
            result = true;
        } finally {
            db.endTransaction();
        }
        return result;
    }

    /**
     * Update entry to database
     * @param entry
     * @return
     */
    public long update(DataEntry entry) {
        return db.update(entry.typeName(), entry.getValues(),
                entry.keyName() + " = ?", new String[]{entry.key()});
    }

    /**
     * Update entries to database
     * @param entries
     */
    public boolean update(Collection<DataEntry> entries) {
        boolean result = false;
        db.beginTransaction();
        try {
            for(DataEntry entry: entries) {
                update(entry);
            }
            db.setTransactionSuccessful();
            result = true;
        } finally {
            db.endTransaction();
        }
        return result;
    }

    /**
     * Save entry to database, add new item or update exists item
     * @param entry
     * @return
     */
    public long save(DataEntry entry) {
        if(contains(entry)) {
            return update(entry);
        }
        else {
            return add(entry);
        }
    }

    /**
     * Save entry to database, add new items or update exists items
     * @param entries
     */
    public boolean save(Collection<DataEntry> entries) {
        boolean result = false;
        db.beginTransaction();
        try {
            for(DataEntry entry: entries) {
                save(entry);
            }
            db.setTransactionSuccessful();
            result = true;
        } finally {
            db.endTransaction();
        }
        return result;
    }

    /**
     * Delete entry by key
     * @param type
     * @param key
     * @return
     */
    public long delete(EntryType type, String key) {
        return db.delete(type.name(), type.primaryKey().name() + " = ?",
                new String[]{key});
    }

    /**
     * Delete entry item
     * @param entry
     * @return
     */
    public long delete(DataEntry entry) {
        return delete(entry.getType(), entry.key());
    }

    /**
     * Delete entries
     * @param entries
     */
    public boolean delete(Collection<DataEntry> entries) {
        boolean result = false;
        db.beginTransaction();
        try {
            for(DataEntry entry: entries) {
                delete(entry);
            }
            db.setTransactionSuccessful();
            result = true;
        } finally {
            db.endTransaction();
        }
        return result;
    }

    /**
     * Count entries
     * @param type
     * @return
     */
    public long count(EntryType type) {
        Cursor cursor = db.rawQuery("SELECT count(*) FROM " + type.name(), null);
        if (cursor.moveToNext()) {
            return cursor.getLong(0);
        }
        return 0;
    }

    /**
     * Judge the database whether has the entry with the given key
     * @param type
     * @param key
     * @return
     */
    public boolean contains(EntryType type, String key) {
        Cursor cursor = db.rawQuery("SELECT count(*) FROM " + type.name()
                + " WHERE " + type.primaryKey() + " = " + key, null);
        if (cursor.moveToNext()) {
            return cursor.getLong(0) > 0;
        }
        return false;
    }

    /**
     * Judge the database whether has the entry
     * @param entry
     * @return
     */
    public boolean contains(DataEntry entry) {
        return contains(entry.getType(), entry.key());
    }

    /**
     * Query entry by given key
     * @param type
     * @param key
     * @return
     */
    public Cursor queryByKey(EntryType type, String key) {
        return db.rawQuery("SELECT * FROM " + type.name() + " WHERE " + type.primaryKey().name() + " = " + key, null);
    }

    /**
     * Query entries
     * @param type
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @param limit
     * @return
     */
    public Cursor query(EntryType type, String selection, String[] selectionArgs,
                        String groupBy, String having, String orderBy, String limit) {
        Cursor cursor = db.query(true, type.name(), type.getColumnNames(), selection,
                selectionArgs, groupBy, having, orderBy, limit);
        return cursor;
    }

    /**
     * Query all entries
     * @param type
     * @return
     */
    public Cursor query(EntryType type) {
        return db.rawQuery("SELECT * FROM " + type.name() + " ORDER BY " + type.primaryKey().name() + " DESC", null);
    }

    /**
     * Close database
     */
    public void close() {
        db.close();
    }
}
