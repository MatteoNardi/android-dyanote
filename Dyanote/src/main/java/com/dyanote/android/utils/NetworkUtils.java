package com.dyanote.android.utils;

import android.util.JsonReader;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class NetworkUtils {

    public static void dumpStream(InputStream s) {
        BufferedReader in = new BufferedReader(new InputStreamReader(s));
        String line = null;
        try {
            while((line = in.readLine()) != null) {
                Log.i("Network", line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String post(String address, String data) {
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
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(20000);
        try {
            conn.setRequestMethod("POST");
        } catch (ProtocolException e) {
            Log.e("Network", "Error setting request type", e);
            return "";
        }
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);

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

        try {
            conn.connect();
            InputStream stream = conn.getResponseCode() == 200 ? conn.getInputStream() : conn.getErrorStream();
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
