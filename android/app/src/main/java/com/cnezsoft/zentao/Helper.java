package com.cnezsoft.zentao;

import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Helper functions
 * Created by Catouse on 2015/1/13.
 */
public class Helper {

    static Rect strechWidth(int widht, int height, int fwidth) {
        return strechWidth(0, 0, widht, height, fwidth);
    }

    static Rect strechWidth(int x, int  y, int width, int height, int fwidth) {
        if(width > fwidth && fwidth > 10) {
            height = (fwidth * height) / width;
        }
        return new Rect(x, y, width, height);
    }

    /**
     * Convert dp to pixel
     * @param context
     * @param dp
     * @return
     */
    public static int convertDpToPx(Context context, float dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    /**
     * Convert pixel to dp
     * @param context
     * @param px
     * @return
     */
    public static int convertPxToDp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    /**
     * Return a fallback value if the given var is null, otherwise return itself
     * @param s
     * @param fallback
     * @param <T>
     * @return <T>
     */
    public static <T> T ifNullThen(T s, T fallback)
    {
        return s == null ? fallback : s;
    }

    /**
     * Return a fallback string if the given string is null or empty, otherwise return itself
     * @param s
     * @param fallback
     * @return String
     */
    public static String ifNullOrEmptyThen(String s, String fallback)
    {
        return (s == null || s.isEmpty()) ? fallback : s;
    }

    /**
     * Determine a string variable is null or empty string
     * @param s
     * @return
     */
    public static boolean isNullOrEmpty(String s)
    {
        return s == null || s.isEmpty();
    }

    /**
     * Return a value from the given map and remove it from the map
     * @param map
     * @param key
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> V mapPop(Map<K, V> map, K key)
    {
        V value = map.get(key);
        map.remove(key);
        return value;
    }

    /**
     *
     * @param jsonArray
     * @return
     */
    public static String[] getStringArrayFromJSON(JSONArray jsonArray){
        String[] stringArray = null;
        int length = jsonArray.length();
        if(jsonArray != null) {
            stringArray = new String[length];
            for(int i=0; i < length; i++) {
                stringArray[i]= jsonArray.optString(i);
            }
        }
        return stringArray;
    }

    public static String formatDate(Date date, DateFormatType formatType) {
        return formatType.getFormat().format(date);
    }

    public static String formatDate(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    public static boolean isInSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isInSameYear(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

    public static boolean isInSameMonth(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
    }
}
