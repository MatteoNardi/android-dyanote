package com.dyanote.android;


import android.content.Context;
import android.util.JsonReader;

import com.dyanote.android.utils.NetworkUtils;

import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NoteRestService {

    // User credentials used to make requests
    private User user;
    // Context needed to access Android resources
    private Context c;

    public NoteRestService(User user, Context c) {
        this.user = user;
        this.c = c;
    }

    public NoteList getAllNotes() {
        NoteList notes = new NoteList();

        try {
            URL url = new URL(String.format(c.getString(R.string.pages_url), user.getEmail()));
            System.out.println(user.getEmail());
            System.out.println(user.getToken());
            System.out.println(String.format(c.getString(R.string.pages_url), user.getEmail()));

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "Bearer " + user.getToken());
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.connect();
            BufferedInputStream in = new BufferedInputStream(conn.getInputStream());

            JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
            NetworkUtils.dumpStream(in);
            /*
            reader.beginObject();
            if (reader.hasNext() && reader.nextName() == "access_token") {
                token = reader.nextString();
           */
            reader.close();
            in.close();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            return notes;
        }
        return notes;
    }
}
