package com.cnezsoft.zentao;

import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

import com.cnezsoft.zentao.colorswatch.MaterialColorName;
import com.cnezsoft.zentao.colorswatch.MaterialColorSwatch;
import com.cnezsoft.zentao.control.ControlBindInfo;
import com.cnezsoft.zentao.data.DataEntry;
import com.cnezsoft.zentao.data.Story;
import com.cnezsoft.zentao.data.StoryColumn;
import com.cnezsoft.zentao.data.Task;
import com.cnezsoft.zentao.data.TaskColumn;

/**
 * Story detail activity
 * Created by sunhao on 15/3/3.
 */
public class StoryDetailActivity extends DetailActivity {
    @Override
    protected boolean setIcon(MaterialColorSwatch swatch, TextView iconView, TextView iconBackView, TextView iconTextView) {
        super.setIcon(swatch, iconView, iconBackView, iconTextView);
        iconBackView.setText("{fa-circle}");
        if(entry != null) {
            int pri = entry.getAccentPri();
            iconView.setTextColor(Color.WHITE);
            if(pri > 0) {
                iconBackView.setTextColor(MaterialColorSwatch.PriAccentSwatches[pri].primary().value());
                iconView.setText(pri + "");
            } else {
                iconBackView.setTextColor(entryType.accent().color(MaterialColorName.A700).getColor());
                iconView.setText("{fa-" + entryType.icon() + "}");
            }
        }
        return true;
    }

    @Override
    protected void display(DataEntry dataEntry) {
        Story story = (Story) dataEntry;
        Story.Status status = story.getStatus();

        displayTitle(story.getAsString(StoryColumn.title));
        displayId("#" + story.key());
        Story.Source type = story.getSource();
        displayOnTextview(R.id.text_type, "{fa-user} " + getString(R.string.text_story_from) + " " + ZentaoApplication.getEnumText(this, type));
        displayOnTextview(R.id.text_info, "{fa-hand-o-right} " + story.getAsString(StoryColumn.assignedTo));
        displayStatus(status, new ControlBindInfo(ZentaoApplication.getEnumText(this, story.getStage())));

        displayHtmlMeta(ZentaoApplication.getEnumText(this, StoryColumn.spec), story.getAsString(StoryColumn.spec), "{fa-file-text-o}");
        displayHtmlMeta(ZentaoApplication.getEnumText(this, StoryColumn.verify), story.getAsString(StoryColumn.verify), "{fa-gavel}");
        setIcon();
    }
}
