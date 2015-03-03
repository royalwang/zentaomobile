package com.cnezsoft.zentao;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cnezsoft.zentao.control.ControlBindInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by sunhao on 15/3/2.
 */
public class MetaListAdapter extends BaseAdapter {

    private final Context context;
    private LayoutInflater inflater = null;
    private ArrayList<HashMap<String, Object>> data;
    private int[] viewIdSet;
    private int layoutId;
    private String[] nameSet;
    private HashMap<Integer, String> idNameMap;
    private boolean clickable = false;
    private boolean dividerEnabled = true;

    public MetaListAdapter(Context context, ArrayList<HashMap<String, Object>> data, int layoutId, String[] nameSet, int[] viewIdSet) {
        this.context = context;
        this.data = data;
        this.inflater = LayoutInflater.from(context);
        this.layoutId = layoutId;
        this.viewIdSet = viewIdSet;
        this.nameSet = nameSet;

        idNameMap = new HashMap<>();
        for(int i = 0; i < viewIdSet.length; ++i) {
            idNameMap.put(viewIdSet[i], nameSet[i]);
        }
    }

    public MetaListAdapter(Context context, ArrayList<HashMap<String, Object>> data) {
        this(context, data, R.layout.list_item_detail_meta,
                new String[] {
                        "divider",
                        "icon",
                        "iconBack",
                        "actionIcon",
                        "actionIconBack",
                        "name",
                        "content",
                        "imageSet"
                }, new int[]{
                        R.id.divider,
                        R.id.icon,
                        R.id.icon_back,
                        R.id.action_icon,
                        R.id.action_icon_back,
                        R.id.text_name,
                        R.id.text_content,
                        R.id.image_container
                });
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return clickable ? super.areAllItemsEnabled() : false;
    }

    @Override
    public boolean isEnabled(int position) {
        return clickable ? super.isEnabled(position) : false;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        Object objId = data.get(position).get("_id");
        if(objId == null) {
            return position;
        } else {
            return (int) objId;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HashMap<Integer, View> holder;
        if(convertView == null) {
            holder = new HashMap<>();
            convertView = inflater.inflate(layoutId, null);
            for(int id: viewIdSet) {
                holder.put(id, convertView.findViewById(id));
            }
            convertView.setTag(holder);
        } else {
            holder = (HashMap<Integer, View>) convertView.getTag();
        }

        HashMap<String, Object> itemData = data.get(position);
        String name;
        for(View view: holder.values()) {
            name = idNameMap.get(view.getId());
            if(setItemValue(view, itemData, name, position)) {
                continue;
            }
            Object thisData = itemData.get(name);
            if(thisData == null) {
                view.setVisibility(View.GONE);
            } else {
                try {
                    ControlBindInfo info = (ControlBindInfo)  thisData;
                    info.displayOn(view);
                } catch (ClassCastException e) {
                    view.setVisibility(View.VISIBLE);
                    try {
                        try {
                            ((TextView) view).setText((Spanned) thisData);
                        } catch (Exception ignore) {
                            try {
                                ((TextView) view).setText((CharSequence) thisData);
                            } catch (Exception ignore2) {
                                ((TextView) view).setText(thisData.toString());
                            }
                        }
                    } catch (ClassCastException ignore) {}
                }
            }
            if(name.equals("divider")) {
                if(dividerEnabled && position != 0 && (thisData == null)) {
                    view.setVisibility(View.VISIBLE);
                } else {
                    view.setVisibility(View.INVISIBLE);
                }
            }
        }

        return convertView;
    }

    public boolean setItemValue(View view, HashMap<String, Object> itemData, String name, int position) {
        return false;
    }
}