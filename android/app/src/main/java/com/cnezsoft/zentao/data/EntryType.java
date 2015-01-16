package com.cnezsoft.zentao.data;


import java.util.ArrayList;

public enum EntryType {
    Unknown,
    Product,
    Project,
    Todo,
    Task,
    Story,
    Bug;

    private IColumn[] cols = null;
    private IColumn primaryColumn = null;
    private String[] columnNames = null;

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

    public boolean containsColumn(IColumn column) {
        for(IColumn col: columns()) {
            if(column == col) {
                return true;
            }
        }
        return false;
    }

    public boolean containsColumn(String name) {
        for(String colName: getColumnNames()) {
            if(name.equals(colName)) {
                return true;
            }
        }
        return false;
    }
}