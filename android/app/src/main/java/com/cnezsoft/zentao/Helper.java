package com.cnezsoft.zentao;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.cnezsoft.zentao.cache.ImageCache;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static String getFriendlyDateString(Context context, Date date) {
        Calendar nowCal = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if(isInSameDay(nowCal, cal)) {
            return context.getString(R.string.text_today);
        } else if(isInSameYear(nowCal, cal)) {
            Helper.formatDate(date, context.getString(R.string.text_short_date_format));
        }
        return Helper.formatDate(date, context.getString(R.string.text_long_date_format));
    }

    public static String getFriendlyDateSpan(Context context, Date begin, Date end) {
        Calendar nowCal = Calendar.getInstance();
        Calendar beginCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        beginCal.setTime(begin);
        endCal.setTime(end);

        if(beginCal.get(Calendar.YEAR) > 2025) {
            return context.getString(R.string.text_unknown_date) + " " + Helper.formatDate(begin, DateFormatType.Time) + " ~ " + Helper.formatDate(end, DateFormatType.Time);
        }

        String beginDatePart = "";
        if(nowCal.get(Calendar.YEAR) != beginCal.get(Calendar.YEAR)) {
            beginDatePart = Helper.formatDate(begin, context.getString(R.string.text_long_date_format));
        } else {
            long deltaDays = nowCal.get(Calendar.DAY_OF_YEAR) - beginCal.get(Calendar.DAY_OF_YEAR);
            if(deltaDays == 0) {
                beginDatePart = context.getString(R.string.text_today);
            } else if(deltaDays == 1) {
                beginDatePart = context.getString(R.string.text_yesterday);
            } else if(deltaDays == -1) {
                beginDatePart = context.getString(R.string.text_tomorrow);
            } else {
                beginDatePart = Helper.formatDate(begin, context.getString(R.string.text_short_date_format));
            }
        }

        String endDatePart = "";
        if(!Helper.isInSameDay(beginCal, endCal)) {
            long deltaDays = nowCal.get(Calendar.DAY_OF_YEAR) - endCal.get(Calendar.DAY_OF_YEAR);
            if(deltaDays == 0) {
                endDatePart = context.getString(R.string.text_today);
            } else if(deltaDays == 1) {
                endDatePart = context.getString(R.string.text_yesterday);
            } else if(deltaDays == -1) {
                endDatePart = context.getString(R.string.text_tomorrow);
            } else if(Helper.isInSameMonth(beginCal, endCal)) {
                endDatePart = Helper.formatDate(begin, context.getString(R.string.text_date_day_format));
            } else {
                endDatePart = Helper.formatDate(begin, context.getString(R.string.text_short_date_format));
            }
        }

        return beginDatePart + " ~ " + endDatePart;
    }

    public static String getFriendlyDateTimeSpan(Context context, Date begin, Date end) {
        Calendar nowCal = Calendar.getInstance();
        Calendar beginCal = Calendar.getInstance();
        Calendar endCal = Calendar.getInstance();
        beginCal.setTime(begin);
        endCal.setTime(end);

        if(beginCal.get(Calendar.YEAR) > 2025) {
            return context.getString(R.string.text_unknown_date) + " " + Helper.formatDate(begin, DateFormatType.Time) + " ~ " + Helper.formatDate(end, DateFormatType.Time);
        }

        String beginDatePart = "";
        if(nowCal.get(Calendar.YEAR) != beginCal.get(Calendar.YEAR)) {
            beginDatePart = Helper.formatDate(begin, context.getString(R.string.text_long_date_format));
        } else {
            long deltaDays = nowCal.get(Calendar.DAY_OF_YEAR) - beginCal.get(Calendar.DAY_OF_YEAR);
            if(deltaDays == 0) {
                beginDatePart = context.getString(R.string.text_today);
            } else if(deltaDays == 1) {
                beginDatePart = context.getString(R.string.text_yesterday);
            } else if(deltaDays == -1) {
                beginDatePart = context.getString(R.string.text_tomorrow);
            } else {
                beginDatePart = Helper.formatDate(begin, context.getString(R.string.text_short_date_format));
            }
        }

        String endDatePart = "";
        if(!Helper.isInSameDay(beginCal, endCal)) {
            long deltaDays = nowCal.get(Calendar.DAY_OF_YEAR) - endCal.get(Calendar.DAY_OF_YEAR);
            if(deltaDays == 0) {
                endDatePart = context.getString(R.string.text_today);
            } else if(deltaDays == 1) {
                endDatePart = context.getString(R.string.text_yesterday);
            } else if(deltaDays == -1) {
                endDatePart = context.getString(R.string.text_tomorrow);
            } else if(Helper.isInSameMonth(beginCal, endCal)) {
                endDatePart = Helper.formatDate(begin, context.getString(R.string.text_date_day_format));
            } else {
                endDatePart = Helper.formatDate(begin, context.getString(R.string.text_short_date_format));
            }
        }

        return beginDatePart + " " + Helper.formatDate(begin, DateFormatType.Time) + " ~ " + endDatePart + Helper.formatDate(end, DateFormatType.Time);
    }

    /**
     * Method for Setting the Height of the ListView dynamically.
     * Hack to fix the issue of not showing all the items of the ListView
     * when placed inside a ScrollView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0) {
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private static final Pattern imgPattern = Pattern.compile("<img\\b[^>]*?\\bsrc[\\s]*=[\\s]*[\"']?[\\s]*([^\"'>]*)[^>]*?/?[\\s]*>", Pattern.CASE_INSENSITIVE);
    public static Matcher getImageMatcher(String html) {
        return imgPattern.matcher(html);
    }

    public static void addImageToContainer(Context context, ImageCache imageCache, ViewGroup container, final String source) {
        ImageView imageView = (ImageView) container.findViewWithTag(source);
        if(imageView == null) {
            imageView = new ImageView(context);
            LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            layout.topMargin = Helper.convertDpToPx(context, 8);
            imageView.setLayoutParams(layout);
            imageView.setTag(source);
            imageView.setAdjustViewBounds(true);
            container.addView(imageView);
        }
        final Bitmap bitmap = imageCache.getFromMemory(source);
        if(bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            final ImageView finalImageView = imageView;
            imageCache.imgReady(new String[]{source}, new ImageCache.OnImageReadyListener() {
                @Override
                public void onImageReady(ImageCache.ImageRef ref) {
                    if(ref.getBitmap() != null) {
                        finalImageView.setImageBitmap(ref.getBitmap());
                    }
                }
            });
        }
    }
}
