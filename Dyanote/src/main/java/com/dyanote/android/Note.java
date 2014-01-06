package com.dyanote.android;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Note implements Parcelable {
    // Id identifying unsaved notes which do not have an ID yet
    public static final long NO_ID = -1;

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

    public void setId(long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getXmlBody() {
        return xmlBody;
    }

    public void setXmlBody(String xmlBody) {
        this.xmlBody = xmlBody;
    }

    public void appendLinkTo(Note child) {
        if(!xmlBody.endsWith("</note>")) {
            Log.e("Note", "Can't add link: doesn't end with </note>");
            return;
        }
        String link = String.format("<a href=\"%d\">%s</a>", child.getId(), child.getTitle());
        xmlBody = xmlBody.replace("</note>", "<br/>" + link + "</note>");
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
