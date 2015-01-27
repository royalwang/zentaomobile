package com.cnezsoft.zentao.data;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Todo database access object
 *
 * Created by Catouse on 2015/1/16.
 */
public class TodoDAO extends DAO {
    /**
     * Constructor with context
     * @param context
     */
    public TodoDAO(Context context) {
        super(context);
    }

    /**
     * Query todos
     * @param pageTab
     * @param order
     * @return
     */
    public Cursor query(Todo.PageTab pageTab, Todo.Order order) {
        return query(EntryType.Todo, TodoColumn.status.name() + "=?", new String[]{pageTab.name()}, order.name() + " DESC");
    }

    /**
     * Query all todos
     * @return
     */
    public Collection<Todo> query() {
        Collection<Todo> todos = new ArrayList<Todo>();
        Cursor cursor = query(EntryType.Todo);
        Todo todo = null;
        while (cursor.moveToNext()) {
            todo = new Todo(cursor);
            todos.add(todo);
        }
        cursor.close();

        return todos;
    }

    /**
     * Get todo by key
     * @param key
     * @return
     */
    public Todo get(String key) {
        Cursor cursor = queryByKey(EntryType.Todo, key);
        if(cursor.moveToNext()) {
            return new Todo(cursor);
        }
        return null;
    }
}
