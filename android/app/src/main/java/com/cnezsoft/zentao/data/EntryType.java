package com.cnezsoft.zentao.data;


import android.content.Context;

import com.cnezsoft.zentao.IAccentIcon;
import com.cnezsoft.zentao.ZentaoApplication;
import com.cnezsoft.zentao.colorswatch.MaterialColorSwatch;

import java.util.ArrayList;

/**
 * Entry type
 */
public enum EntryType implements IAccentIcon{
    Default(MaterialColorSwatch.Grey, "question"),
    Todo(MaterialColorSwatch.LightBlue, "check-square-o"),
    Task(MaterialColorSwatch.Green, "tasks"),
    Bug(MaterialColorSwatch.Pink, "bug"),
    Story(MaterialColorSwatch.Purple, "lightbulb-o"),
    Project(MaterialColorSwatch.Indigo, "folder-o"),
    Product(MaterialColorSwatch.Teal, "cube");

    private IColumn[] cols = null;
    private IColumn primaryColumn = null;
    private IPageTab[] pageTabs = null;
    private String[] columnNames = null;

    EntryType(MaterialColorSwatch accentColor, String iconName) {
        this.accentColor = accentColor;
        this.iconName = iconName;
    }

    private MaterialColorSwatch accentColor;

    public MaterialColorSwatch accent() {
        return accentColor;
    }

    private String iconName;

    public String icon() {
        return iconName;
    }

    /**
     * Get entry type from name
     * @param name
     * @return
     */
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

    public String text(Context context) {
        return ZentaoApplication.getEnumText(context, this);
    }

    /**
     * Get primary key
     * @return
     */
    public IColumn primaryKey() {
        IColumn[] cs = columns();
        if(cs == null) return null;

        if(primaryColumn == null) {
            for(IColumn column: cs) {
                if(column.isPrimaryKey()) {
                    primaryColumn = column;
                    break;
                }
            }
        }
        return primaryColumn;
    }

    /**
     * Get column by name
     * @param name
     * @return
     */
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
                case Task:
                    cols = TaskColumn.values();
                    break;
                case Bug:
                    cols = BugColumn.values();
                    break;
                case Story:
                    cols = StoryColumn.values();
                    break;
                case Project:
                    cols = ProjectColumn.values();
                    break;
                case Product:
                    cols = ProductColumn.values();
                    break;
            }
        }
        return cols;
    }

    /**
     * Get default column for order
     * @return
     */
    public IColumn defaultOrderColumn() {
        switch (this)
        {
            case Todo:
                return TodoColumn._id;
            case Task:
                return TodoColumn._id;
            case Bug:
                return TodoColumn._id;
            case Story:
                return TodoColumn._id;
            case Product:
                return ProductColumn._id;
            case Project:
                return ProjectColumn._id;
        }
        return null;
    }

    /**
     * get all page tabs
     * @return
     */
    public IPageTab[] tabs() {
        if(pageTabs == null)
        {
            switch (this)
            {
                case Todo:
                    pageTabs = com.cnezsoft.zentao.data.Todo.PageTab.values();
                    break;
                case Task:
                    pageTabs =  com.cnezsoft.zentao.data.Task.PageTab.values();
                    break;
                case Bug:
                    pageTabs =  com.cnezsoft.zentao.data.Bug.PageTab.values();
                    break;
                case Story:
                    pageTabs =  com.cnezsoft.zentao.data.Story.PageTab.values();
                    break;
            }
        }
        return pageTabs;
    }

    /**
     * Get PageTab by name
     * @param name
     * @return
     */
    public IPageTab getTab(String name) {
        for(IPageTab pageTab: tabs()) {
            if(pageTab.name().equals(name)) {
                return pageTab;
            }
        }
        return null;
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