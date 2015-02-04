package com.cnezsoft.zentao.data;

import android.content.Context;

import com.cnezsoft.zentao.ZentaoApplication;

/**
 * Story columns
 * Created by Catouse on 2015/2/2.
 */
public enum StoryColumn implements IColumn{
    _id(DataType.INT, false),
    product(DataType.INT),
    module(DataType.INT),
    plan(DataType.INT),
    source(DataType.ENUM),
    fromBug(DataType.INT),
    title(DataType.STRING),
    keywords(DataType.STRING),
    pri(DataType.INT),
    estimate(DataType.FLOAT),
    status(DataType.ENUM),
    stage(DataType.ENUM),
    openedBy(DataType.STRING),
    openedDate(DataType.DATETIME),
    assignedTo(DataType.STRING),
    assignedDate(DataType.DATETIME),
    lastEditedBy(DataType.STRING),
    lastEditedDate(DataType.DATETIME),
    reviewedBy(DataType.STRING),
    reviewedDate(DataType.DATETIME),
    closedBy(DataType.STRING),
    closedDate(DataType.DATETIME),
    closedReason(DataType.ENUM),
    version(DataType.INT),
    deleted(DataType.BOOLEAN),

    // Below for detail
    duplicateStory(DataType.INT),
    toBug(DataType.INT),
    mailto(DataType.STRING),
    spec(DataType.HTML),
    verify(DataType.HTML),

    lastSyncTime(DataType.DATETIME);

    private final DataType dataType;
    private boolean isNullable = true;

    StoryColumn(DataType dataType, boolean isNullable) {
        this.dataType = dataType;
        this.isNullable = isNullable;
    }

    StoryColumn(DataType dataType) {
        this.dataType = dataType;
    }

    /**
     * Get text
     * @param context
     * @return
     */
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
    public static StoryColumn primary() {
        for(StoryColumn column: values()) {
            if(column.isPrimaryKey()) {
                return column;
            }
        }
        return null;
    }
}
