package com.cnezsoft.zentao.data;

import android.content.Context;

import com.cnezsoft.zentao.ZentaoApplication;

/**
 * Created by Catouse on 2015/1/30.
 */
public enum TaskColumn implements IColumn{
    _id(DataType.INT, false),
    project(DataType.INT),
    module(DataType.INT),
    story(DataType.INT),
    fromBug(DataType.INT),
    name(DataType.STRING),
    type(DataType.ENUM),
    pri(DataType.INT),
    estimate(DataType.FLOAT),    // 估计时间
    consumed(DataType.FLOAT),    // 消耗时间
    left(DataType.FLOAT),        // 剩余时间
    deadline(DataType.DATETIME),
    status(DataType.ENUM),
    openedBy(DataType.STRING),
    openedDate(DataType.DATETIME),
    assignedTo(DataType.STRING),
    assignedDate(DataType.DATETIME),
    estStarted(DataType.DATETIME), // 计划开始
    realStarted(DataType.DATETIME),// 时间开始
    finishedBy(DataType.STRING),
    finishedDate(DataType.DATETIME),
    canceledBy(DataType.STRING),
    canceledDate(DataType.DATETIME),
    closedBy(DataType.STRING),
    closedDate(DataType.DATETIME),
    lastEditedBy(DataType.STRING),
    lastEditedDate(DataType.DATETIME),

    // below for detail
    desc(DataType.STRING),
    doc(DataType.STRING),
    mailto(DataType.STRING),
    closeReason(DataType.ENUM),
    storyVersion(DataType.INT);

    private final DataType dataType;
    private boolean isNullable = true;

    TaskColumn(DataType dataType, boolean isNullable) {
        this.dataType = dataType;
        this.isNullable = isNullable;
    }

    TaskColumn(DataType dataType) {
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
}
