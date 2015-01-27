package com.cnezsoft.zentao.data;

import java.util.ArrayList;

/**
 * Created by Catouse on 2015/1/15.
 */
public class SQLHelper {
    /**
     * Get create database sql
     * @return
     */
    public static ArrayList<String> getCreateDatabaseSql()
    {
        ArrayList<String> sqls = new ArrayList<>();
        for(EntryType type: EntryType.values()) {
            if(type == EntryType.Default) continue;
            String sql = getCreateTableSql(type);
            if(sql != null) {
                sqls.add(getCreateTableSql(type));
            }
        }
        return sqls;
    }

    /**
     * Get delete database sql
     * @return
     */
    public static ArrayList<String> getDeleteDatabaseSql()
    {
        ArrayList<String> sqls = new ArrayList<>();
        for(EntryType type: EntryType.values()) {
            if(type == EntryType.Default) continue;
            sqls.add(getDeleteTableSql(type));
        }
        return sqls;
    }

    /**
     * Get delete table sql
     * @param type
     * @return
     */
    public static String getDeleteTableSql(EntryType type)
    {
        return "DROP TABLE IF EXISTS " + type.name() + ";";
    }

    /**
     * Get create table sql
     * @param type
     * @return
     */
    public static String getCreateTableSql(EntryType type)
    {
        String sql = null;
        IColumn[] columns = type.columns();
        if(columns != null) {
            sql = "CREATE TABLE " + type.name() + " (";
            for(IColumn column: type.columns()) {
                sql += column.name() + " "
                        + column.type().sqlType().name() + " "
                        + (column.isPrimaryKey() ? "PRIMARY KEY " : "")
                        + (column.nullable() ? "" : "NOT NULL ") + ", ";
            }
            sql = sql.substring(0, sql.length() - 2) + ");";
        }
        return sql;
    }
}
