package com.cnezsoft.zentao;

import android.content.Context;

/**
 * Created by Catouse on 2015/2/5.
 */
public enum SyncFrequency {

    Test(12*1000),
    Quickly(30*1000),
    Normal(5*60*1000),
    Power(30*60*1000),
    TwoHours(2*60*60*1000),
    HalfDay(12*60*60*1000);

    private long milliseconds;

    /**
     * Constructor with milliseconds
     * @param milliseconds
     */
    SyncFrequency(long milliseconds) {
        this.milliseconds = milliseconds;
    }

    /**
     * Get milliseconds
     * @return
     */
    public long getMilliseconds() {
        return milliseconds;
    }

    public static String[] getAllItemsText(Context context) {
        SyncFrequency[] freqs = values();
        String[] texts = new String[values().length];
        for(int i = 0; i < freqs.length; ++i) {
            texts[i] = ZentaoApplication.getEnumText(context, freqs[i]);
        }
        return texts;
    }

    /**
     * Get frequency name
     * @param context
     * @param millseconds
     * @return
     */
    public static String getFreqName(Context context, long millseconds) {
        SyncFrequency freq = null;
        for(SyncFrequency f: values()) {
            if(f.getMilliseconds() == millseconds) {
                freq = f;
                break;
            }
        }
        if(freq != null) {
            return ZentaoApplication.getEnumText(context, freq);
        }
        return String.format(context.getResources().getString(R.string.text_miniute), millseconds/(1000*60));
    }

    /**
     * Get the default option
     * @return
     */
    public static SyncFrequency defaultOption() {
        return SyncFrequency.Normal;
    }
}
