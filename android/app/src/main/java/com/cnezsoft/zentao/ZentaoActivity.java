package com.cnezsoft.zentao;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by Catouse on 2015/1/29.
 */
public class ZentaoActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setElevation(10);
    }
}
