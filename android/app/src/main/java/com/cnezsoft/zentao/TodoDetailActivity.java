package com.cnezsoft.zentao;

import android.graphics.Color;
import android.widget.TextView;

import com.cnezsoft.zentao.colorswatch.MaterialColorSwatch;
import com.cnezsoft.zentao.control.ControlBindInfo;
import com.cnezsoft.zentao.data.DataEntry;
import com.cnezsoft.zentao.data.Todo;
import com.cnezsoft.zentao.data.TodoColumn;

/**
 * Todo detail activity
 * Created by sunhao on 15/3/3.
 */
public class TodoDetailActivity extends DetailActivity {
    @Override
    protected boolean setIcon(MaterialColorSwatch swatch, TextView iconView, TextView iconBackView, TextView iconTextView) {
        super.setIcon(swatch, iconView, iconBackView, iconTextView);
        Todo todo = (Todo) entry;
        iconBackView.setText("{fa-circle}");
        if(entry != null) {
            int pri = todo.getAccentPri();
            int color = MaterialColorSwatch.PriAccentSwatches[pri].primary().value();
            iconBackView.setTextColor(color);
            iconView.setTextColor(Color.WHITE);
            iconView.setText(todo.getStatus() == Todo.Status.done ? "{fa-check}" : (pri > 0 ? (pri + "") : ""));
        }
        return true;
    }

    @Override
    protected void display(DataEntry dataEntry) {
        Todo todo = (Todo) dataEntry;
        Todo.Status status = todo.getStatus();

        displayTitle(todo.getAsString(TodoColumn.name));
        displayId("#" + todo.key());
        Todo.Types type = todo.getTodoType();
        displayOnTextview(R.id.text_type, "{fa-" + type.icon() + "} " + ZentaoApplication.getEnumText(this, type));
        displayStatus(status, new ControlBindInfo(todo.getFriendlyTimeString(this)));

        displayHtmlMeta(ZentaoApplication.getEnumText(this, TodoColumn.desc), todo.getAsString(TodoColumn.desc), "{fa-file-text-o}");

        setIcon();
    }
}
