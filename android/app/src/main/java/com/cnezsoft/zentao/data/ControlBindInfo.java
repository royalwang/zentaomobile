package com.cnezsoft.zentao.data;

/**
 * Created by Catouse on 2015/2/28.
 */
public class ControlBindInfo {
    public String text;
    public int textColor;
    public int backColor;

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

    public ControlBindInfo() {
    }
}
