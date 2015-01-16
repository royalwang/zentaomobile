package com.cnezsoft.zentao.data;

/**
 * Created by Catouse on 2015/1/16.
 */

public interface IColumn {
    public DataType type();
    public int index();
    public String name();
    public Boolean isPrimaryKey();
}
