package com.cnezsoft.zentao.data;

import android.content.Context;

import com.cnezsoft.zentao.ZentaoApplication;

/**
 * Product columns
 * Created by Catouse on 2015/2/26.
 */
public enum ProductColumn implements IColumn {
    _id(DataType.INT, false),
    name(DataType.STRING),
    code(DataType.STRING),
    status(DataType.ENUM),
    desc(DataType.STRING),
    PO(DataType.STRING),
    QD(DataType.STRING),
    RD(DataType.STRING),
    acl(DataType.ENUM),
    whitelist(DataType.STRING),
    createdBy(DataType.STRING),
    createdVersion(DataType.STRING),
    createdDate(DataType.DATETIME),
    deleted(DataType.BOOLEAN);

    private final DataType dataType;
    private boolean isNullable = true;

    ProductColumn(DataType dataType, boolean isNullable) {
        this.dataType = dataType;
        this.isNullable = isNullable;
    }

    ProductColumn(DataType dataType) {
        this.dataType = dataType;
    }

    /**
     * Get text string
     *
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
        return ordinal();
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
    public static ProductColumn primary() {
        for(ProductColumn column: values()) {
            if(column.isPrimaryKey()) {
                return column;
            }
        }
        return null;
    }
}
