package com.cnezsoft.zentao.data;

/**
 * Created by Catouse on 2015/1/15.
 */
public class SQLHelper {
    public static String getCreateDatatableSql()
    {
        String sql = "";
        for(EntryType type: EntryType.values()) {
            sql += getCreateTableSql(type);
        }
        return sql;
    }

    public static String getDeleteDatatableSql()
    {
        String sql = "";
        for(EntryType type: EntryType.values()) {
            sql += getDeleteTableSql(type);
        }
        return sql;
    }

    public static String getDeleteTableSql(EntryType type)
    {
        return "DROP TABLE IF EXISTS " + type.name();
    }

    public static String getCreateTableSql(EntryType type)
    {
        String sql = "";
        IColumn[] columns = type.getColumns();
        if(columns != null) {
            sql = "CREATE TABLE " + type.name() + " (";
            for(IColumn column: type.getColumns()) {
                sql += column.name() + " "
                        + column.type().sqlType().name() + " "
                        + (column.isPrimaryKey() ? "PRIMARY KEY " : "") + ", ";
            }
            sql = sql.substring(0, sql.length() - 2) + ");";
        }
        return sql;
    }
}
