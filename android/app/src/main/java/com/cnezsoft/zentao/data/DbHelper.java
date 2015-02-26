package com.cnezsoft.zentao.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.cnezsoft.zentao.ZentaoApplication;

/**
 * Database helpers
 *
 * Created by Catouse on 2015/1/15.
 */
public class DbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 28;
    public static final String DATABASE_NAME = "com.cnezsoft.zentao.db.";

    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context to use to open or create the database
     */
    public DbHelper(Context context) {
        super(context, DATABASE_NAME + ((ZentaoApplication) context.getApplicationContext()).getUser().getAddressIdentify(), null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        for(String sql: SQLHelper.getCreateDatabaseSql()) {
            Log.v("DBHELPER", "sql:" + sql);
            db.execSQL(sql);
        }
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p/>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        for(String sql: SQLHelper.getDeleteDatabaseSql()) {
            db.execSQL(sql);
        }
        onCreate(db);
    }
}
