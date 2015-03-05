package com.cnezsoft.zentao;

import com.cnezsoft.zentao.data.DataType;

/**
 * Created by sunhao on 15/3/4.
 */
public enum UserAttr {
    account(DataType.STRING),
    passwordMd5(DataType.STRING),
    address(DataType.STRING),
    lastLoginTime(DataType.DATETIME, 0),
    lastSyncTime(DataType.DATETIME, 0),
    email(DataType.STRING),
    realName(DataType.STRING),
    role(DataType.STRING),
    gender(DataType.STRING),
    dbVersion(DataType.LONG, 0l),
    notifyEnable(DataType.BOOLEAN, true),
    syncFrequency(DataType.LONG, SyncFrequency.defaultOption().getMilliseconds()),
    id(DataType.STRING),
    company(DataType.STRING);

    private DataType type;
    private Object defaultValue = null;

    public DataType getType() {
        return type;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    UserAttr(DataType dataType) {
        this.type = dataType;
    }

    UserAttr(DataType dataType, Object defaultValue) {
        this.type = dataType;
        this.defaultValue = defaultValue;
    }
}
