package com.cnezsoft.zentao.cache;

import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created by Catouse on 2015/2/10.
 */
public class URLDrawable extends BitmapDrawable {

    private Drawable drawable;

    public URLDrawable(Drawable defaultDraw){
        setDrawable(defaultDraw);
    }

    public void setDrawable(Drawable ndrawable){
        drawable = ndrawable;
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable
                .getIntrinsicHeight());
        setBounds(0, 0, drawable.getIntrinsicWidth(), drawable
                .getIntrinsicHeight());
    }

    @Override
    public void draw(Canvas canvas) {
        drawable.draw(canvas);
    }
}