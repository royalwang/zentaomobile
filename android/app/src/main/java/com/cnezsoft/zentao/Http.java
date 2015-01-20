package com.cnezsoft.zentao;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
    public static String get(String url) throws MalformedURLException {
        return get(new URL(url));
    }

    /**
     * Return response string with http 'GET' method
     * @param url
     * @return
     */
    public static String get(URL url) {
        Log.v("HTTP GET", "url:" + url.toString());
        try {
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
            c.setConnectTimeout(timeout);
            c.setReadTimeout(timeout);

            c.connect();
            int status = c.getResponseCode();
            Log.v("HTTP GET", "responseCode:" + status);

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
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
     * Return json object with http 'GET' method
     * @param url
     * @return
     * @throws JSONException
     */
    public static JSONObject getJSON(URL url) {
        String responseText = get(url);
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
     * Return json object with http 'GET' method
     * @param url
     * @return
     * @throws JSONException
     */
    public static JSONObject getJSON(String url) {
        URL address = null;
        try {
            address = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        return getJSON(address);
    }
}
