package com.cnezsoft.zentao.data;

/**
 * Todo columns
 */
public enum TodoColumn implements IColumn {
    _id(DataType.INT, false),
    pri(DataType.INT),
    begin(DataType.DATETIME),
    end(DataType.DATETIME),
    type(DataType.STRING),
    name(DataType.STRING),
    status(DataType.STRING),
    desc(DataType.STRING);

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

    private DataType dataType;

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
}
