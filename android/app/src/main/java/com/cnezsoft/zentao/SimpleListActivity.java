package com.cnezsoft.zentao;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.InsetDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.cnezsoft.zentao.colorswatch.MaterialColor;
import com.cnezsoft.zentao.colorswatch.MaterialColorSwatch;
import com.cnezsoft.zentao.control.ControlBindInfo;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * SimpleListActivity
 * Created by Catouse on 2015/2/28.
 */
public class SimpleListActivity extends ZentaoActivity {

    protected ListView list;
    protected TextView messagerView;
    protected SimpleAdapter adapter;
    protected ArrayList<HashMap<String, ?>> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_list);

        data = new ArrayList<>();
        list = (ListView) findViewById(R.id.listview);

        adapter = getAdapter();
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemClick(parent, view, position, id);
            }
        });

        listenMessage();
        executeUpdateListTask();
    }

    /**
     * List the "Synchronizer.MESSAGE_OUT_SYNC" message
     * Override this to listen more message type
     */
    protected void listenMessage() {
        listenMessage(Synchronizer.MESSAGE_OUT_SYNC);
    }

    protected void displayMessage(Iconify.IconValue icon, String message, MaterialColorSwatch swatch, long time) {
        if(messagerView == null) {
            messagerView = findTextViewById(R.id.messager);
        }
        final MaterialColor color = swatch.primary();
        messagerView.setBackgroundColor(color.value());
        messagerView.setTextColor(color.getInverseColor());
        messagerView.setCompoundDrawables(new IconDrawable(this, icon) {{
            sizeDp(24);
            color(color.getInverseColor());
        }}, null, null, null);
        messagerView.setVisibility(View.VISIBLE);
        messagerView.setText(message);

        final Activity activity = this;
        if(time > 0) {
            Animation finalAnimation = AnimationUtils.loadAnimation(activity, R.anim.abc_slide_out_top);
            finalAnimation.setStartOffset(time);
            finalAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    messagerView.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            messagerView.setAnimation(finalAnimation);
        }
    }

    protected void displayMessage(Iconify.IconValue icon, String message, long time) {
        displayMessage(icon, message, MaterialColorSwatch.Blue, time);
    }

    protected void displayMessage(String message, MaterialColorSwatch swatch, long time) {
        displayMessage(null, message, swatch, time);
    }

    protected void displayMessage(String message, long time) {
        displayMessage(null, message, time);
    }

    /**
     * Load list data in async task.
     * @param params
     * @return
     */
    protected ArrayList<HashMap<String, Object>> loadData(Context... params) {
        return null;
    }

    /**
     * On list item click
     * @param parent The AdapterView where the click happened.
     * @param view The view within the AdapterView that was clicked (this
     *            will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id The row id of the item that was clicked.
     */
    protected void onListItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    /**
     * Get adapter
     * Override this to get custom adapter
     * @return
     */
    protected SimpleAdapter getAdapter() {
        adapter = new SimpleAdapter(this, data, R.layout.list_item_simple_list,
                new String[]{
                    "icon_back",
                    "icon",
                    "prefix",
                    "title",
                    "subtitle",
                    "tag",
                    "id",
                    "info",
                    "extra_info",
                    "status"
                },
                new int[]{
                    R.id.icon,
                    R.id.text_icon,
                    R.id.text_prefix,
                    R.id.text_title,
                    R.id.text_subtitle,
                    R.id.text_tag,
                    R.id.text_id,
                    R.id.text_info,
                    R.id.text_extra_info,
                    R.id.text_status
                });
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if(!setListViewValue(view, data, textRepresentation)) {
                    try {
                        ControlBindInfo info = (ControlBindInfo) data;
                        if(info != null) {
                            view.setVisibility(info.visibility);
                            if(info.visibility == View.VISIBLE) {
                                TextView textView = (TextView) view;
                                textView.setText(info.text);
                                textView.setTextColor(info.textColor);
                                textView.setBackgroundColor(info.backColor);
                            }
                            return true;
                        }
                    } catch (ClassCastException ignored) {
                    }
                    return false;
                }
                return false;
            }
        });
        return adapter;
    }

    protected boolean setListViewValue(View view, Object data, String textRepresentation) {
        return false;
    }

    @Override
    protected void onReceiveMessage(Intent intent) {
        if(intent.getAction().equals(Synchronizer.MESSAGE_OUT_SYNC)) {
            executeUpdateListTask();
        }
    }

    private void executeUpdateListTask() {
        new UpdateListTask().execute(this);
    }

    protected HashMap<String, Object> convertListItemData(HashMap<String, Object> item) {
        return item;
    }

    protected boolean onUpdateList(ArrayList<HashMap<String, Object>> newData) {
        if(newData != null && newData.size() > 0) {
            data.clear();
            for(HashMap<String, Object> item: newData) {
                data.add(convertListItemData(item));
            }
            adapter.notifyDataSetChanged();
            return true;
        }
        return false;
    }

    private class UpdateListTask extends AsyncTask<Context, Integer, ArrayList<HashMap<String, Object>>> {
        @Override
        protected ArrayList<HashMap<String, Object>> doInBackground(Context... params) {
            return loadData(params);
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, Object>> entryTypeHashMapHashMap) {
            super.onPostExecute(entryTypeHashMapHashMap);
            onUpdateList(entryTypeHashMapHashMap);
        }
    }
}
