package com.cnezsoft.zentao.data;

import android.content.Context;

import com.cnezsoft.zentao.ZentaoApplication;

/**
 * Created by Catouse on 2015/2/2.
 */
public enum BugColumn implements IColumn {
    _id(DataType.INT, false),
    product(DataType.INT),
    module(DataType.INT),
    project(DataType.INT),
    plan(DataType.INT),
    story(DataType.INT),
    task(DataType.INT),
    title(DataType.STRING),
    keywords(DataType.STRING),
    severity(DataType.INT),
    pri(DataType.INT),
    type(DataType.ENUM),
    os(DataType.ENUM),
    browser(DataType.ENUM),
    found(DataType.STRING),
    status(DataType.ENUM),
    confirmed(DataType.BOOLEAN),
    openedBy(DataType.STRING),
    openedDate(DataType.DATETIME),
    assignedTo(DataType.STRING),
    assignedDate(DataType.DATETIME),
    resolvedBy(DataType.STRING),
    resolution(DataType.ENUM),
    resolvedDate(DataType.DATETIME),
    closedBy(DataType.STRING),
    closedDate(DataType.DATETIME),
    lastEditedBy(DataType.STRING),
    lastEditedDate(DataType.DATETIME),
    deleted(DataType.BOOLEAN),

    // below for detail
    hardware(DataType.STRING),
    activatedCount(DataType.INT),
    duplicateBug(DataType.STRING),
    resolvedBuild(DataType.STRING),
    openedBuild(DataType.STRING),
    mailto(DataType.STRING),
    toTask(DataType.INT),
    toStory(DataType.INT),
    storyVersion(DataType.INT),
    steps(DataType.STRING),

    lastSyncTime(DataType.DATETIME);

    private final DataType dataType;
    private boolean isNullable = true;

    BugColumn(DataType dataType, boolean isNullable) {
        this.dataType = dataType;
        this.isNullable = isNullable;
    }

    BugColumn(DataType dataType) {
        this.dataType = dataType;
    }

    @Override
    public String text(Context context) {
        return ZentaoApplication.getEnumText(context, this);
    }

    /**
     * Get data type
     *
     * @return
     */
    @Override
    public DataType type() {
        return dataType;
    }

    /**
     * Get index
     *
     * @return
     */
    @Override
    public int index() {
        return this.ordinal();
    }

    /**
     * Judge the column is primary key
     *
     * @return
     */
    @Override
    public Boolean isPrimaryKey() {
        return this == _id;
    }

    /**
     * Judge the column is nullable
     *
     * @return
     */
    @Override
    public boolean nullable() {
        return isNullable;
    }

    /**
     * Get the primary column
     * @return
     */
    public static BugColumn primary() {
        for(BugColumn column: values()) {
            if(column.isPrimaryKey()) {
                return column;
            }
        }
        return null;
    }
}
