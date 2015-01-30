package com.cnezsoft.zentao.data;

import android.content.Context;

/**
 * Created by Catouse on 2015/1/30.
 */
public interface IListFilter {
    public String name();
    public int ordinal();

    public String text(Context context);
    public IListFilter[] all();
}
