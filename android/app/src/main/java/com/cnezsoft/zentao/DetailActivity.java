package com.cnezsoft.zentao;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.cnezsoft.zentao.colorswatch.MaterialColorName;
import com.cnezsoft.zentao.colorswatch.MaterialColorSwatch;
import com.cnezsoft.zentao.data.DataEntry;
import com.cnezsoft.zentao.data.EntryType;

public class DetailActivity extends ZentaoActivity {
    public static final String ARG_ENTRY_TYPE = "com.cnezsoft.zentao.ENTRY_TYPE";
    public static final String ARG_ID = "com.cnezsoft.zentao.ID";

    protected int entryId;
    protected EntryType entryType;
    protected ZentaoApplication application;

    protected TextView iconView;
    protected TextView iconTextView;
    protected TextView iconBackView;
    protected DataEntry entry;

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

        iconView = (TextView) findViewById(R.id.icon);
        iconTextView = (TextView) findViewById(R.id.icon_text);
        iconBackView = (TextView) findViewById(R.id.icon_back);
    }

    @Override
    protected void setAccentSwatch(MaterialColorSwatch swatch) {
        super.setAccentSwatch(swatch);
        findViewById(R.id.header).setBackgroundColor(swatch.color(MaterialColorName.C600).value());
        setIcon(swatch, iconView, iconBackView, iconTextView);
    }

    protected void setIcon(MaterialColorSwatch swatch, TextView iconView, TextView iconBackView, TextView iconTextView) {
        iconView.setText("{fa-" + entryType.icon() + "}");
        iconBackView.setTextColor(swatch.color(MaterialColorName.A400).value());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(ZentaoApplication.getEnumText(this, entryType) + " #" + entryId);
        return true;
    }
}
