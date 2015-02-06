package com.cnezsoft.zentao;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

public class SettingsActivity extends ZentaoActivity {

    private User user;
    private ZentaoApplication application;
    private TextView textZentaoAddress;
    private TextView textZentaoUser;
    private TextView textSyncFreqName;
    private AlertDialog.Builder dialogBuilder = null;
    private Switch switchDisplayNotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        application = (ZentaoApplication) this.getApplicationContext();
        user = application.getUser();

        textZentaoAddress = (TextView) findViewById(R.id.text_zentao_address);
        textZentaoUser = (TextView) findViewById(R.id.text_zentao_user);
        textSyncFreqName = (TextView) findViewById(R.id.text_sync_freq_name);
        switchDisplayNotify = (Switch) findViewById(R.id.switch_display_notify);
        switchDisplayNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                user.setNotify(isChecked).save();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUserInfo();
    }

    private void refreshUserInfo() {
        user.load();
        textZentaoAddress.setText(user.getAddress());
        textZentaoUser.setText(user.getAccount());
        textSyncFreqName.setText(user.getSyncFreqName(this));
        switchDisplayNotify.setChecked(user.isNotify());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(false);

        menu.findItem(R.id.action_feedback).setIcon(new IconDrawable(this, Iconify.IconValue.fa_smile_o){{
            color(Color.WHITE);
            sizeRes(R.dimen.action_bar_icon_size);
        }});
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if(id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void changeFreq(View view) {
        if(dialogBuilder == null) {
            dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setTitle("请选择同步频率");
            dialogBuilder.setItems(SyncFrequency.getAllItemsText(this), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    user.setSyncFrequency(SyncFrequency.values()[which]).save();
                    refreshUserInfo();
                    dialog.dismiss();
                }
            });
            dialogBuilder.create();
        }
        dialogBuilder.show();
    }

    public void openZentao(View view) {
        ZentaoApplication.openBrowser(this, user.getAddress());
    }

    public void onUserLogout(View view) {
        application.logout(this);
        finish();
    }
}
