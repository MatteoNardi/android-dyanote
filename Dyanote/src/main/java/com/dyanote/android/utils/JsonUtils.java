package com.dyanote.android.utils;

import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtils {
    public static Map<String, String> parseObject(String str) {
        Map<String, String> json = new HashMap<String, String>();;

        JsonReader reader = new JsonReader(new StringReader(str));
        try {
            parseObject(reader, json);
            reader.close();
        } catch (IOException e) {
            Log.e("JsonUtils", "Error parsing Json Object: " + str, e);
        }
        return json;
    }

    private static void parseObject(JsonReader reader, Map<String, String> out) throws IOException {
        reader.beginObject();
        while(reader.hasNext()) {
            out.put(reader.nextName(), reader.nextString());
        }
        reader.endObject();
    }

    public static List< Map<String, String> > parseArray(String str) {
        List< Map<String, String> > jsonList = new ArrayList< Map<String, String> >();

        JsonReader reader = new JsonReader(new StringReader(str));
        try {
            reader.beginArray();
            while(reader.hasNext()) {
                Map<String, String> json = new HashMap<String, String>();
                parseObject(reader, json);
                jsonList.add(json);
            }
            reader.endArray();
            reader.close();
        } catch (IOException e) {
            Log.e("JsonUtils", "Error parsing Json Object: " + str, e);
        }
        return jsonList;
    }
}
