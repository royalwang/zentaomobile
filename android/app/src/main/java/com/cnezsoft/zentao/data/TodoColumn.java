package com.cnezsoft.zentao.data;

public enum TodoColumn implements IColumn {
    id(DataType.INT),
    pri(DataType.INT),
    begin(DataType.DATETIME),
    end(DataType.DATETIME),
    type(DataType.STRING),
    name(DataType.STRING),
    status(DataType.STRING);

    @Override
    public Boolean isPrimaryKey() {
        return this == id;
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

    TodoColumn(DataType dataType) {
        this.dataType = dataType;
    }
}
