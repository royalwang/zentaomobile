package com.cnezsoft.zentao;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.GZIPInputStream;

/**
 * Hppt helpers
 * Created by Catouse on 2015/1/13.
 */
public class Http {

    private static int timeout = 15000;

    /**
     * Timeout getter
     * @return
     */
    public static int getTimeout() {
        return timeout;
    }

    /**
     * Timeout setter
     * @param timeout
     */
    public static void setTimeout(int timeout) {
        Http.timeout = timeout;
    }

    /**
     * Return response string with http 'GET' method
     * @param url
     * @return
     * @throws MalformedURLException
     */
    public static String get(String url, boolean gizip) throws MalformedURLException {
        return get(new URL(url), gizip);
    }

    /**
     * Return response string with http 'GET' method, gzip disabled
     * @param url
     * @return
     * @throws MalformedURLException
     */
    public static String get(String url) throws MalformedURLException {
        return get(new URL(url), false);
    }

    /**
     * Return response string with http 'GET' method
     * @param url
     * @return
     */
    public static String get(URL url, boolean gzip) {
        Log.v("HTTP GET", "url:" + url.toString());
        try {
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(timeout);
            c.setReadTimeout(timeout);
            if(gzip) c.setRequestProperty("Accept-Encoding", "gzip");
            c.connect();
            int status = c.getResponseCode();
            Log.v("HTTP GET", "responseCode:" + status);

            switch (status) {
                case 200:
                case 201:
                    InputStream inputStream = c.getInputStream();
                    String contentEncoding = c.getContentEncoding();
                    Log.v("HTTP GET", "contentEncoding:" + contentEncoding);
                    if(contentEncoding != null && contentEncoding.indexOf("gzip") != -1) {
                        inputStream = new GZIPInputStream(inputStream);
                    }
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    String responseText = sb.toString();
                    Log.v("HTTP GET", "responseText:" + responseText);
                    return responseText;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Return response string with http 'GET' method, gzip disabled
     * @param url
     * @return
     */
    public static String get(URL url) {
        return get(url, false);
    }

    /**
     * Return json object with http 'GET' method
     * @param url
     * @return
     * @throws JSONException
     */
    public static JSONObject getJSON(URL url, boolean gzip) {
        String responseText = get(url, gzip);
        if(responseText != null) {
            try {
                return new JSONObject(responseText);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Return json object with http 'GET' method, gzip disabled
     * @param url
     * @return
     */
    public static JSONObject getJSON(URL url) {
        return getJSON(url, false);
    }

    /**
     * Return json object with http 'GET' method
     * @param url
     * @return
     * @throws JSONException
     */
    public static JSONObject getJSON(String url, boolean gzip) {
        URL address = null;
        try {
            address = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        return getJSON(address, gzip);
    }

    /**
     * Return json object with http 'GET' method, gzip disabled
     * @param url
     * @return
     */
    public static JSONObject getJSON(String url) {
        return getJSON(url, false);
    }

}
