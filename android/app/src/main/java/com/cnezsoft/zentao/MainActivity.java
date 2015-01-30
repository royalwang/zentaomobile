package com.cnezsoft.zentao;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.cnezsoft.zentao.data.DAO;
import com.cnezsoft.zentao.data.DataEntry;
import com.cnezsoft.zentao.data.EntryType;
import com.cnezsoft.zentao.data.Todo;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Main activity
 */
public class MainActivity extends ZentaoActivity {
    /**
     * Used to store Zentao application context
     */
    private ZentaoApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check user status
        application = (ZentaoApplication) getApplicationContext();
        application.checkUserStatus(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ZentaoApplication.LOGIN_REQUEST)
        {
            // Show message
            Toast.makeText(application, getText(resultCode == RESULT_OK ? R.string.message_login_success : R.string.message_login_failed), Toast.LENGTH_SHORT).show();

            // Start syncService
            startService(new Intent(this, ZentaoSyncService.class));
        }
    }

    /**
     * Handle the click event: open the login activity
     * @param view
     */
    public void openLoginActivity(View view) {
        application.login(this, false);
    }

    public void showDatabase(View view) {
        String logKey = "DATABASE TEST";
        long now = new Date().getTime();
        long minute = 60*1000;
        long counter = 0;
        Set<Todo> todos;
        Set<DataEntry> entries = new HashSet<>();
        DAO dao = new DAO(this);

        Log.d(logKey, "*************** show database ***************");
        for(EntryType type: new EntryType[] {EntryType.Todo, EntryType.Task}) {
            counter = dao.count(type);
            Log.d(logKey, type.name() + " count: " + counter);
            if(counter > 0) {
                final Cursor cursor = dao.query(type);
                while (cursor.moveToNext()) {
                    Log.d(logKey, type.name() + ": " + new DataEntry(type){{fromCursor(cursor);}}.toJSONString());
                }
            }
            Log.d(logKey, "---------------------------------------------");
        }
    }
}
