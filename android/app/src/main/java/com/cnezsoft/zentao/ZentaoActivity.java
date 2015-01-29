package com.cnezsoft.zentao;

import android.annotation.TargetApi;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import android.view.WindowManager;

import com.cnezsoft.zentao.colorswatch.MaterialColorName;
import com.cnezsoft.zentao.colorswatch.MaterialColorSwatch;

/**
 * Created by Catouse on 2015/1/29.
 */
public class ZentaoActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setElevation(0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void setAccentSwatch(MaterialColorSwatch swatch) {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(swatch.primary().value()));
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(swatch.color(MaterialColorName.C500).value());
    }
}
