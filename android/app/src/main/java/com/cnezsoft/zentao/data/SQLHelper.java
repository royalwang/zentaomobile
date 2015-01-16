package com.cnezsoft.zentao.data;

/**
 * Created by Catouse on 2015/1/15.
 */
public class SQLHelper {

    public static final String TODO_TABLE_NAME = "todo";
    public static final String CREATE_TODO_TABLE =
            "CREATE TABLE " + TODO_TABLE_NAME + " (" +
                    "id INTEGER PRIMARY KEY, " +
                    "pri INTEGER" +
                    "status INTEGER" +
                    "begin INTEGER, " +
                    "end INTEGER" +
                    "type TEXT" +
                    "name TEXT" +
                    ");";
    public static final String DELETE_TODO_TABLE =
            "DROP TABLE IF EXISTS " + TODO_TABLE_NAME;

    public static final String CREATE_DATABASE = CREATE_TODO_TABLE;

    public static final String DELETE_DATABASE = DELETE_TODO_TABLE;
}
