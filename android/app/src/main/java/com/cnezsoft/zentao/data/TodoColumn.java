package com.cnezsoft.zentao.data;

import android.content.Context;

import com.cnezsoft.zentao.ZentaoApplication;

/**
 * Todo columns
 */
public enum TodoColumn implements IColumn {
    _id(DataType.INT, false),
    pri(DataType.INT),
    begin(DataType.DATETIME),
    end(DataType.DATETIME),
    type(DataType.ENUM),
    name(DataType.STRING),
    status(DataType.ENUM),
    idvalue(DataType.INT),
    desc(DataType.STRING),

    lastSyncTime(DataType.DATETIME);

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
     * Judge the column whether is the primary key
     * @return
     */
    @Override
    public Boolean isPrimaryKey() {
        return this == _id;
    }

    private boolean isNullable = true;

    /**
     * Judge the column whether is nullable
     * @return
     */
    @Override
    public boolean nullable() {
        return isNullable;
    }

    /**
     * Get the column index
     * @return
     */
    @Override
    public int index() {
        return this.ordinal();
    }

    private final DataType dataType;

    /**
     * Get data ;type
     * @return
     */
    @Override
    public DataType type() {
        return dataType;
    }

    TodoColumn(DataType dataType, boolean isNullable) {
        this.dataType = dataType;
        this.isNullable = isNullable;
    }
    TodoColumn(DataType dataType) {
        this.dataType = dataType;
    }

    /**
     * Get primary column
     * @return
     */
    public static TodoColumn primary() {
        for(TodoColumn column: values()) {
            if(column.isPrimaryKey()) {
                return column;
            }
        }
        return null;
    }
}
