package com.cnezsoft.zentao;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cnezsoft.zentao.colorswatch.MaterialColorName;
import com.cnezsoft.zentao.colorswatch.MaterialColorSwatch;
import com.cnezsoft.zentao.control.ControlBindInfo;
import com.cnezsoft.zentao.data.DAO;
import com.cnezsoft.zentao.data.DataEntry;
import com.cnezsoft.zentao.data.DataEntryFactory;
import com.cnezsoft.zentao.data.EntryType;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;

public class DetailActivity extends ZentaoActivity {
    public static final String ARG_ENTRY_TYPE = "com.cnezsoft.zentao.ENTRY_TYPE";
    public static final String ARG_ID = "com.cnezsoft.zentao.ID";

    protected int entryId;
    protected EntryType entryType;
    private ZentaoApplication application;

    private ArrayList<HashMap<String, Object>> metaData;
    private MetaListAdapter metaAdapter;

    private LinearLayout metaContainer;

    protected TextView iconView;
    protected TextView iconTextView;
    protected TextView iconBackView;
    protected TextView titleTextview;
    protected TextView subtitleTextview;
    protected TextView headTitletview;
    protected ProgressBar progressBar;
    protected DataEntry entry;
    private NumberFormat progressFormat;
    private DAO dao;

    private boolean loadFromRemote = true;
    private Menu menu;
    private MenuItem loadingMenuItem;

    protected void setLoadFromRemote(boolean loadFromRemote) {
        this.loadFromRemote = loadFromRemote;
    }

    protected DAO getDAO() {
        if(dao == null) {
            dao = new DAO(this);
        }
        return dao;
    }

    protected DataEntry loadData() {
        getDAO();
        entry = DataEntryFactory.create(entryType, dao.query(entryType, entryId));
        if(entry!=null && entry.isUnread()) {
            entry.markRead();
            dao.save(entry);
        }
        return entry;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(dao != null) {
            dao.close();
        }
    }

    @Override
    protected void onDestroy() {
        if(dao != null) {
            dao.close();
            dao = null;
        }
        super.onDestroy();
    }

    protected void executeLoadData() {
        new LoadDataTask().execute(this);
    }

    private void onDataLoad(DataEntry dataEntry) {
        if(dataEntry == null) {
            Toast.makeText(application,
                    String.format(getString(R.string.text_entry_not_found_format), entryId, ZentaoApplication.getEnumText(this, entryType)),
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        metaData.clear();
        display(dataEntry);

        metaContainer.removeAllViews();
        for(int i = 0; i < metaAdapter.getCount(); ++i) {
            metaContainer.addView(metaAdapter.getView(i, null, null));
        }

        if(loadFromRemote) {
            setLoadFromRemote(false);
            sendGetFromRemoteMessage();
        } else {
            setLoadingIcon(false);
        }
    }

    protected void sendGetFromRemoteMessage() {
        Intent intent = new Intent(Synchronizer.MESSAGE_IN_GET_ENTRY);
        intent.putExtra("type", entryType.name());
        intent.putExtra("id", entry.key());
        sendBroadcast(intent);
    }

    protected void setLoadingIcon(boolean display) {
        if(menu == null) {
            return;
        }

        if(loadingMenuItem == null) {
            loadingMenuItem = menu.findItem(R.id.action_loading);
        }

        if(display) {
            ProgressBar loadingProgress = (ProgressBar) loadingMenuItem.setVisible(true).getActionView();
            loadingProgress.setIndeterminate(true);
            loadingProgress.setAlpha(0.54f);
            loadingProgress.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(loadingMenuItem.isVisible()) {
                        setLoadingIcon(false);
                    }
                }
            }, 15000);
        } else {
            loadingMenuItem.setVisible(false);
        }
    }

    protected void display(DataEntry dataEntry) {
    }

    protected void displayMeta(HashMap<String, Object> meta) {
        metaData.add(meta);
    }

    protected void displayHtmlMeta(String name, String html, Object icon, Object iconBack, boolean divider) {
        HashMap<String, Object> meta = new HashMap<>();
        meta.put("name", name);

        meta.put("icon", icon);
        if(icon!=null && iconBack == null) {
            iconBack = "{fa-circle-thin}";
        }
        meta.put("iconBack", iconBack);

        if(!divider) {
            meta.put("divider", false);
        }

        if(Helper.isNullOrEmpty(html)) {
            meta.put("content", "");
        } else {
            Matcher imageMatcher = Helper.getImageMatcher(html);
            ArrayList<String> imageSet = new ArrayList<>();
            StringBuffer sb = new StringBuffer();
            int count = 1;
            String imageTextFormat = getString(R.string.text_image_format);
            boolean find = false;
            while(imageMatcher.find()) {
                find = true;
                String source = imageMatcher.group(1);
                if(source.startsWith("data/upload/")) {
                    source = application.getUser().getAddress() + "/" + source;
                }
                imageSet.add(source);
                imageMatcher.appendReplacement(sb, String.format(imageTextFormat, count++));
            }
            meta.put("imageSet", imageSet);
            meta.put("content", Html.fromHtml(find ? sb.toString() : html));
        }

        displayMeta(meta);
    }

    protected void displayHtmlMeta(String name, String html, Object icon) {
        displayHtmlMeta(name, html, icon, null, true);
    }

    protected void displayMeta(String name, Object content, Object icon, Object iconBack, boolean divider) {
        HashMap<String, Object> meta = new HashMap<>();
        meta.put("name", name);
        meta.put("content", content);
        meta.put("icon", icon);
        if(icon!=null && iconBack == null) {
            iconBack = "{fa-circle-thin}";
        }
        meta.put("iconBack", iconBack);

        if(!divider) {
            meta.put("divider", false);
        }
        metaData.add(meta);
    }

    protected void displayMeta(String name, Object content, boolean divider) {
        displayMeta(name, content, "", "", divider);
    }

    protected void displayMeta(String name, Object content, Object icon, boolean divider) {
        displayMeta(name, content, icon, null, divider);
    }

    protected void displayMeta(String name, Object content, Object icon, Object iconBack) {
        displayMeta(name, content, icon, iconBack, true);
    }

    protected void displayMeta(String name, Object content, Object icon) {
        displayMeta(name, content, icon, null);
    }

    protected void displayMeta(String name, Object content) {
        displayMeta(name, content, null);
    }

    protected void displayOnTextview(TextView textview, String title) {
        textview.setText(title);
        textview.setVisibility(title == null ? View.GONE : View.VISIBLE);
    }

    protected void displayOnTextview(int id, String title) {
        displayOnTextview((TextView) findViewById(id), title);
    }

    protected void displayOnTextview(TextView textview, ControlBindInfo info) {
        if(textview != null) {
            textview.setVisibility(info.visibility);
            if(info.visibility == View.VISIBLE) {
                textview.setText(info.text);
                textview.setTextColor(info.textColor);
                textview.setBackgroundColor(info.backColor);
            }
        }
    }

    protected void displayOnTextview(int id, ControlBindInfo info) {
        displayOnTextview(findTextViewById(id), info);
    }

    protected void displayTitle(String title) {
        displayOnTextview(titleTextview, title);
    }

    protected void displaySubtitle(String title) {
        displayOnTextview(subtitleTextview, title);
    }

    protected void displayHeadTitle(String title) {
        displayOnTextview(headTitletview, title);
    }

    protected void displayProgress(float progress, int color) {
        if(progressFormat == null) {
            progressFormat = NumberFormat.getPercentInstance();
            progressFormat.setMaximumFractionDigits(1);
        }
        displayOnTextview(R.id.text_progress, progressFormat.format(progress));

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress((int) Math.ceil(progress * 100));

        color = color != 0 ? color : getSwatch().primary().value();
        setProgressBarColor(color);
    }

    protected void displayProgress(float progress) {
        displayProgress(progress, 0);
    }

    protected void displayStatusIcon(String icon, int iconColor, String backIcon, int backIconColor) {
        displayOnTextview(R.id.status_icon,
                new ControlBindInfo(icon, iconColor, 0, icon != null ? View.VISIBLE : View.GONE));
        displayOnTextview(R.id.status_icon_back,
                new ControlBindInfo(backIcon, backIconColor, 0, backIcon != null ? View.VISIBLE : (icon != null ? View.INVISIBLE : View.GONE)));
    }

    protected void displayStatus(String statusTitle, String statusInfo) {
        displayOnTextview(R.id.text_status, statusTitle);
        displayOnTextview(R.id.text_status_info, statusInfo);
    }

    protected void displayStatus(ControlBindInfo statusTitle, ControlBindInfo statusInfo) {
        displayOnTextview(R.id.text_status, statusTitle);
        displayOnTextview(R.id.text_status_info, statusInfo);
    }

    protected void displayStatus(Enum status, ControlBindInfo statusInfo) {
        if(statusInfo.textColor == 0) {
            statusInfo.textColor = findTextViewById(R.id.text_status_info).getTextColors().getDefaultColor();
        }
        try {
            IAccentIcon accentIcon = (IAccentIcon) status;
            displayStatus(new ControlBindInfo(ZentaoApplication.getEnumText(this, status), accentIcon.accent().primary().value()), statusInfo);
            displayStatusIcon("{fa-" + accentIcon.icon() + "}", accentIcon.accent().primary().value());
        } catch (ClassCastException e) {
            displayStatus(new ControlBindInfo(ZentaoApplication.getEnumText(this, status), findTextViewById(R.id.text_status).getTextColors().getDefaultColor()), statusInfo);
        }
    }

    protected void displayStatusIcon(String icon, int iconColor) {
        displayStatusIcon(null, 0, icon, iconColor);
    }

    protected void displayId(String title) {
        displayOnTextview(R.id.text_id, title);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application = (ZentaoApplication) getApplicationContext();

        // get arguments from intent
        Intent intent = getIntent();
        if(intent == null) {
            throw new NullPointerException("Can't get intent object.");
        }
        String type = intent.getStringExtra(ARG_ENTRY_TYPE);
        if(type != null) {
            entryType = EntryType.valueOf(type);
        }
        int tempId = intent.getIntExtra(ARG_ID, -1);
        if(tempId > -1) {
            entryId = tempId;
        } else {
            throw new NullPointerException("Can't get ID form intent object.");
        }

        setContentView(R.layout.activity_detail);

        metaData = new ArrayList<>();
        metaAdapter = new MetaListAdapter(this, metaData);
        metaAdapter.setClickable(false);
        metaContainer = (LinearLayout) findViewById(R.id.meta_container);

        iconView = (TextView) findViewById(R.id.icon);
        iconTextView = (TextView) findViewById(R.id.icon_text);
        iconBackView = (TextView) findViewById(R.id.icon_back);

        titleTextview = (TextView) findViewById(R.id.text_title);
        subtitleTextview = (TextView) findViewById(R.id.text_subtitle);
        headTitletview = (TextView) findViewById(R.id.text_head_title);
        progressBar = ((ProgressBar) findViewById(R.id.progressBar));

        listenMessage();
        executeLoadData();
        setAccentSwatch(entryType.accent());
    }

    /**
     * List the "Synchronizer.MESSAGE_OUT_SYNC" message
     * Override this to listen more message type
     */
    protected void listenMessage() {
        listenMessage(Synchronizer.MESSAGE_OUT_SYNC);
        listenMessage(Synchronizer.MESSAGE_OUT_GET_ENTRY);
    }

    @Override
    protected void onReceiveMessage(Intent intent) {
        Log.v("DETAIL", "Message: " + intent.getAction());
        String action = intent.getAction();
        if(action.equals(Synchronizer.MESSAGE_OUT_SYNC) || action.equals(Synchronizer.MESSAGE_OUT_GET_ENTRY)) {
            executeLoadData();
        }
    }

    @Override
    protected void setAccentSwatch(MaterialColorSwatch swatch) {
        super.setAccentSwatch(swatch);
        findViewById(R.id.header).setBackgroundColor(swatch.color(MaterialColorName.C600).value());
        findViewById(R.id.status_container).setBackgroundColor(swatch.color(MaterialColorName.C50).value());
        setProgressBarColor(swatch.primary().value());
        if(!setIcon()) {
            iconView.setVisibility(View.GONE);
            iconBackView.setVisibility(View.GONE);
            iconTextView.setVisibility(View.GONE);
        }
    }

    protected void setProgressBarColor(int color) {
        progressBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    protected boolean setIcon(MaterialColorSwatch swatch, TextView iconView, TextView iconBackView, TextView iconTextView) {
        iconBackView.setVisibility(View.VISIBLE);
        iconTextView.setVisibility(View.VISIBLE);
        iconView.setVisibility(View.VISIBLE);
        iconView.setText("{fa-" + entryType.icon() + "}");
        iconBackView.setTextColor(swatch.color(MaterialColorName.A400).value());
        return true;
    }

    protected boolean setIcon() {
        return setIcon(getSwatch(), iconView, iconBackView, iconTextView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(ZentaoApplication.getEnumText(this, entryType));

        setLoadingIcon(true);
        return true;
    }

    private class LoadDataTask extends AsyncTask<Context, Integer, DataEntry> {
        @Override
        protected DataEntry doInBackground(Context... params) {
            return loadData();
        }

        @Override
        protected void onPostExecute(DataEntry dataEntry) {
            super.onPostExecute(dataEntry);
            onDataLoad(dataEntry);
        }
    }
}
