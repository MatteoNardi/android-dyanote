package com.dyanote.android;

import android.text.SpannableString;

public class Note {
    private long id;
    private String title;
    private String body;

    public Note(long id, String title, String body) {
        this.id = id;
        this.title = title;
        this.body = body;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public SpannableString getRepresentation() {
        return new SpannableString(body);
    }
}
