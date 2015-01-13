package com.cnezsoft.zentao;

import java.util.Map;

/**
 * Created by Catouse on 2015/1/13.
 */
public class Helper {

    public static <T> T ifNullThen(T s, T fallback)
    {
        return s == null ? fallback : s;
    }

    public static String ifNullOrEmptyThen(String s, String fallback)
    {
        return (s == null || s.isEmpty()) ? fallback : s;
    }

    public static <K, V> V mapPop(Map<K, V> map, K key)
    {
        V value = map.get(key);
        map.remove(key);
        return value;
    }
}
