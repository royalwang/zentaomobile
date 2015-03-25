package com.cnezsoft.zentao;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;

/**
 * Login activity
 */
public class LoginActivity extends ZentaoActivity {

    private Button loginButton;
    private EditText editAddress;
    private EditText editAccount;
    private EditText editPasswordMd5;
    private boolean autoLogin = false;
    private ZentaoApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.button_login);
        editAddress = (EditText) findViewById(R.id.edit_address);
        editAccount = (EditText) findViewById(R.id.edit_account);
        editPasswordMd5 = (EditText) findViewById(R.id.edit_password);
        editAddress.setCompoundDrawablesWithIntrinsicBounds(
                new IconDrawable(this, Iconify.IconValue.fa_globe) {{sizeDp(24); colorRes(R.color.primary);}}, null, null, null);
        editAccount.setCompoundDrawablesWithIntrinsicBounds(
                new IconDrawable(this, Iconify.IconValue.fa_user) {{sizeDp(24); colorRes(R.color.primary);}}, null, null, null);
        editPasswordMd5.setCompoundDrawablesWithIntrinsicBounds(
                new IconDrawable(this, Iconify.IconValue.fa_lock) {{sizeDp(24); colorRes(R.color.primary);}}, null, null, null);

        application = (ZentaoApplication) getApplicationContext();
        User user = application.getUser();

        if(user.hasLoginCredentials())
        {
            editAccount.setText(user.getAccount());
            editAddress.setText(user.getAddress());
            editPasswordMd5.setText(user.getPasswordMd5WithFlag());
        }

        if(user.isOfflineAvalibale()) {
            findViewById(R.id.button_login_offline).setVisibility(View.VISIBLE);
        }

        Intent intent = getIntent();
        if(intent != null)
        {
            autoLogin = intent.getBooleanExtra(ZentaoApplication.EXTRA_AUTO_LOGIN, false);
        }
    }

    /**
     * Dispatch onStart() to all fragments.  Ensure any created loaders are
     * now started.
     */
    @Override
    protected void onStart() {
        super.onStart();
        if(autoLogin && application.getUser().hasLoginCredentials())
        {
            onLogin(null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Call this when your activity is done and should be closed.  The
     * ActivityResult is propagated back to whoever launched you via
     * onActivityResult().
     */
    @Override
    public void finish() {
        setResult(application.getUser().getStatus() == User.Status.ONLINE ? RESULT_OK : RESULT_CANCELED);
        super.finish();
    }

    /**
     * Handle the login button click event
     * @param view
     */
    public void onLogin(View view) {
        application.switchUser(editAddress.getText().toString(), editAccount.getText().toString(), editPasswordMd5.getText().toString());

        loginButton.setText(getString(R.string.button_loging));
        loginButton.setEnabled(false);

        final Activity activity = this;

        application.login(new CustomAsyncTask.OnPostExecuteHandler<OperateBundle<Boolean, User>>() {
            @Override
            public void onPostExecute(OperateBundle<Boolean, User> result) {
                loginButton.setText(getString(R.string.label_login));
                loginButton.setEnabled(true);

                String[] loginMessages = getResources().getStringArray(R.array.login_messages);
                if (result.getResult()) {
                    finish();
                } else {
                    new AlertDialog.Builder(activity)
                            .setTitle(getString(R.string.message_login_failed))
                            .setMessage(Helper.ifNullOrEmptyThen(loginMessages[result.getCode()], result.getMessage()))
                            .setNeutralButton(android.R.string.ok,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    })
                            .show();
                }
            }
        });
    }

    /**
     * Open zentao pro website
     * @param view
     */
    public void exploreZentaoPro(View view) {
        ZentaoApplication.openBrowser(this, "http://www.zentao.net/book/zentaoprohelp.html");
    }

    public void onLoginOffline(View view) {
        finish();
    }
}
