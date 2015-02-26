package com.cnezsoft.zentao.data;

import android.content.Context;

import com.cnezsoft.zentao.ZentaoApplication;

/**
 * Project columns
 * Created by Catouse on 2015/2/26.
 */
public enum ProjectColumn implements IColumn {
    _id(DataType.INT, false),
    isCat(DataType.BOOLEAN),
    catID(DataType.INT),
    type(DataType.ENUM),
    parent(DataType.INT),
    name(DataType.STRING),
    code(DataType.STRING),
    begin(DataType.DATETIME),
    end(DataType.DATETIME),
    days(DataType.INT),
    status(DataType.ENUM),
    statge(DataType.INT),
    pri(DataType.INT),
    desc(DataType.STRING),
    openedBy(DataType.STRING),
    openedDate(DataType.DATETIME),
    openedVersion(DataType.STRING),
    closedBy(DataType.STRING),
    closedDate(DataType.DATETIME),
    canceledBy(DataType.STRING),
    canceledDate(DataType.DATETIME),
    PO(DataType.STRING),
    PM(DataType.STRING),
    QD(DataType.STRING),
    RD(DataType.STRING),
    team(DataType.STRING),
    acl(DataType.ENUM),
    whitelist(DataType.STRING),
    deleted(DataType.BOOLEAN);

    private final DataType dataType;
    private boolean isNullable = true;

    ProjectColumn(DataType dataType, boolean isNullable) {
        this.dataType = dataType;
        this.isNullable = isNullable;
    }

    ProjectColumn(DataType dataType) {
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
    public static ProjectColumn primary() {
        for(ProjectColumn column: values()) {
            if(column.isPrimaryKey()) {
                return column;
            }
        }
        return null;
    }
}
