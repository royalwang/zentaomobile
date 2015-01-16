package com.cnezsoft.zentao.data;

public enum DataType {
    STRING(SqlDataType.TEXT),
    BOOLEAN(SqlDataType.INTEGER),
    INT(SqlDataType.INTEGER),
    LONG(SqlDataType.INTEGER),
    FLOAT(SqlDataType.REAL),
    DOUBLE(SqlDataType.REAL),
    DATETIME(SqlDataType.INTEGER);

    private SqlDataType type;

    public SqlDataType sqlType() {
        return type;
    }

    DataType(SqlDataType type) {
        this.type = type;
    }
}
