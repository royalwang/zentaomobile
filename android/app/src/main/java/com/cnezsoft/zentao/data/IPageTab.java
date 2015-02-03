package com.cnezsoft.zentao.data;

import android.content.Context;

/**
 * Page tab interface
 * Created by Catouse on 2015/1/30.
 */
public interface IPageTab {
    public String name();
    public String text(Context context);
    public int ordinal();

    public IPageTab[] tabs();

    public EntryType getEntryType();
}
