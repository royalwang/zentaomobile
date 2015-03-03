package com.cnezsoft.zentao;

import android.graphics.Color;
import android.widget.TextView;

import com.cnezsoft.zentao.colorswatch.MaterialColorName;
import com.cnezsoft.zentao.colorswatch.MaterialColorSwatch;
import com.cnezsoft.zentao.control.ControlBindInfo;
import com.cnezsoft.zentao.data.DataEntry;
import com.cnezsoft.zentao.data.Task;
import com.cnezsoft.zentao.data.TaskColumn;

/**
 * Task detail activity
 * Created by sunhao on 15/3/3.
 */
public class TaskDetailActivity extends DetailActivity {
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
        Task task = (Task) dataEntry;
        task.calculateHours();
        Task.Status status = task.getStatus();

        displayTitle(task.getAsString(TaskColumn.name));
        displayId("#" + task.key());
        Task.Type type = task.getTaskType();
        displayOnTextview(R.id.text_type, "{fa-tag} " + ZentaoApplication.getEnumText(this, type));
        displayOnTextview(R.id.text_info, "{fa-user} " + task.getAsString(TaskColumn.assignedTo));
        displayStatus(status, new ControlBindInfo(String.format(getString(R.string.text_task_hours_format), task.getEstimate(), task.getConsumed())));
        displayProgress(task.getProgress(), status.accent().primary().getColor());
        displayOnTextview(R.id.text_caption, String.format(getString(R.string.text_task_hours_left_format), task.getLeft()));

        displayHtmlMeta(ZentaoApplication.getEnumText(this, TaskColumn.desc), task.getAsString(TaskColumn.desc), "{fa-file-text-o}");
//        displayMeta(ZentaoApplication.getEnumText(this, TaskColumn.project), );
        setIcon();
    }
}
