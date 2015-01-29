package com.cnezsoft.zentao;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;

import com.cnezsoft.zentao.data.Todo;

/**
 * Created by Catouse on 2015/1/27.
 */
public class TodoFragmentPagerAdapter extends FragmentPagerAdapter {
    private Context context;
    private Todo.Order order;
    private Todo.Group group;

    public TodoFragmentPagerAdapter(Context context, FragmentManager fm, Todo.Order order, Todo.Group group) {
        super(fm);
        this.context = context;
        this.order = order;
        this.group = group;
    }

    @Override
    public Fragment getItem(int i) {
        return TodoListWrapperFragment.newInstance(Todo.PageTab.values()[i], order, group);
    }

    @Override
    public int getCount() {
        return Todo.PageTab.values().length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return ZentaoApplication.getEnumText(context, Todo.PageTab.values()[position]);
    }
}
