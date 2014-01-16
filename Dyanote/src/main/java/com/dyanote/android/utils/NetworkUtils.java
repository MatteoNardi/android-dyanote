package com.dyanote.android.utils;

import android.util.Log;

import com.dyanote.android.User;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public final class NetworkUtils {

    private NetworkUtils() {
    }

    public static void dumpStream(InputStream s) {
        BufferedReader in = new BufferedReader(new InputStreamReader(s));
        String line;
        try {
            while((line = in.readLine()) != null) {
                Log.i("Network", line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String get(String url, User user) {
        return request(url, "GET", user, null, false);
    }

    public static String post(String url, String data) {
        return request(url, "POST", null, data, false);
    }

    public static String postJson(String url, String data, User user) {
        return request(url, "POST", user, data, true);
    }

    public static String putJson(String url, String data, User user) {
        return request(url, "PUT", user, data, true);
    }

    public static String delete(String url, User user) {
        return request(url, "DELETE", user, null, false);
    }

    private static String request(String address, String method, User user, String data, boolean isJson) {
        Log.i("NetworkUtils", method + " request to " + address);
        URL url = null;
        try {
            url = new URL(address);
        } catch (MalformedURLException e) {
            Log.e("Network", "Malformed URL", e);
            return "";
        }
        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            Log.e("Network", "Error opening connection", e);
            return "";
        }

        // Set connection timeouts
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(20000);

        // Set request method
        try {
            conn.setRequestMethod(method);
        } catch (ProtocolException e) {
            Log.e("Network", "Error setting request type", e);
            return "";
        }

        // Add authentication
        if (user != null)
            conn.setRequestProperty("Authorization", "Bearer " + user.getToken());

        // Add authentication
        if (isJson)
            conn.setRequestProperty("Content-Type", "application/json");

        conn.setDoInput(true);
        conn.setUseCaches(false);

        // Add data
        if(data != null) {
            conn.setDoOutput(true);
            if(method == "GET")
                throw new UnsupportedOperationException("Can't add data to get request");
            OutputStream os;
            try {
                os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(data);
                writer.flush();
                writer.close();
                os.close();
            } catch (IOException e) {
                Log.e("Network", "Error writing data to HttpURLConnection", e);
                return "";
            }
        }

        // Connect and get response
        try {
            conn.connect();
            InputStream stream;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() < 300)
                stream = conn.getInputStream();
            else
                stream = conn.getErrorStream();
            BufferedInputStream in = new BufferedInputStream(stream);
            java.util.Scanner s = new java.util.Scanner(in, "UTF-8").useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        } catch (IOException e) {
            Log.e("Network", "Error reading response", e);
            return "";
        } finally {
            conn.disconnect();
        }
    }
}
