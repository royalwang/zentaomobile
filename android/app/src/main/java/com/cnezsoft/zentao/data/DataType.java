package com.cnezsoft.zentao.data;

/**
 * Data type enum
 */
public enum DataType {
    STRING(SqlDataType.TEXT),
    BOOLEAN(SqlDataType.INTEGER),
    INT(SqlDataType.INTEGER),
    LONG(SqlDataType.INTEGER),
    FLOAT(SqlDataType.REAL),
    DOUBLE(SqlDataType.REAL),
    DATETIME(SqlDataType.INTEGER);

    private SqlDataType type;

    /**
     * Get sql type
     * @return
     */
    public SqlDataType sqlType() {
        return type;
    }

    DataType(SqlDataType type) {
        this.type = type;
    }
}
