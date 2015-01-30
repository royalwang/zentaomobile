package com.cnezsoft.zentao;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;

import com.cnezsoft.zentao.data.IPageTab;

/**
 * Created by Catouse on 2015/1/30.
 */
public class EntryListFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context context;
    private IPageTab pageTab;

    public EntryListFragmentPagerAdapter(Context context, FragmentManager fm, IPageTab pageTab) {
        super(fm);
        this.context = context;
        this.pageTab = pageTab;
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        return EntryListViewFragment.newInstance(pageTab.tabs()[position]);
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return pageTab.tabs().length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return ZentaoApplication.getEnumText(context, (Enum) pageTab.tabs()[position]);
    }
}
