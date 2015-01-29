package com.cnezsoft.zentao;

import java.text.SimpleDateFormat;

/**
 * Created by Catouse on 2015/1/29.
 */
public enum DateFormatType {
    DateTime("yyyy-MM-dd HH:mm"),
    Time("HH:mm"),
    FullTime("HH:mm:ss"),
    Date("yyyy-MM-dd");

    private String pattern;

    private SimpleDateFormat format = null;

    public SimpleDateFormat getFormat() {
        if(format == null) {
            format = new SimpleDateFormat(pattern);
        }
        return format;
    }

    DateFormatType(String format) {
        this.pattern = format;
    }
}
