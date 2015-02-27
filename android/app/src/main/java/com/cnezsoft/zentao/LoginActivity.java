package com.cnezsoft.zentao;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import java.util.Date;

/**
 * Login activity
 */
public class LoginActivity extends ZentaoActivity {

    private User user;
    private Button loginButton;
    private EditText editAddress;
    private EditText editAccount;
    private EditText editPasswordMd5;
    private boolean autoLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.button_login);
        editAddress = (EditText) findViewById(R.id.edit_address);
        editAccount = (EditText) findViewById(R.id.edit_account);
        editPasswordMd5 = (EditText) findViewById(R.id.edit_password);

        ZentaoApplication context = (ZentaoApplication) getApplicationContext();
        user = context.getUser();

        if(user.getStatus() != User.Status.Unknown)
        {
            editAccount.setText(user.getAccount());
            editAddress.setText(user.getAddress());
            editPasswordMd5.setText(user.getPasswordMD5WithFlag());
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
        if(autoLogin && user.getStatus() != User.Status.Unknown)
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
        setResult(user.getStatus() == User.Status.Online ? RESULT_OK : RESULT_CANCELED);
        super.finish();
    }

    /**
     * Handle the login button click event
     * @param view
     */
    public void onLogin(View view) {
        user.setAddress(editAddress.getText().toString(), false)
            .setAccount(editAccount.getText().toString(), false)
            .setPassword(editPasswordMd5.getText().toString());

        loginButton.setText(getString(R.string.button_loging));
        loginButton.setEnabled(false);
        new tryLoginTask().execute(user);
    }

    /**
     * Handle the login task result
     * @param result
     */
    private void handleLoginResult(OperateBundle<Boolean, ZentaoConfig> result){
        loginButton.setText(getString(R.string.label_login));
        loginButton.setEnabled(true);

        String[] loginMessages = getResources().getStringArray(R.array.login_messages);
        if(result.getResult())
        {
            user.setLastSyncTime(new Date(0)).save();
            user.online();
            ZentaoConfig zentaoConfig = result.getValue();
            ((ZentaoApplication) getApplicationContext()).setZentaoConfig(zentaoConfig);

            finish();
        }
        else
        {
            new AlertDialog.Builder(this)
                .setTitle(getString(R.string.message_login_failed))
                .setMessage(Helper.ifNullOrEmptyThen(result.getMessage(), loginMessages[result.getCode()]))
                .setNeutralButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .show();
        }
    }

    /**
     * Open zentao pro website
     * @param view
     */
    public void exploreZentaoPro(View view) {
        ZentaoApplication.openBrowser(this, "http://www.zentao.net/book/zentaoprohelp.html");
    }

    /**
     * The async task for login in Zentao server
     */
    private class tryLoginTask extends AsyncTask<User, Integer, OperateBundle<Boolean, ZentaoConfig>> {

        protected OperateBundle<Boolean, ZentaoConfig> doInBackground(User... users) {
            return ZentaoAPI.tryLogin(users[0]);
        }

        protected void onPostExecute(OperateBundle<Boolean, ZentaoConfig> result) {
            try {
                handleLoginResult(result);
            } catch(WindowManager.BadTokenException e) {
            }
        }
    }

}
