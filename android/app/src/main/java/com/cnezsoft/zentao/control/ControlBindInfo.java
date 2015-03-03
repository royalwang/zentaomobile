package com.cnezsoft.zentao.control;

import android.view.View;
import android.widget.TextView;

/**
 * ControlBindInfo
 * Created by Catouse on 2015/2/28.
 */
public class ControlBindInfo {
    public String text;
    public int textColor;
    public int backColor;

    public int visibility = View.VISIBLE;

    public ControlBindInfo(String text, int textColor, int backColor, int visibility) {
        this.text = text;
        this.textColor = textColor;
        this.backColor = backColor;
        this.visibility = visibility;
    }

    public ControlBindInfo(String text, int textColor, int backColor) {
        this.text = text;
        this.textColor = textColor;
        this.backColor = backColor;
    }

    public ControlBindInfo(String text, int textColor) {
        this.text = text;
        this.textColor = textColor;
    }

    public ControlBindInfo(String text) {
        this.text = text;
    }

    public ControlBindInfo(int visibility) {
        this.visibility = visibility;
    }

    public ControlBindInfo() {
    }

    public void displayOn(View view) {
        view.setVisibility(visibility);
        view.setBackgroundColor(backColor);
        try {
            TextView textView = (TextView) view;
            textView.setText(text);
            textView.setTextColor(textColor);
        } catch (ClassCastException ignore) {}
    }
}
