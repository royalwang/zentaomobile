package com.cnezsoft.zentao;

import java.util.Map;

/**
 * Helper functions
 * Created by Catouse on 2015/1/13.
 */
public class Helper {

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
}
