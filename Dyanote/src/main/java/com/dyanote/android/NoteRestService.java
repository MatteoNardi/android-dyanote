package com.dyanote.android;


import android.content.Context;
import android.util.Log;

import com.dyanote.android.utils.JsonUtils;
import com.dyanote.android.utils.NetworkUtils;

import java.util.List;
import java.util.Map;

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

        String url = String.format(c.getString(R.string.pages_url), user.getEmail());
        String response = NetworkUtils.get(url, user);

        List< Map<String, String> > pages = JsonUtils.parseArray(response);

        for(Map<String, String> page: pages) {
            String body = page.get("body");
            String title = page.get("title");
            long id = Long.parseLong(page.get("id")); //TODO: fixme
            Log.i("NoteRestService", body + title + "###" + id);
            notes.addNote(new Note(id, title, body));
        }

        return notes;
    }
}
