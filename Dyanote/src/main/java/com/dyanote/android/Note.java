package com.dyanote.android;

import android.os.Parcel;
import android.os.Parcelable;

public class Note implements Parcelable {
    private long id;
    private String title;
    private String xmlBody;
    private Long parentId;

    public Note(long id, long parentId, String title, String xmlBody) {
        if(title == null)
            throw new IllegalArgumentException("title must not be null");
        if(xmlBody == null)
            throw new IllegalArgumentException("xmlBody must not be null");
        this.id = id;
        this.parentId = parentId;
        this.title = title;
        this.xmlBody = xmlBody;
    }

    public Note(Parcel parcel) {
        this.id = parcel.readLong();
        this.parentId = parcel.readLong();
        this.title = parcel.readString();
        this.xmlBody = parcel.readString();
    }

    public long getId() {
        return id;
    }

    public Long getParentId() {
        return parentId;
    }

    public String getTitle() {
        return title;
    }

    // Get a XML serialization of the note (header and body)
    public String getXmlBody() {
        return xmlBody;
    }


    /* Parcelable implementation */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeLong(parentId);
        parcel.writeString(title);
        parcel.writeString(xmlBody);
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
