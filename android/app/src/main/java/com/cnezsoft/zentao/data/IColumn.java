package com.cnezsoft.zentao.data;

import android.content.Context;

/**
 * Column interface
 *
 * Created by Catouse on 2015/1/16.
 */

public interface IColumn {

    /**
     * Get text string
     * @param context
     * @return
     */
    public String text(Context context);

    /**
     * Get data type
     * @return
     */
    public DataType type();

    /**
     * Get index
     * @return
     */
    public int index();

    /**
     * Get name
     * @return
     */
    public String name();

    /**
     * Judge the column is primary key
     * @return
     */
    public Boolean isPrimaryKey();

    /**
     * Judge the column is nullable
     * @return
     */
    public boolean nullable();
}
