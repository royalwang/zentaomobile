package com.cnezsoft.zentao;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.cnezsoft.zentao.colorswatch.MaterialColorSwatch;
import com.cnezsoft.zentao.control.ControlBindInfo;
import com.cnezsoft.zentao.data.DAO;
import com.cnezsoft.zentao.data.DataEntry;
import com.cnezsoft.zentao.data.EntryType;
import com.cnezsoft.zentao.data.Project;
import com.cnezsoft.zentao.data.ProjectColumn;

import java.text.NumberFormat;

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
    protected boolean setIcon(MaterialColorSwatch swatch, TextView iconView, TextView iconBackView, TextView iconTextView) {
        super.setIcon(swatch, iconView, iconBackView, iconTextView);
        iconBackView.setTextColor(Project.accent(entryId).primary().value());
        return true;
    }

    @Override
    protected DataEntry loadData() {
        super.loadData();
        Project project = (Project) entry;
        DAO dao = getDAO();
        String key = project.key();
        project.calculateHours(dao.getProjectTasks(key));
        project.setBugCount(dao.getBugCountOfProject(key));
        return project;
    }

    @Override
    protected void display(DataEntry dataEntry) {
        Project project = (Project) dataEntry;
        Project.Status status = project.getStatus();

        displayTitle(project.getAsString(ProjectColumn.name));
        displayId("#" + project.key());
        displayOnTextview(R.id.text_type, "{fa-user} " + project.getAsString(ProjectColumn.PM));
        displayOnTextview(R.id.text_info, getString(R.string.text_project_code) + " " + project.getAsString(ProjectColumn.code));
        displayProgress(project.getProgress(), status.accent().primary().value());
        displayStatus(status, new ControlBindInfo(project.getFriendlyDateSpanString(this)));
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(1);
        if(project.getBugCount() > 0) {
            displayMeta(getString(R.string.text_project_active_bug), project.getBugCount(), "{fa-bug}");
        }
        displayMeta(getString(R.string.text_project_hours), String.format(getString(R.string.text_project_hours_format),
                numberFormat.format(project.getEstimate()),
                numberFormat.format(project.getConsumed()),
                numberFormat.format(project.getLeft())), "{fa-clock-o}");
        displayMeta(ZentaoApplication.getEnumText(this, ProjectColumn.desc), project.getFriendlyString(ProjectColumn.desc), "{fa-file-text-o}");
        displayMeta(ZentaoApplication.getEnumText(this, ProjectColumn.type), ZentaoApplication.getEnumText(this, project.getProjectType()), "{fa-tag}");
        displayMeta(ZentaoApplication.getEnumText(this, ProjectColumn.days), project.getAsInteger(ProjectColumn.days), "{fa-sun-o}");
        Project.Acl acl = project.getAcl();
        displayMeta(ZentaoApplication.getEnumText(this, ProjectColumn.acl), acl, acl == Project.Acl.open ? "{fa-unlock}" : "{fa-lock}");
        displayMeta(ZentaoApplication.getEnumText(this, ProjectColumn.PM), project.getAsString(ProjectColumn.PM), "{fa-user}");
        displayMeta(ZentaoApplication.getEnumText(this, ProjectColumn.PO), project.getAsString(ProjectColumn.PO), false);
        displayMeta(ZentaoApplication.getEnumText(this, ProjectColumn.QD), project.getAsString(ProjectColumn.QD), false);
        displayMeta(ZentaoApplication.getEnumText(this, ProjectColumn.RD), project.getAsString(ProjectColumn.RD), false);
        String whiteList = project.getAsString(ProjectColumn.whitelist);
        if(!Helper.isNullOrEmpty(whiteList)) {
            displayMeta(ZentaoApplication.getEnumText(this, ProjectColumn.whitelist), whiteList, "{fa-group}");
        }
        setIcon();
    }
}
