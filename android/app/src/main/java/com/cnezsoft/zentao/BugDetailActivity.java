package com.cnezsoft.zentao;

import android.graphics.Color;
import android.widget.TextView;

import com.cnezsoft.zentao.colorswatch.MaterialColorName;
import com.cnezsoft.zentao.colorswatch.MaterialColorSwatch;
import com.cnezsoft.zentao.control.ControlBindInfo;
import com.cnezsoft.zentao.data.Bug;
import com.cnezsoft.zentao.data.BugColumn;
import com.cnezsoft.zentao.data.DataEntry;
import com.cnezsoft.zentao.data.Task;
import com.cnezsoft.zentao.data.TaskColumn;

/**
 * Bug detail activity
 * Created by sunhao on 15/3/3.
 */
public class BugDetailActivity extends DetailActivity {
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
        Bug bug = (Bug) dataEntry;
        Bug.Status status = bug.getStatus();

        displayTitle(bug.getAsString(BugColumn.title));
        displayId("#" + bug.key());
        Bug.Type type = bug.getBugType();
        displayOnTextview(R.id.text_type, "{fa-tag} " + ZentaoApplication.getEnumText(this, type));
        displayOnTextview(R.id.text_info, "{fa-user} " + bug.getAsString(BugColumn.assignedTo));
        displayStatus(status, new ControlBindInfo(bug.getAsBoolean(BugColumn.confirmed) ? getString(R.string.text_confirmed)
                : getString(R.string.text_unconfirm)));
        int severity = bug.getAsInteger(BugColumn.severity);
        displayOnTextview(R.id.text_caption, new ControlBindInfo(String.format(getString(R.string.text_bug_severity), severity), MaterialColorSwatch.PriAccentSwatches[severity].primary().getColor()));

        String resolvedBy = bug.getAsString(BugColumn.resolvedBy);
        if(!Helper.isNullOrEmpty(resolvedBy)) {
            displayMeta(ZentaoApplication.getEnumText(this, BugColumn.resolvedBy), resolvedBy, "{fa-user}");
            displayMeta(ZentaoApplication.getEnumText(this, BugColumn.resolvedDate),
                    Helper.formatDate(bug.getAsDate(BugColumn.resolvedDate), getString(R.string.text_long_datetime_format)), "{fa-clock-o}");
            displayMeta(ZentaoApplication.getEnumText(this, BugColumn.resolution), ZentaoApplication.getEnumText(this, bug.getResolution()), "{fa-check}", false);
        }
        String resolvedBuild = bug.getAsString(BugColumn.resolvedBuild);
        if(!Helper.isNullOrEmpty(resolvedBuild)) {
            displayMeta(ZentaoApplication.getEnumText(this, BugColumn.resolvedBuild), resolvedBuild, "{fa-code-fork}", false);
        }
        displayHtmlMeta(ZentaoApplication.getEnumText(this, BugColumn.steps), bug.getAsString(BugColumn.steps), "{fa-list-ol}");
        displayMeta(ZentaoApplication.getEnumText(this, BugColumn.os), ZentaoApplication.getEnumText(this, bug.getOS()), "{fa-desktop}");
        displayMeta(ZentaoApplication.getEnumText(this, BugColumn.browser), ZentaoApplication.getEnumText(this, bug.getBrowser()), "{fa-globe}");
        displayMeta(ZentaoApplication.getEnumText(this, BugColumn.activatedCount), bug.getAsInteger(BugColumn.activatedCount), "{fa-power-off}");
        displayMeta(ZentaoApplication.getEnumText(this, BugColumn.openedBuild), bug.getAsString(BugColumn.openedBuild), "{fa-code-fork}");

        setIcon();
    }
}
