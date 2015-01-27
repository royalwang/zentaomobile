package com.cnezsoft.zentao;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;

/**
 * Created by Catouse on 2015/1/27.
 */
public class TodoFragmentPagerAdapter extends FragmentPagerAdapter {
    private Context context;

    public TodoFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int i) {
        return TodoListWrapperFragment.newInstance(TodoListWrapperFragment.PageTab.values()[i]);
    }

    @Override
    public int getCount() {
        return TodoListWrapperFragment.PageTab.values().length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return context.getResources().getStringArray(R.array.todo_list_names)[position];
    }
}
