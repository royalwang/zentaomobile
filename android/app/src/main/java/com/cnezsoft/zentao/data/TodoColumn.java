package com.cnezsoft.zentao.data;

public enum TodoColumn implements IColumn {
    id(DataType.INT, false),
    pri(DataType.INT),
    begin(DataType.DATETIME, false),
    end(DataType.DATETIME, false),
    type(DataType.STRING),
    name(DataType.STRING),
    status(DataType.STRING);

    @Override
    public Boolean isPrimaryKey() {
        return this == id;
    }

    private boolean isNullable = true;

    @Override
    public boolean nullable() {
        return isNullable;
    }

    @Override
    public int index() {
        return this.ordinal();
    }

    private DataType dataType;

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
}
