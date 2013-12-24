package com.dyanote.android.utils;

import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class JSonUtils {
    public static Map<String, String> parseObject(String str) {
        Map<String, String> json = new HashMap<String, String>();

        JsonReader reader = new JsonReader(new StringReader(str));
        try {
            reader.beginObject();
            while(reader.hasNext()) {
                json.put(reader.nextName(), reader.nextString());
            }
            reader.close();
        } catch (IOException e) {
            Log.e("JSonUtils", "Error parsing Json Object: " + str, e);
        }
        return json;
    }
}
