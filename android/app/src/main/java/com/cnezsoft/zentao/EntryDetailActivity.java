package com.cnezsoft.zentao;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.IconTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.cnezsoft.zentao.cache.ImageCache;
import com.cnezsoft.zentao.cache.URLDrawable;
import com.cnezsoft.zentao.colorswatch.MaterialColorName;
import com.cnezsoft.zentao.colorswatch.MaterialColorSwatch;
import com.cnezsoft.zentao.data.Bug;
import com.cnezsoft.zentao.data.BugColumn;
import com.cnezsoft.zentao.data.DAO;
import com.cnezsoft.zentao.data.DataEntry;
import com.cnezsoft.zentao.data.DataEntryFactory;
import com.cnezsoft.zentao.data.DataLoader;
import com.cnezsoft.zentao.data.DataType;
import com.cnezsoft.zentao.data.EntryType;
import com.cnezsoft.zentao.data.IColumn;
import com.cnezsoft.zentao.data.Story;
import com.cnezsoft.zentao.data.StoryColumn;
import com.cnezsoft.zentao.data.Task;
import com.cnezsoft.zentao.data.TaskColumn;
import com.cnezsoft.zentao.data.Todo;
import com.cnezsoft.zentao.data.TodoColumn;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Detail view activity
 */
public class EntryDetailActivity extends ZentaoActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ARG_ENTRY_TYPE = "com.cnezsoft.zentao.ENTRY_TYPE";
    public static final String ARG_ID = "com.cnezsoft.zentao.ID";
    public static final long AUTO_UPDATE_INTERVAL = 1000*60*1;

    protected EntryType entryType = null;
    private long entryId = -1;
    protected boolean inherit = false;
    private DataEntry entry;
    private int layout;
    private boolean firstLoad = true;
    private Menu menu = null;
    private User user;
    private Drawable defaultImageDrawable;
    private ImageCache imageCache;
    private Pattern imgPattern = Pattern.compile("<img.+src=[\"']([^ \"']+)[\"']", Pattern.CASE_INSENSITIVE);

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

        user = ((ZentaoApplication) this.getApplicationContext()).getUser();

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

        imageCache = ImageCache.from(this);

        // init loader
        getLoaderManager().initLoader(entryType.ordinal(), null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_entry_detail, menu);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setTitle(String.format(getResources().getString(R.string.action_detail_format), entryType.text(this)));

        TextView loadingItemView = (TextView) menu.findItem(R.id.action_loading)
                .setVisible(false).getActionView();
        loadingItemView.setText("  {fa-circle-o-notch}  ");
        loadingItemView.setTextSize(loadingItemView.getTextSize() * .65f);
        loadingItemView.setTextColor(Color.WHITE);
        loadingItemView.setAlpha(.5f);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate_normal);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount((int) ((1000 * 15) / animation.getDuration()));
        loadingItemView.setAnimation(animation);
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

    /**
     * Clear loading icon animate
     */
    private void clearLoadingAnimate() {
        if(menu != null) {
            menu.findItem(R.id.action_loading).setVisible(false)
                    .getActionView().clearAnimation();
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(entry == null) {
            entry = DataEntryFactory.create(entryType);
        }
        if(data.moveToNext()) {
            entry.fromCursor(data);
        }

        if(entry.isUnread()) {
            entry.markRead();
            DAO dao = new DAO(this);
            dao.save(entry);
            dao.close();
        }

        displayEntry();

        Log.v("DETAIL", "onLoadFinished, firstLoad=" + firstLoad);

        if(firstLoad) {
            firstLoad = false;
            long nowTime = new Date().getTime();
            long entrySyncTime = entry.getLastSyncTime().getTime();
            if(entrySyncTime <=0 || (nowTime - entrySyncTime) > AUTO_UPDATE_INTERVAL) {
                if(menu != null) {
                    menu.findItem(R.id.action_loading).setVisible(true);
                }

                Intent intent = new Intent(Synchronizer.MESSAGE_IN_GET_ENTRY);
                intent.putExtra("type", entryType.name());
                intent.putExtra("id", entry.key());
                sendBroadcast(intent);
            } else {
                clearLoadingAnimate();
            }
        } else {
            clearLoadingAnimate();
        }
    }

    @Override
    protected void onReceiveMessage(Intent intent) {
        super.onReceiveMessage(intent);
        Log.v("DETAIL", "onReceiveMessage: " + intent.getAction() + ", result: " + intent.getBooleanExtra("result", false));
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
        findViewById(R.id.entry_detail_heading).setBackgroundColor(swatch.color(MaterialColorName.C600).value());
    }

    private void displayHtmlInTextView(final TextView view, String html) {
        if(defaultImageDrawable == null) {
            defaultImageDrawable = new IconDrawable(this, Iconify.IconValue.fa_image);
        }

        final HashMap<String, URLDrawable> imageSet = new HashMap<>();
        Spanned htmlSpanned = Html.fromHtml(html, new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                if(source.startsWith("data/upload/")) {
                    source = user.getAddress() + "/" + source;
                }
                Log.v("DETAIL", ">>>>> getDrawable:" + source);
                Bitmap bitmap = imageCache.getFromMemory(source);
                if(bitmap != null) {
                    BitmapDrawable drawable = new BitmapDrawable(bitmap);

                    drawable.setBounds(Helper.strechWidth(bitmap.getWidth(), bitmap.getHeight(), view.getWidth()));
                    Log.v("DETAIL", "memory image bounds:" + drawable.getBounds().toString());
                    return drawable;
                } else {
                    URLDrawable drawable = new URLDrawable(defaultImageDrawable);
                    imageSet.put(source, drawable);
                    return drawable;
                }
            }
        }, null);
        view.setText(htmlSpanned);
        if(imageSet.size() > 0) {
            imageCache.imgReady(imageSet.keySet().toArray(new String[imageSet.keySet().size()]), new ImageCache.OnImageReadyListener() {
                @Override
                public void onImageReady(ImageCache.ImageRef ref) {
                    Bitmap bitmap = ref.getBitmap();
                    if(bitmap != null) {
                        BitmapDrawable drawable = new BitmapDrawable(bitmap);
                        URLDrawable urlDrawable = imageSet.get(ref.getUrl());
                        urlDrawable.setDrawable(drawable);

                        drawable.setBounds(Helper.strechWidth(bitmap.getWidth(), bitmap.getHeight(), view.getWidth()));
                        urlDrawable.setBounds(drawable.getBounds());
                        Log.v("DETAIL", "remote drawable  bounds:" + drawable.getBounds().toString());
                        Log.v("DETAIL", "remote urlDrawable bounds:" + urlDrawable.getBounds().toString());
                        view.requestLayout();
                    }
                }
            });
        }
    }

    private void displayImage(ViewGroup container, final String source) {
        ImageView imageView = (ImageView) container.findViewWithTag(source);
        if(imageView == null) {
            imageView = new ImageView(this);
            LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            layout.bottomMargin = Helper.convertDpToPx(this, 16);
            imageView.setLayoutParams(layout);
            imageView.setTag(source);
            imageView.setAdjustViewBounds(true);
            container.addView(imageView);
        }
        final Bitmap bitmap = imageCache.getFromMemory(source);
        if(bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            final ImageView finalImageView = imageView;
            imageCache.imgReady(new String[]{source}, new ImageCache.OnImageReadyListener() {
                @Override
                public void onImageReady(ImageCache.ImageRef ref) {
                    if(ref.getBitmap() != null) {
                        finalImageView.setImageBitmap(ref.getBitmap());
                    }
                }
            });
        }
    }

    private void displayEntry() {
        Log.v("DETAIL", "========== displayEntry ===========");
        int accentPri = entry.getAccentPri();
        if(accentPri > 0 && accentPri < MaterialColorSwatch.PriAccentSwatches.length) {
            setAccentSwatch(MaterialColorSwatch.PriAccentSwatches[accentPri]);
        }

        // common attributes
        ViewGroup container = (ViewGroup) findViewById(R.id.entry_detail_container);
        for(IColumn column: entryType.columns()) {
            final TextView view = (TextView) container.findViewWithTag("entry_" + column.name());
            if(view != null) {
                if(column.type() == DataType.HTML) {
                    String html = entry.getAsString(column);
                    if(html != null) {
                        final ViewGroup imageContainer = (ViewGroup) container.findViewWithTag("entry_" + column.name() + "_images");
                        if(imageContainer == null) {
                            displayHtmlInTextView(view, html);
                        } else {
                            view.setText(Html.fromHtml(html));
                            Matcher imageMatcher = imgPattern.matcher(html);
                            while(imageMatcher.find()) {
                                String source = imageMatcher.group(1);
                                if(source.startsWith("data/upload/")) {
                                    source = user.getAddress() + "/" + source;
                                }
                                displayImage(imageContainer, source);
                            }
                        }
                    } else {
                        view.setText("");
                    }
                } else {
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
