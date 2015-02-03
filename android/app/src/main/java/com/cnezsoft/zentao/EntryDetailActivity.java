package com.cnezsoft.zentao;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.IconTextView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.cnezsoft.zentao.colorswatch.MaterialColorSwatch;
import com.cnezsoft.zentao.data.Bug;
import com.cnezsoft.zentao.data.BugColumn;
import com.cnezsoft.zentao.data.DAO;
import com.cnezsoft.zentao.data.DataEntry;
import com.cnezsoft.zentao.data.DataEntryFactory;
import com.cnezsoft.zentao.data.DataLoader;
import com.cnezsoft.zentao.data.EntryType;
import com.cnezsoft.zentao.data.IColumn;
import com.cnezsoft.zentao.data.Story;
import com.cnezsoft.zentao.data.StoryColumn;
import com.cnezsoft.zentao.data.Task;
import com.cnezsoft.zentao.data.TaskColumn;
import com.cnezsoft.zentao.data.Todo;
import com.cnezsoft.zentao.data.TodoColumn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Detail view activity
 */
public class EntryDetailActivity extends ZentaoActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ARG_ENTRY_TYPE = "com.cnezsoft.zentao.ENTRY_TYPE";
    public static final String ARG_ID = "com.cnezsoft.zentao.ID";

    protected EntryType entryType = null;
    private long entryId = -1;
    protected boolean inherit = false;
    private DataEntry entry;
    private int layout;

    protected EntryType setEntryType() {
        return EntryType.Default;
    }

    public EntryDetailActivity() {
        entryType = setEntryType();
        inherit = entryType != null && entryType != EntryType.Default;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get arguments from intent
        Intent intent = getIntent();
        if(intent == null) {
            throw new NullPointerException("Can't get intent object.");
        }
        String type = intent.getStringExtra(ARG_ENTRY_TYPE);
        if(type != null) {
            entryType = EntryType.valueOf(type);
        } else if(!inherit) {
            throw new NullPointerException("Can't get EntryType form intent object.");
        }
        long temp = intent.getLongExtra(ARG_ID, -1l);
        if(temp > -1) {
            entryId = temp;
        } else {
            throw new NullPointerException("Can't get ID form intent object.");
        }

        // choose layout
        switch (entryType) {
            case Todo:
                layout = R.layout.activity_todo_detail;
                break;
            case Task:
                layout = R.layout.activity_task_detail;
                break;
            case Bug:
                layout = R.layout.activity_bug_detail;
                break;
            case Story:
                layout = R.layout.activity_story_detail;
                break;
            default:
                layout = R.layout.activity_entry_detail;
                break;
        }

        setContentView(layout);

        // init loader
        getLoaderManager().initLoader(entryType.ordinal(), null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_entry_detail, menu);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setTitle(String.format(getResources().getString(R.string.action_detail_format), entryType.text(this)));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id == android.R.id.home) {
            finish();
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        DataLoader.OnLoadDataListener listener = new DataLoader.OnLoadDataListener(){{}
            @Override
            public Cursor onLoadData(Context context) {
                DAO dao = new DAO(context);
                return dao.query(entryType, entryId + "");
            }
        };

        if(listener != null) {
            return new DataLoader(this, entryType, listener);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(entry == null) {
            entry = DataEntryFactory.create(entryType);
        }
        if(data.moveToNext()) {
            entry.fromCursor(data);
        }
        displayEntry();
    }

    private List<Map<String, Object>> getEntryData() {
        List<Map<String, Object>> list = new ArrayList<>();
        final Context context = this;
        for(final IColumn column: entryType.columns()) {
            list.add(new HashMap<String, Object>(){{
                put("text_name", column.text(context));
                put("text_value", entry.getFriendlyString(column));
            }});
        }

        return list;
    }

    @Override
    protected void setAccentSwatch(MaterialColorSwatch swatch) {
        super.setAccentSwatch(swatch);
        findViewById(R.id.entry_detail_heading).setBackgroundColor(swatch.primary().value());
    }

    private void displayEntry() {
        int accentPri = entry.getAccentPri();
        if(accentPri > 0 && accentPri < MaterialColorSwatch.PriAccentSwatches.length) {
            setAccentSwatch(MaterialColorSwatch.PriAccentSwatches[accentPri]);
        }

        // common attributes
        ViewGroup container = (ViewGroup) findViewById(R.id.entry_detail_container);
        TextView view;
        String friendlyStr;
        for(IColumn column: entryType.columns()) {
            view = (TextView) container.findViewWithTag("entry_" + column.name());
            if(view != null) {
                friendlyStr = entry.getFriendlyString(column);
                if(friendlyStr != null) {
                    view.setText(entry.getFriendlyString(column));
                }
            }
        }

        // specials
        if(layout == R.layout.activity_entry_detail) {
            ListView listView = (ListView) findViewById(R.id.listView_default);
            SimpleAdapter adapter = (SimpleAdapter) listView.getAdapter();
            if(adapter == null) {
                adapter = new SimpleAdapter(this,
                        getEntryData(),
                        R.layout.list_item_entry_detail,
                        new String[]{"text_name", "text_value"},
                        new int[]{R.id.text_name, R.id.text_value});
            } else {
                adapter.notifyDataSetChanged();
            }
            listView.setAdapter(adapter);
        } else if(layout == R.layout.activity_todo_detail && entryType == EntryType.Todo) {
            Todo todo = (Todo) entry;
            Resources resources = getResources();
            Todo.Status status = todo.getStatus();
            ((IconTextView) findViewById(R.id.text_entry_type)).setText("{fa-tag} "
                    + ZentaoApplication.getEnumText(this, todo.getTodoType()));
            String desc = todo.getAsString(TodoColumn.desc);
            if(desc != null) {
                ((TextView) findViewById(R.id.text_entry_desc)).setText(Html.fromHtml(desc));
            }
            TextView statusView = (TextView) findViewById(R.id.text_entry_status);
            statusView.setText(ZentaoApplication.getEnumText(this, status));
            TextView statusIconView = (TextView) findViewById(R.id.icon_entry_status);
            TextView dateView = (TextView) findViewById(R.id.text_entry_date);
            dateView.setText(Helper.formatDate(todo.getAsDate(TodoColumn.begin),
                    resources.getString(R.string.text_todo_date_format))
                    + " " + Helper.formatDate(todo.getAsDate(TodoColumn.end), DateFormatType.Time));
            int statusColor = status.accent().primary().value();
            statusIconView.setText("{fa-" + status.icon() + "}");
            statusView.setTextColor(statusColor);
            statusIconView.setTextColor(statusColor);
        } else if(layout == R.layout.activity_task_detail && entryType == EntryType.Task) {
            Task task = (Task) entry;
            Resources resources = getResources();
            Task.Status status = task.getStatus();

            ((IconTextView) findViewById(R.id.text_entry_type)).setText("{fa-folder-o} "
                    + ZentaoApplication.getEnumText(this, task.getTaskType()));
            String desc = task.getAsString(TaskColumn.desc);
            if(desc != null) {
                ((TextView) findViewById(R.id.text_entry_desc)).setText(Html.fromHtml(desc));
            }

            TextView statusView = (TextView) findViewById(R.id.text_entry_status);
            statusView.setText(ZentaoApplication.getEnumText(this, status));
            TextView statusIconView = (TextView) findViewById(R.id.icon_entry_status);
            int statusColor = status.accent().primary().value();
            statusIconView.setText("{fa-" + status.icon() + "}");
            statusView.setTextColor(statusColor);
            statusIconView.setTextColor(statusColor);

            float estimate = task.getAsFloat(TaskColumn.estimate);
            float consumed = task.getAsFloat(TaskColumn.consumed);
            float left = task.getAsFloat(TaskColumn.left);
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar_entry_hours);
            progressBar.getProgressDrawable().setColorFilter(statusColor, PorterDuff.Mode.SRC_IN);
            progressBar.setMax((int) (Math.max(estimate, consumed + left) * 100));
            progressBar.setProgress((int) (consumed * 100));

            ((TextView) findViewById(R.id.text_entry_hours)).setText(
                    String.format(resources.getString(R.string.text_task_hours_format), estimate, consumed));
            ((TextView) findViewById(R.id.text_entry_left)).setText(
                    String.format(resources.getString(R.string.text_task_hours_left_format), left));

            TextView assignedToView = (TextView) findViewById(R.id.text_entry_assignedTo);
            String assignedTo = task.getAsString(TaskColumn.assignedTo);
            assignedToView.setVisibility(Helper.isNullOrEmpty(assignedTo) ? View.INVISIBLE : View.VISIBLE);
            assignedToView.setText("{fa-hand-o-right} " + assignedTo);
        } else if(layout == R.layout.activity_bug_detail && entryType == EntryType.Bug) {
            Bug bug = (Bug) entry;
            Resources resources = getResources();
            Bug.Status status = bug.getStatus();

            ((IconTextView) findViewById(R.id.text_entry_type)).setText("{fa-folder-o} "
                    + ZentaoApplication.getEnumText(this, bug.getBugType()));

            String desc = bug.getAsString(BugColumn.steps);
            if(desc != null) {
                ((TextView) findViewById(R.id.text_entry_desc)).setText(Html.fromHtml(desc));
            }

            TextView statusView = (TextView) findViewById(R.id.text_entry_status);
            statusView.setText(ZentaoApplication.getEnumText(this, status));
            TextView statusIconView = (TextView) findViewById(R.id.icon_entry_status);
            int statusColor = status.accent().primary().value();
            statusIconView.setText("{fa-" + status.icon() + "}");
            statusView.setTextColor(statusColor);
            statusIconView.setTextColor(statusColor);

            TextView assignedToView = (TextView) findViewById(R.id.text_entry_assignedTo);
            String assignedTo = bug.getAsString(BugColumn.assignedTo);
            assignedToView.setVisibility(Helper.isNullOrEmpty(assignedTo) ? View.INVISIBLE : View.VISIBLE);
            assignedToView.setText("{fa-hand-o-right} " + assignedTo);

            ((TextView) findViewById(R.id.text_entry_confirm)).setText(
                    bug.getAsBoolean(BugColumn.confirmed) ? resources.getString(R.string.text_confirmed)
                            : resources.getString(R.string.text_unconfirm));

            TextView severityView = (TextView) findViewById(R.id.text_entry_severity);
            int severity = bug.getAsInteger(BugColumn.severity);
            severityView.setText(String.format(resources.getString(R.string.text_bug_severity), severity));
            severityView.setTextColor(MaterialColorSwatch.PriAccentSwatches[severity].primary().value());
        } else if(layout == R.layout.activity_story_detail && entryType == EntryType.Story) {
            Story story = (Story) entry;
            Resources resources = getResources();
            Story.Status status = story.getStatus();

            ((IconTextView) findViewById(R.id.text_entry_type)).setText("{fa-user} " + resources.getString(R.string.text_story_from)
                    + ZentaoApplication.getEnumText(this, story.getSource()));

            String desc = story.getAsString(StoryColumn.spec);
            if(desc != null) {
                ((TextView) findViewById(R.id.text_entry_desc)).setText(Html.fromHtml(desc));
            }

            ((TextView) findViewById(R.id.text_entry_stage)).setText(ZentaoApplication.getEnumText(this, story.getStage()));

            TextView statusView = (TextView) findViewById(R.id.text_entry_status);
            statusView.setText(ZentaoApplication.getEnumText(this, status));
            TextView statusIconView = (TextView) findViewById(R.id.icon_entry_status);
            int statusColor = status.accent().primary().value();
            statusIconView.setText("{fa-" + status.icon() + "}");
            statusView.setTextColor(statusColor);
            statusIconView.setTextColor(statusColor);

            TextView assignedToView = (TextView) findViewById(R.id.text_entry_assignedTo);
            String assignedTo = story.getAsString(StoryColumn.assignedTo);
            assignedToView.setVisibility(Helper.isNullOrEmpty(assignedTo) ? View.INVISIBLE : View.VISIBLE);
            assignedToView.setText("{fa-hand-o-right} " + assignedTo);
        }
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        entry = null;
    }
}
