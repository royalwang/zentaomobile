package com.cnezsoft.zentao;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Date;

public class SettingsActivity extends ZentaoActivity {

    private ZentaoApplication application;
    private TextView textZentaoAddress;
    private TextView textZentaoUser;
    private TextView textSyncFreqName;
    private TextView textUserLastSyncTime;
    private TextView textVersionName;
    private AlertDialog.Builder dialogBuilder = null;
    private Switch switchDisplayNotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        application = (ZentaoApplication) this.getApplicationContext();
        final User user = application.getUser();

        textVersionName = (TextView) findViewById(R.id.text_version_name);
        textZentaoAddress = (TextView) findViewById(R.id.text_zentao_address);
        textUserLastSyncTime = (TextView) findViewById(R.id.text_last_sync_time);
        textZentaoUser = (TextView) findViewById(R.id.text_zentao_user);
        textSyncFreqName = (TextView) findViewById(R.id.text_sync_freq_name);
        switchDisplayNotify = (Switch) findViewById(R.id.switch_display_notify);
        switchDisplayNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                user.put(UserAttr.notifyEnable, isChecked);
                application.saveUser();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUserInfo();
    }

    private void refreshUserInfo() {
        User user = application.getUser();
        textVersionName.setText(application.getVersionName());
        textZentaoAddress.setText(user.getAddress());
        textZentaoUser.setText(user.getAccount());
        textSyncFreqName.setText(SyncFrequency.getFreqName(this, user.getSyncFrequency()));
        switchDisplayNotify.setChecked(user.isNotifyEnable());
        textUserLastSyncTime.setText(String.format(getResources().getString(R.string.text_last_sync_time_format), user.getLastSyncTimeStr(this)));
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

//        menu.findItem(R.id.action_feedback).setIcon(new IconDrawable(this, Iconify.IconValue.fa_smile_o){{
//            color(Color.WHITE);
//            sizeRes(R.dimen.action_bar_icon_size);
//        }});
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
//        } else if(id == R.id.action_feedback) {
//            return true;
        } else if(id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void changeFreq(View view) {
        final User user = application.getUser();
        if(dialogBuilder == null) {
            dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setTitle("请选择同步频率");
            dialogBuilder.setItems(SyncFrequency.getAllItemsText(this), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    user.put(UserAttr.syncFrequency, SyncFrequency.values()[which]);
                    application.saveUser();
                    refreshUserInfo();
                    dialog.dismiss();
                }
            });
            dialogBuilder.create();
        }
        dialogBuilder.show();
    }

    public void openZentao(View view) {
        ZentaoApplication.openBrowser(this, application.getUser().getAddress());
    }

    public void onUserLogout(View view) {
        application.logout(this);
        finish();
    }

    public void onResetSyncTime(View view) {
        application.getUser().put(UserAttr.lastSyncTime, new Date(0));
        application.saveUser();
        refreshUserInfo();
    }

    public void onSendFeedback(View view) {
        Intent data=new Intent(Intent.ACTION_SENDTO);
        data.setData(Uri.parse("mailto:" + getString(R.string.email_feedback)));
        data.putExtra(Intent.EXTRA_SUBJECT, "禅道Android客户端（" + textVersionName.getText().toString() + "）意见反馈");
        data.putExtra(Intent.EXTRA_TEXT, "(请畅所欲言)");
        try {
            startActivity(data);
        } catch (ActivityNotFoundException e) {
            new AlertDialog.Builder(this)
                .setMessage(String.format(getString(R.string.text_feedback_alert), getString(R.string.email_feedback)))
                .setNeutralButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .show();
        }
    }
}
