package com.cnezsoft.zentao.data;

/**
 * Created by Catouse on 2015/1/15.
 */
public class SQLHelper {
    /**
     * Get create database sql
     * @return
     */
    public static String getCreateDatabaseSql()
    {
        String sql = "";
        for(EntryType type: EntryType.values()) {
            sql += getCreateTableSql(type);
        }
        return sql;
    }

    /**
     * Get delete database sql
     * @return
     */
    public static String getDeleteDatabaseSql()
    {
        String sql = "";
        for(EntryType type: EntryType.values()) {
            sql += getDeleteTableSql(type);
        }
        return sql;
    }

    /**
     * Get delete table sql
     * @param type
     * @return
     */
    public static String getDeleteTableSql(EntryType type)
    {
        return "DROP TABLE IF EXISTS " + type.name();
    }

    /**
     * Get create table sql
     * @param type
     * @return
     */
    public static String getCreateTableSql(EntryType type)
    {
        String sql = "";
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
