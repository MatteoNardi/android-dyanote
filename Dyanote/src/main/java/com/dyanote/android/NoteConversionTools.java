package com.dyanote.android;

import android.text.SpannableStringBuilder;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;

public class NoteConversionTools {

    private NoteConversionTools() {}

    public static void convert(Note note, NoteConverter builder) {
        Log.i("NoteConversionTools", "Converting note " + note.getId() + " with " + builder);
        try {
            execute_conversion(note, builder);
        } catch (Exception e) {
            Log.e("NoteConversionTools", "Error converting xml", e);
        }
    }

    public static void execute_conversion(Note note, NoteConverter builder)
            throws XmlPullParserException, IOException {

        Log.i("NoteConversionTools", note.getXmlBody());

        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(new StringReader(note.getXmlBody()));

        int startBold = -1;
        int startItalic = -1;
        int startHeader = -1;
        int startLink = -1;
        Long current_href = note.getId();

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {

            if(eventType == XmlPullParser.START_DOCUMENT) {
                Log.i("NoteConversionTools", "Start document");

            } else if(eventType == XmlPullParser.START_TAG) {
                String tag = parser.getName();
                int pos = builder.length();
                Log.i("NoteConversionTools", "Start tag " + tag);
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
                            Log.w("NoteConversionTools", "Link with invalid href");
                        }
                    }
                }

            } else if(eventType == XmlPullParser.END_TAG) {
                String tag = parser.getName();
                int end = builder.length();
                int start = -1;
                Log.i("NoteConversionTools", "End tag " + tag);
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
                String text = parser.getText().replace("\n", "");
                Log.i("NoteConversionTools", "Text " + text);
                builder.append(text);
            }
            eventType = parser.next();
        }
        Log.i("XML", "End document");
    }

    // NoteConverter is a builder used to convert a note from its XML representation
    // For example: Xml to SpannableString, Xml to Markdown.
    public interface NoteConverter extends CharSequence, Appendable {
        public abstract void setBold(int start, int end);

        public abstract void setItalic(int start, int end);

        public abstract void setHeader(int start, int end);

        public abstract void setLink(int start, int end, Long href);
    }
}
