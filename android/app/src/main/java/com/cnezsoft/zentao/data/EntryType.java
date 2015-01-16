package com.cnezsoft.zentao.data;


public enum EntryType {
    Unknown,
    Product,
    Project,
    Todo,
    Task,
    Story,
    Bug;

    private IColumn[] columns = null;

    public IColumn[] getColumns()
    {
        if(columns == null)
        {
            switch (this)
            {
                case Todo:
                    columns = TodoColumn.values();
                    break;
            }
        }
        return columns;
    }
}