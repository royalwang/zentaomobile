package com.cnezsoft.zentao;

import android.graphics.Color;
import android.widget.TextView;

import com.cnezsoft.zentao.colorswatch.MaterialColorName;
import com.cnezsoft.zentao.colorswatch.MaterialColorSwatch;
import com.cnezsoft.zentao.control.ControlBindInfo;
import com.cnezsoft.zentao.data.DAO;
import com.cnezsoft.zentao.data.DataEntry;
import com.cnezsoft.zentao.data.EntryType;
import com.cnezsoft.zentao.data.Project;
import com.cnezsoft.zentao.data.ProjectColumn;
import com.cnezsoft.zentao.data.Story;
import com.cnezsoft.zentao.data.StoryColumn;
import com.cnezsoft.zentao.data.Task;
import com.cnezsoft.zentao.data.TaskColumn;

import java.util.Date;

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
    protected DataEntry loadData() {
        super.loadData();
        Task task = (Task) entry;
        DAO dao = getDAO();
        task.setStory(dao.query(EntryType.Story, task.getAsInteger(TaskColumn.story)));
        task.setProject(dao.query(EntryType.Project, task.getAsInteger(TaskColumn.project)));
        return task;
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
        displayOnTextview(R.id.text_info, "{fa-hand-o-right} " + task.getAsString(TaskColumn.assignedTo));
        displayStatus(status, new ControlBindInfo(String.format(getString(R.string.text_task_hours_format), task.getEstimate(), task.getConsumed())));
        displayProgress(task.getProgress(), status.accent().primary().getColor());
        displayOnTextview(R.id.text_caption, String.format(getString(R.string.text_task_hours_left_format), task.getLeft()));
        displayHtmlMeta(ZentaoApplication.getEnumText(this, TaskColumn.desc), task.getAsString(TaskColumn.desc), "{fa-file-text-o}");

        Story story = task.getStory();
        if(story != null) {
            String storySpec = story.getAsString(StoryColumn.spec);
            if(!Helper.isNullOrEmpty(storySpec)) {
                storySpec = "<p>" + storySpec + "</p>";
            } else {
                storySpec = "";
            }
            displayHtmlMeta(ZentaoApplication.getEnumText(this, TaskColumn.story),
                    "<h6>#" + story.key() + " " + story.getAsString(StoryColumn.title) + "</h6>" + storySpec, "{fa-" + EntryType.Story.icon() + "}");
        }

        Project project = task.getProject();
        if(project != null) {
            displayMeta(ZentaoApplication.getEnumText(this, TaskColumn.project), project.getAsString(ProjectColumn.name), "{fa-" + EntryType.Project.icon() + "}");
        }

        Date deadline = task.getAsDate(TaskColumn.deadline);
        Date estStarted = task.getAsDate(TaskColumn.estStarted);
        Date realStarted = task.getAsDate(TaskColumn.realStarted);
        if(deadline!=null) {
            displayMeta(ZentaoApplication.getEnumText(this, TaskColumn.deadline), Helper.formatDate(deadline, DateFormatType.DateTime), "{fa-clock-o}");
        }
        if(estStarted!=null) {
            displayMeta(ZentaoApplication.getEnumText(this, TaskColumn.estStarted), Helper.formatDate(estStarted, DateFormatType.DateTime), false);
        }
        if(realStarted!=null) {
            displayMeta(ZentaoApplication.getEnumText(this, TaskColumn.realStarted), Helper.formatDate(realStarted, DateFormatType.DateTime), false);
        }

        String openedBy = task.getAsString(TaskColumn.openedBy);
        displayMeta(ZentaoApplication.getEnumText(this, TaskColumn.openedBy),
                openedBy + " " + String.format(getString(R.string.text_date_at_format),
                        Helper.formatDate(task.getAsDate(TaskColumn.openedDate), DateFormatType.DateTime)), "{fa-user}");

        String finishedBy = task.getAsString(TaskColumn.finishedBy);
        if(!Helper.isNullOrEmpty(finishedBy)) {
            displayMeta(ZentaoApplication.getEnumText(this, TaskColumn.finishedBy),
                    finishedBy + " " + String.format(getString(R.string.text_date_at_format),
                            Helper.formatDate(task.getAsDate(TaskColumn.finishedDate), DateFormatType.DateTime)), false);
        }
        String closedBy = task.getAsString(TaskColumn.closedBy);
        if(!Helper.isNullOrEmpty(closedBy)) {
            displayMeta(ZentaoApplication.getEnumText(this, TaskColumn.closedBy),
                    closedBy + " " + String.format(getString(R.string.text_date_at_format),
                            Helper.formatDate(task.getAsDate(TaskColumn.closedDate), DateFormatType.DateTime)), false);
            displayMeta(ZentaoApplication.getEnumText(this, TaskColumn.closeReason), task.getAsString(TaskColumn.closeReason), false);
        }
        String canceledBy = task.getAsString(TaskColumn.canceledBy);
        if(!Helper.isNullOrEmpty(canceledBy)) {
            displayMeta(ZentaoApplication.getEnumText(this, TaskColumn.canceledBy),
                    canceledBy + " " + String.format(getString(R.string.text_date_at_format),
                            Helper.formatDate(task.getAsDate(TaskColumn.canceledDate), DateFormatType.DateTime)), false);
        }
        setIcon();
    }
}
