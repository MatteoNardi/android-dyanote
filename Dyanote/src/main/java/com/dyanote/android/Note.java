package com.dyanote.android;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.SpannableString;

public class Note implements Parcelable {
    private long id;
    private String title;
    private String body;

    public Note(long id, String title, String body) {
        this.id = id;
        this.title = title;
        this.body = body;
    }

    public Note(Parcel parcel) {
        this.id = parcel.readLong();
        this.title = parcel.readString();
        this.body = parcel.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(title);
        parcel.writeString(body);
    }

    public static final Parcelable.Creator<Note> CREATOR = new Parcelable.Creator<Note>() {
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        public Note[] newArray(int size) {
            return new Note[size];
        }
    };
}
