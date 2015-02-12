package com.cnezsoft.zentao.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;

import java.util.Collection;
import java.util.HashMap;

/**
 * Database access object
 * Created by Catouse on 2015/1/15.
 */
public class DAO {
    private static final String uriPrefix = "sqlite://com.cnezsoft.zentao/";

    public static Uri getUri(EntryType type) {
        return Uri.parse(uriPrefix + type.name());
    }

    protected DbHelper helper;
    protected SQLiteDatabase db;
    protected Context context;

    /**
     * Constructor with context
     * @param context
     */
    public DAO(Context context) {
        this.context = context;
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
        if(entry.deleting()) {
            return delete(entry);
        }
        else if(contains(entry)) {
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
    public DAOResult save(Collection<DataEntry> entries, boolean markNewUnread) {
        DAOResult result = new DAOResult();
        db.beginTransaction();
        try {
            for(DataEntry entry: entries) {
                // todo: check the NullPointerException exception
                if(entry == null) continue;
                if(entry.deleting()) {
                    long deleteResult = delete(entry);
                    if(deleteResult > 0) {
                        result.setDelete(entry.getType());
                    }
                } else if(contains(entry)) {
                    if(update(entry) > 0) {
                        result.setUpdate(entry.getType());
                    }
                } else {
                    if(markNewUnread) entry.markUnread();
                    if(add(entry) > 0) {
                        result.setAdd(entry.getType());
                    }
                }
            }
            db.setTransactionSuccessful();
            result.setResult(true);
        } finally {
            db.endTransaction();
        }

        if(result.getResult()) {
            result.notifyChange(context);
        }
        return result;
    }

    public DAOResult save(Collection<DataEntry> entries) {
        return save(entries, false);
    }

    /**
     * Delete entry by key
     * @param type
     * @param key
     * @return
     */
    public long delete(EntryType type, String key) {
        IColumn primaryKey = type.primaryKey();
        if(primaryKey == null) return 0;
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
        if(entries != null) {
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
     * Check the database whether is empty
     * @return
     */
    public boolean isDatabaseEmpty() {
        for(EntryType type: EntryType.values()) {
            if(type == EntryType.Default) continue;
            try {
                if(count(type) > 0) return false;
            } catch (SQLiteException e) {
            }
        }
        return true;
    }

    /**
     * Count entries
     * @param type
     * @return
     */
    public long count(EntryType type, String selection) {
        Cursor cursor = db.rawQuery("SELECT count(*) FROM " + type.name() + " WHERE " + selection, null);
        if (cursor.moveToNext()) {
            return cursor.getLong(0);
        }
        return 0;
    }

    public long count(IPageTab pageTab, String account) {
        EntryType entryType = pageTab.getEntryType();
        switch (entryType) {
            case Todo:
                Todo.PageTab todoTab = (Todo.PageTab) pageTab;
                switch (todoTab) {
                    case undone:
                        return count(entryType, TodoColumn.account + " = " + account + " AND " + TodoColumn.status.name() + " IS NOT " + Todo.Status.done.name());
                    case done:
                        return count(entryType, TodoColumn.account + " = " + account + " AND " + TodoColumn.status.name() +  " = " + Todo.Status.done.name());
                }
                break;
            case Task:
            case Bug:
            case Story:
                if(account == null) {
                    break;
                }
                return count(entryType, pageTab.name() + " = " + account);
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
    public Cursor query(EntryType type, String key) {
        return db.rawQuery("SELECT * FROM " + type.name() + " WHERE " + type.primaryKey().name() + " = " + key, null);
    }

    public Cursor query(EntryType type, int key) {
        return query(type, key + "");
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

    public Cursor query(EntryType type, String selection, String[] selectionArgs, String orderBy) {
        return db.query(true, type.name(), type.getColumnNames(), selection,
                selectionArgs, null, null, orderBy, null);
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
     * Query entries by given pageTab
     * @param pageTab
     * @param account
     * @param order
     * @param orderType
     * @return
     */
    public Cursor query(IPageTab pageTab, String account, IColumn order, OrderType orderType) {
        EntryType entryType = pageTab.getEntryType();
        switch (entryType) {
            case Todo:
                Todo.PageTab todoTab = (Todo.PageTab) pageTab;
                switch (todoTab) {
                    case undone:
                        return query(entryType, TodoColumn.account + " = ? AND "
                                + TodoColumn.status.name() +  " IS NOT ?",
                                new String[]{account, Todo.Status.done.name()}, order.name() + " " + orderType.name());
                    case done:
                        return query(entryType, TodoColumn.account + " = ? AND "
                                + TodoColumn.status.name() +  " =?",
                                new String[]{account, Todo.Status.done.name()}, order.name() + " " + orderType.name());
                }
                break;
            case Task:
                if(account == null) break;
                Task.PageTab taskTab = (Task.PageTab) pageTab;
                switch (taskTab) {
                    case assignedTo:
                        return query(entryType, TaskColumn.assignedTo.name() +  " =?", new String[]{account}, order.name() + " " + orderType.name());
                    case openedBy:
                        return query(entryType, TaskColumn.openedBy.name() +  " =?", new String[]{account}, order.name() + " " + orderType.name());
                    case finishedBy:
                        return query(entryType, TaskColumn.finishedBy.name() +  " =?", new String[]{account}, order.name() + " " + orderType.name());
                }
                break;
            case Bug:
                if(account == null) break;
                switch ((Bug.PageTab) pageTab) {
                    case assignedTo:
                        return query(entryType, BugColumn.assignedTo.name() + " =?", new String[]{account}, order.name() + " " + orderType.name());
                    case openedBy:
                        return query(entryType, BugColumn.openedBy.name() +  " =?", new String[]{account}, order.name() + " " + orderType.name());
                    case resolvedBy:
                        return query(entryType, BugColumn.resolvedBy.name() +  " =?", new String[]{account}, order.name() + " " + orderType.name());
                }
                break;
            case Story:
                if(account == null) break;
                switch ((Story.PageTab) pageTab) {
                    case assignedTo:
                        return query(entryType, StoryColumn.assignedTo.name() +  " =?", new String[]{account}, order.name() + " " + orderType.name());
                    case openedBy:
                        return query(entryType, StoryColumn.openedBy.name() +  " =?", new String[]{account}, order.name() + " " + orderType.name());
                    case reviewedBy:
                        return query(entryType, StoryColumn.reviewedBy.name() +  " =?", new String[]{account}, order.name() + " " + orderType.name());
                }
                break;
        }
        return null;
    }

    /**
     * Query entries by given pageTab
     * @param pageTab
     * @param user
     * @param order
     * @return
     */
    public Cursor query(IPageTab pageTab, String user, IColumn order) {
        return query(pageTab, user, order, OrderType.ASC);
    }

    /**
     * Query entries by given pageTab
     * @param pageTab
     * @param user
     * @return
     */
    public Cursor query(IPageTab pageTab, String user) {
        return query(pageTab, user, pageTab.getEntryType().defaultOrderColumn(), OrderType.DESC);
    }

    /**
     * Get summery
     * @param entryType
     * @return
     */
    public HashMap<String, String> getSummery(EntryType entryType, String account) {
        long number = 0;
        long unread = 0;
        String newest = "";
        Cursor cursor;
        switch (entryType) {
            case Todo:
                cursor = query(Todo.PageTab.undone, account, TodoColumn._id, OrderType.DESC);
                number = cursor != null ? cursor.getCount() : 0;
                if(number > 0 && cursor.moveToNext()) {
                    Todo todo = new Todo(cursor);
                    unread = todo.isUnread() ? 1 : 0;
                    newest = "#" + todo.key() + " " + todo.getAsString(TodoColumn.name);
                    while (cursor.moveToNext()) {
                        if(cursor.getLong(cursor.getColumnIndex(TodoColumn.unread.name())) > 0) {
                            unread++;
                        }
                    }
                }
                break;
            case Task:
                cursor = query(Task.PageTab.assignedTo, account, TaskColumn._id, OrderType.DESC);
                number = cursor != null ? cursor.getCount() : 0;
                if(number > 0 && cursor.moveToNext()) {
                    Task task = new Task(cursor);
                    unread = task.isUnread() ? 1 : 0;
                    newest = "#" + task.key() + " " + task.getAsString(TaskColumn.name);
                    while (cursor.moveToNext()) {
                        if(cursor.getLong(cursor.getColumnIndex(TaskColumn.unread.name())) > 0) {
                            unread++;
                        }
                    }
                }
                break;
            case Bug:
                cursor = query(Bug.PageTab.assignedTo, account, BugColumn._id, OrderType.DESC);
                number = cursor != null ? cursor.getCount() : 0;
                if(number > 0 && cursor.moveToNext()) {
                    Bug bug = new Bug(cursor);
                    unread = bug.isUnread() ? 1 : 0;
                    newest = "#" + bug.key() + " " + bug.getAsString(BugColumn.title);
                    while (cursor.moveToNext()) {
                        if(cursor.getLong(cursor.getColumnIndex(BugColumn.unread.name())) > 0) {
                            unread++;
                        }
                    }
                }
                break;
            case Story:
                cursor = query(Story.PageTab.assignedTo, account, StoryColumn._id, OrderType.DESC);
                number = cursor != null ? cursor.getCount() : 0;
                if(number > 0 && cursor.moveToNext()) {
                    Story story = new Story(cursor);
                    unread = story.isUnread() ? 1 : 0;
                    newest = "#" + story.key() + " " + story.getAsString(StoryColumn.title);
                    while (cursor.moveToNext()) {
                        if(cursor.getLong(cursor.getColumnIndex(StoryColumn.unread.name())) > 0) {
                            unread++;
                        }
                    }
                }
                break;
        }


        HashMap<String, String> summery = new HashMap<>(2);
        summery.put("count", number + "");
        summery.put("newest", newest);
        summery.put("unread", unread + "");
        return summery;
    }

    public HashMap<EntryType, HashMap<String, String>> getSummery(String account) {
        EntryType[] types = EntryType.values();
        HashMap<EntryType, HashMap<String, String>> summeries = new HashMap<>(types.length);
        for(EntryType entryType: types) {
            summeries.put(entryType, getSummery(entryType, account));
        }
        return summeries;
    }

    /**
     * Close database
     */
    public void close() {
        db.close();
    }
}
