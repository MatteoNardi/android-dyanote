package com.dyanote.android;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

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
    public String getXmlBody() {
        return xmlBody;
    }

    // Get a SpannableString representation of the body (displayable on a TextView)
    public SpannableString getViewRepresentation(Context c) {
        if (niceBody == null) {
            try {
                niceBody = convertToSpannableString(c);
            } catch (Exception e) {
                Log.e("XML parsing", "Error converting to Spannable String from XML", e);
                return new SpannableString("Error");
            }
        }
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

    private SpannableString convertToSpannableString(Context c) throws IOException, XmlPullParserException {
        InputStream in = new ByteArrayInputStream(xmlBody.getBytes("UTF-8"));
        SpannableStringBuilder output = new SpannableStringBuilder();
        Log.i("XML", "Converting note from xml to spannable string " + xmlBody);
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new StringReader(xmlBody));

            int startBold = -1;
            int startItalics = -1;
            int startHeader = -1;
            int startLink = -1;

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {

                if(eventType == XmlPullParser.START_DOCUMENT) {
                    Log.i("XML", "Start document");

                } else if(eventType == XmlPullParser.START_TAG) {
                    String tag = parser.getName();
                    int pos = output.length();
                    Log.i("XML", "Start tag " + tag);
                    if(tag == "b") {
                        startBold = pos;
                    } else if(tag == "i") {
                        startItalics = pos;
                    } else if(tag == "h1") {
                        startHeader = pos;
                    } else if (tag == "a") {
                        startLink = pos;
                    }

                } else if(eventType == XmlPullParser.END_TAG) {
                    String tag = parser.getName();
                    int end = output.length();
                    int start = -1;
                    Object style = null;
                    Log.i("XML", "End tag " + tag);
                    if(tag == "b") {
                        start = startBold;
                        style = new TextAppearanceSpan(c, R.style.BoldText);
                    } else if(tag == "i") {
                        start = startItalics;
                        style = new TextAppearanceSpan(c, R.style.ItalicText);
                    } else if(tag == "h1") {
                        start = startHeader;
                        style = new TextAppearanceSpan(c, R.style.HeaderText);
                    } else if (tag == "a") {
                        start = startLink;
                        style = new TextAppearanceSpan(c, R.style.LinkText);
                    }
                    if(start < 0 || end < 0)
                        Log.w("XML", "Wrong note format at " + start + ":" + end);
                    else
                        output.setSpan(style, start, end, 0);

                } else if(eventType == XmlPullParser.TEXT) {
                    Log.i("XML", "Text "+parser.getText());
                    output.append(parser.getText());
                }
                eventType = parser.next();
            }
            Log.i("XML", "End document");
        } finally {
            in.close();
        }
        return new SpannableString(output);
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
