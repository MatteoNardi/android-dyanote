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

import com.dyanote.android.utils.DyanoteSpannableStringBuilder;

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
        this.niceBody = null;
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

    // Get a SpannableString representation of the body (displayable on a TextView)
    public SpannableString getViewRepresentation(DyanoteSpannableStringBuilder builder) {
        if (niceBody == null) {
            try {
                niceBody = convertToSpannableString(builder);
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

    private SpannableString convertToSpannableString(DyanoteSpannableStringBuilder builder)
            throws IOException, XmlPullParserException {

        InputStream in = new ByteArrayInputStream(xmlBody.getBytes("UTF-8"));
        Log.i("XML", "Converting note from xml to spannable string " + xmlBody);
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new StringReader(xmlBody));

            int startBold = -1;
            int startItalic = -1;
            int startHeader = -1;
            int startLink = -1;
            Long current_href = getId();

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {

                if(eventType == XmlPullParser.START_DOCUMENT) {
                    Log.i("XML", "Start document");

                } else if(eventType == XmlPullParser.START_TAG) {
                    String tag = parser.getName();
                    int pos = builder.length();
                    Log.i("XML", "Start tag " + tag);
                    if(tag.equals("b")) {
                        startBold = pos;
                    } else if(tag.equals("i")) {
                        startItalic = pos;
                    } else if(tag.equals("h1")) {
                        startHeader = pos;
                    } else {
                        if (tag.equals("a")) {
                            startLink = pos;
                            try {
                                current_href = Long.parseLong(parser.getAttributeValue(0));
                            } catch (Exception e) {
                                Log.w("XML", "Link with invalid href");
                            }
                        }
                    }

                } else if(eventType == XmlPullParser.END_TAG) {
                    String tag = parser.getName();
                    int end = builder.length();
                    int start = -1;
                    Log.i("XML", "End tag " + tag);
                    if(tag.equals("b")) {
                        start = startBold;
                        builder.setBold(start, end);
                    } else if(tag.equals("i")) {
                        start = startItalic;
                        builder.setItalic(start, end);
                    } else if(tag.equals("h1")) {
                        start = startHeader;
                        builder.setHeader(start, end);
                    } else if (tag.equals("a")) {
                        start = startLink;
                        builder.setLink(start, end, current_href);
                    }

                } else if(eventType == XmlPullParser.TEXT) {
                    Log.i("XML", "Text "+parser.getText());
                    builder.append(parser.getText());
                }
                eventType = parser.next();
            }
            Log.i("XML", "End document");
        } finally {
            in.close();
        }
        return new SpannableString(builder);
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
