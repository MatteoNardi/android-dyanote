package com.dyanote.android;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.SpannableString;

public class Note implements Parcelable {
    private long id;
    private String title;
    private String xmlBody;
    static SpannableString niceBody;

    public Note(long id, String title, String xmlBody) {
        this.id = id;
        this.title = title;
        this.xmlBody = xmlBody;
    }

    public Note(String xml) {
        convertFromXML(xml);
    }

    public Note(Parcel parcel) {
        this.id = parcel.readLong();
        this.title = parcel.readString();
        this.xmlBody = parcel.readString();
        this.niceBody = null;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    // Get a XML serialization of the note (header and body)
    public String getXml() {
        return convertToXML();
    }

    // Get a SpannableString representation of the body (displayable on a TextView)
    public SpannableString getViewRepresentation() {
        if (niceBody == null)
            niceBody = convertToSpannableString();
        return niceBody;
    }

    // Get a Markdown-like representation of the body
    public String getEditRepresentation() {
        return convertToMarkdown();
    }

    // Update the note body from the Markdown representation of the body
    public void setFromEditRepresentation(String markdown) {
        convertFromMarkdown(markdown);
    }



    /* Serialization and conversion methods */

    private void convertFromXML(String xml) {
        // TODO: implement this once we'll use XML APIs to get notes
    }

    private String convertToXML() {
        return "";
    }

    private SpannableString convertToSpannableString() {
        return new SpannableString(xmlBody);
    }

    private String convertToMarkdown() {
        return xmlBody;
    }

    private void convertFromMarkdown(String markdown) {
        xmlBody = markdown;
    }

    /* Parcelable implementation */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
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
