package com.cnezsoft.zentao.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

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
}
