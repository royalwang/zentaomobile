package com.cnezsoft.zentao;

import android.os.Bundle;
import android.widget.TextView;

import com.cnezsoft.zentao.colorswatch.MaterialColorSwatch;
import com.cnezsoft.zentao.data.EntryType;
import com.cnezsoft.zentao.data.Project;
import com.cnezsoft.zentao.data.ProjectColumn;

/**
 * Project detail activity
 * Created by Catouse on 2015/3/2.
 */
public class ProjectDetailActivity extends DetailActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAccentSwatch(EntryType.Project.accent());
    }

    @Override
    protected void setIcon(MaterialColorSwatch swatch, TextView iconView, TextView iconBackView, TextView iconTextView) {
        super.setIcon(swatch, iconView, iconBackView, iconTextView);
        iconView.setTextColor(Project.accent(entryId).primary().value());
        if(entry != null) {
            String name = entry.getAsString(ProjectColumn.name);
            if(!Helper.isNullOrEmpty(name)) {
                iconTextView.setText(name.substring(0, 1));
            }
        }
    }
}
