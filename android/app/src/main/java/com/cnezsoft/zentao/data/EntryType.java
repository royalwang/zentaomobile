package com.cnezsoft.zentao.data;


import java.util.ArrayList;

/**
 * Entry type
 */
public enum EntryType {
    Default,
    Product,
    Project,
    Todo,
    Task,
    Story,
    Bug;

    private IColumn[] cols = null;
    private IColumn primaryColumn = null;
    private String[] columnNames = null;

    public static EntryType fromName(String name) {
        name = name.toLowerCase();
        for(EntryType entryType: EntryType.values()) {
            if(name.equals(entryType.name().toLowerCase())) {
                return entryType;
            }
        }
        return null;
    }

    /**
     * Get all columns names
     * @return
     */
    public String[] getColumnNames() {
        if(columnNames == null) {
            ArrayList<String> names = new ArrayList<>();
            for(IColumn column: columns()) {
                names.add(column.name());
            }
            columnNames = names.toArray(new String[names.size()]);
        }
        return columnNames;
    }

    /**
     * Get primary key
     * @return
     */
    public IColumn primaryKey() {
        if(primaryColumn == null) {
            for(IColumn column: columns()) {
                if(column.isPrimaryKey()) {
                    primaryColumn = column;
                    break;
                }
            }
        }
        return primaryColumn;
    }

    public IColumn getColumn(String name) {
        for(IColumn column: columns()) {
            if(column.name().equals(name)) {
                return column;
            }
        }
        return null;
    }

    /**
     * Get all columns
     * @return
     */
    public IColumn[] columns() {
        if(cols == null)
        {
            switch (this)
            {
                case Todo:
                    cols = TodoColumn.values();
                    break;
            }
        }
        return cols;
    }

    /**
     * Judge the entry whether has the given column
     * @param column
     * @return
     */
    public boolean containsColumn(IColumn column) {
        for(IColumn col: columns()) {
            if(column.equals(col)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Judge the entry whether has the column with given column name
     * @param name
     * @return
     */
    public boolean containsColumn(String name) {
        for(String colName: getColumnNames()) {
            if(name.equals(colName)) {
                return true;
            }
        }
        return false;
    }
}