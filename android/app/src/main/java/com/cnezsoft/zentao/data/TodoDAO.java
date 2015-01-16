package com.cnezsoft.zentao.data;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Catouse on 2015/1/16.
 */
public class TodoDAO extends DAO {
    public TodoDAO(Context context) {
        super(context);
    }

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
}
