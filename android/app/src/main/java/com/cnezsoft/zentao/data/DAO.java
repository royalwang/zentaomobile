package com.cnezsoft.zentao.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;

import java.util.ArrayList;
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
     * Close database
     */
    public void close() {
        db.close();
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
//    public boolean update(Collection<DataEntry> entries) {
//        boolean result = false;
//        db.beginTransaction();
//        try {
//            for(DataEntry entry: entries) {
//                update(entry);
//            }
//            db.setTransactionSuccessful();
//            result = true;
//        } finally {
//            db.endTransaction();
//        }
//        return result;
//    }

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
    public DAOResult save(ArrayList<DataEntry> entries, boolean markNewUnread) {
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

        if(entries.size() == 1 && result.sum(EntryType.Todo) > 0) {
            if(correctTodo(entries.get(0).key())) {
                result.setCorrect(EntryType.Todo, 1);
            }
        }
        else if(result.getUpdate(EntryType.Todo) > 0 || result.getAdd(EntryType.Todo) > 0) {
            result.setCorrect(EntryType.Todo, correctTodo());
        }

        return result;
    }

    public DAOResult save(ArrayList<DataEntry> entries) {
        return save(entries, false);
    }

    /**
     * Correct todoes witch the type is not custom
     */
    public int correctTodo() {
        Cursor cursor = query(EntryType.Todo, TodoColumn.type.name() + " IS NOT ?", new String[]{Todo.Types.custom.name()});
        int count = 0;
        while (cursor.moveToNext()) {
            if(correctTodo(cursor)) count++;
        }
        return count;
    }

    public boolean correctTodo(String key) {
        Cursor cursor = query(EntryType.Todo, key);
        if(cursor.moveToNext()) {
            return correctTodo(cursor);
        }
        return false;
    }

    public boolean correctTodo(Cursor cursor) {
        return correctTodo(new Todo(cursor));
    }

    public boolean correctTodo(Todo todo) {
        Cursor entryCursor;
        boolean needUpdate = false;
        switch (todo.getTodoType()) {
            case task:
                entryCursor = query(EntryType.Task, todo.getAsInteger(TodoColumn.idvalue));
                if(entryCursor.moveToNext()) {
                    todo.put(TodoColumn.name, entryCursor.getString(entryCursor.getColumnIndex(TaskColumn.name.name())));
                    needUpdate = true;
                }
                break;
            case bug:
                entryCursor = query(EntryType.Bug, todo.getAsInteger(TodoColumn.idvalue));
                if(entryCursor.moveToNext()) {
                    todo.put(TodoColumn.name, entryCursor.getString(entryCursor.getColumnIndex(BugColumn.title.name())));
                    needUpdate = true;
                }
                break;
        }
        if(needUpdate) {
            update(todo);
        }
        return needUpdate;
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
//    public boolean delete(Collection<DataEntry> entries) {
//        boolean result = false;
//        if(entries != null) {
//            db.beginTransaction();
//            try {
//                for(DataEntry entry: entries) {
//                    delete(entry);
//                }
//                db.setTransactionSuccessful();
//                result = true;
//            } finally {
//                db.endTransaction();
//            }
//        }
//        return result;
//    }

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

//    public long count(IPageTab pageTab, String account) {
//        EntryType entryType = pageTab.getEntryType();
//        switch (entryType) {
//            case Todo:
//                Todo.PageTab todoTab = (Todo.PageTab) pageTab;
//                switch (todoTab) {
//                    case undone:
//                        return count(entryType, TodoColumn.account + " = " + account + " AND " + TodoColumn.status.name() + " IS NOT " + Todo.Status.done.name());
//                    case done:
//                        return count(entryType, TodoColumn.account + " = " + account + " AND " + TodoColumn.status.name() +  " = " + Todo.Status.done.name());
//                }
//                break;
//            case Task:
//            case Bug:
//            case Story:
//                if(account == null) {
//                    break;
//                }
//                return count(entryType, pageTab.name() + " = " + account);
//        }
//        return 0;
//    }

    /**
     * Judge the database whether has the entry with the given key
     * @param type
     * @param key
     * @return
     */
    public boolean contains(EntryType type, String key) {
        Cursor cursor = db.rawQuery("SELECT count(*) FROM " + type.name()
                + " WHERE " + type.primaryKey() + " = " + key, null);
        boolean result = false;
        if (cursor.moveToNext()) {
            result = cursor.getLong(0) > 0;
        }
        cursor.close();
        return result;
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

    public Cursor query(EntryType type, String selection, String[] selectionArgs) {
        return db.query(true, type.name(), type.getColumnNames(), selection,
                selectionArgs, null, null, null, null);
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
    public HashMap<String, Object> getSummery(EntryType entryType, String account) {
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
            case Project:
                cursor = query(EntryType.Project, null, null, ProjectColumn._id.name() + " DESC");
                number = cursor != null ? cursor.getCount() : 0;
                if(number > 0 && cursor.moveToNext()) {
                    Project project = new Project(cursor);
                    newest = "#" + project.key() + " " + project.getAsString(ProjectColumn.name);
                    if(project.getStatus() == Project.Status.doing) {
                        unread++;
                    }
                    String statusDoingName = Project.Status.doing.name();
                    while (cursor.moveToNext()) {
                        if(cursor.getString(cursor.getColumnIndex(StoryColumn.status.name())).equals(statusDoingName)) {
                            if(unread < 1) {
                                project = new Project(cursor);
                                newest = "#" + project.key() + " " + project.getAsString(ProjectColumn.name);
                            }
                            unread++;
                        }
                    }
                }
                break;
            case Product:
                cursor = query(EntryType.Product, null, null, ProductColumn._id.name() + " DESC");
                number = cursor != null ? cursor.getCount() : 0;
                if(number > 0 && cursor.moveToNext()) {
                    Product product = new Product(cursor);
                    newest = "#" + product.key() + " " + product.getAsString(ProjectColumn.name);
                    if(product.getStatus() == Product.Status.normal) {
                        unread++;
                    }
                    String statusNormalName = Product.Status.normal.name();
                    while (cursor.moveToNext()) {
                        if(cursor.getString(cursor.getColumnIndex(StoryColumn.status.name())).equals(statusNormalName)) {
                            if(unread < 1) {
                                product = new Product(cursor);
                                newest = "#" + product.key() + " " + product.getAsString(ProductColumn.name);
                            }
                            unread++;
                        }
                    }
                }
                break;
        }

        HashMap<String, Object> summery = new HashMap<>(2);
        summery.put("type", entryType);
        summery.put("count", number);
        summery.put("newest", newest);
        summery.put("newCount", unread);
        return summery;
    }

    /**
     * Get all type summeries
     * @param account
     * @return
     */
    public ArrayList<HashMap<String, Object>> getSummery(String account) {
        EntryType[] types = EntryType.values();
        ArrayList<HashMap<String, Object>> summeries = new ArrayList<>(types.length - 1);
        for(EntryType entryType: types) {
            if(entryType != EntryType.Default) {
                summeries.add(getSummery(entryType, account));
            }
        }
        return summeries;
    }

    public long getBugCountOfProduct(String productID) {
        return count(EntryType.Bug,
                BugColumn.product.name() + " = " + productID + " AND " + BugColumn.status.name() + " = '" + Bug.Status.active.name() + "'");
    }

    public long getBugCountOfProject(String projectID) {
        return count(EntryType.Bug,
                BugColumn.project.name() + " = " + projectID + " AND " + BugColumn.status.name() + " = '" + Bug.Status.active.name() + "'");
    }

    public long getStoryCountOfProduct(String productID, Story.Status status) {
        return count(EntryType.Story,
                StoryColumn.product.name() + " = " + productID + " AND " + StoryColumn.status.name() + " = '" + status.name() + "'");
    }

    public Cursor getProjectTasks(String id) {
        return query(EntryType.Task, TaskColumn.project.name() + " = ?", new String[]{id});
    }

    public ArrayList<HashMap<String, Object>> getProjectsList(boolean simpleSet) {
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();
        Cursor cursor = query(EntryType.Project);
        Project project;
        while (cursor.moveToNext()) {
            project = new Project(cursor);
            if(simpleSet && project.isDoneLongTimeAgo()) {
                continue;
            }

            project.calculateHours(getProjectTasks(project.key()));
            HashMap<String, Object> item = new HashMap<>();
            item.put("title", project.getAsString(ProjectColumn.name));
            item.put("id", project.getAsInteger(ProjectColumn._id));
            item.put("date", project.getFriendlyTimeString(context));
            item.put("bugCount", getBugCountOfProject(project.key()));
            item.put("progress", project.getProgress());
            item.put("hours", project.getHour());
            item.put("status", project.getStatus());
            list.add(item);
        }
        return list;
    }

    public ArrayList<HashMap<String, Object>> getProductsList(boolean simpleSet) {
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();
        Cursor cursor = simpleSet ?
                query(EntryType.Product, ProductColumn.status.name() + " = ?", new String[]{Product.Status.normal.name()})
                : query(EntryType.Product);
        Product product;
        while (cursor.moveToNext()) {
            product = new Product(cursor);
            int id = product.getAsInteger(ProductColumn._id);
            String idStr = id + "";
            HashMap<String, Object> item = new HashMap<>();
            item.put("bugCount", getBugCountOfProduct(idStr));
            item.put("title", product.getAsString(ProductColumn.name));
            item.put("id", id);
            item.put("status", product.getStatus());
            item.put("storyCount", getStoryCountOfProduct(idStr, Story.Status.active));
            item.put("changedCount", getStoryCountOfProduct(idStr, Story.Status.changed));
            item.put("draftCount", getStoryCountOfProduct(idStr, Story.Status.draft));
            list.add(item);
        }
        return list;
    }
}
