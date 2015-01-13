package com.cnezsoft.zentao;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;

import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends ActionBarActivity {

    private ZentaoConfig zentaoConfig;
    private User user;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.button_login);
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

    public void onLogin(View view) {
        if(user == null)
        {
            user = new User();
        }
        user.setAccount(((EditText) findViewById(R.id.edit_account)).getText().toString());
        user.setPasswordMD5(ZentaoAPI.md5(((EditText) findViewById(R.id.edit_password)).getText().toString()));
        user.setAddress(((EditText) findViewById(R.id.edit_address)).getText().toString());

        loginButton.setText(getString(R.string.button_loging));
        loginButton.setEnabled(false);
        new tryLoginTask().execute(user);
    }

    private void handleLoginResult(OperateBundle<Boolean, ZentaoConfig> result){
        loginButton.setText(getString(R.string.button_login));
        loginButton.setEnabled(true);

        String[] loginMessages = getResources().getStringArray(R.array.login_messages);
        if(result.getResult())
        {
            user.setLastLoginTime();
            zentaoConfig = result.getValue();
            Toast.makeText(getApplicationContext(), loginMessages[0], Toast.LENGTH_SHORT).show();
        }
        else
        {
            new AlertDialog.Builder(this)
                .setTitle(getString(R.string.message_login_failed))
                .setMessage(loginMessages[result.getCode()])
                .setNeutralButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            }
                        })
                .show();
        }
    }

    private class tryLoginTask extends AsyncTask<User, Integer, OperateBundle<Boolean, ZentaoConfig>> {
        protected OperateBundle<Boolean, ZentaoConfig> doInBackground(User... users) {
            return ZentaoAPI.tryLogin(users[0]);
        }

        protected void onPostExecute(OperateBundle<Boolean, ZentaoConfig> result) {
            handleLoginResult(result);
        }
    }

}
