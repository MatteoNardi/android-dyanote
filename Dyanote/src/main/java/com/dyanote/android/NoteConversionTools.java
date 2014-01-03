package com.dyanote.android;

import android.text.SpannableStringBuilder;
import android.util.Log;
import android.util.Xml;

import org.markdown4j.Markdown4jProcessor;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

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
        int startBullet = -1;
        Long current_href = note.getId();

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {

            if(eventType == XmlPullParser.START_DOCUMENT) {
                Log.i("NoteConversionTools", "Start document");

            } else if(eventType == XmlPullParser.START_TAG) {
                String tag = parser.getName();
                int pos = builder.length();
                Log.i("NoteConversionTools", "Start tag " + tag);
                if(tag.equals("strong")) {
                    startBold = pos;
                } else if(tag.equals("em")) {
                    startItalic = pos;
                } else if(tag.equals("h1")) {
                    startHeader = pos;
                } else if(tag.equals("li")) {
                    startBullet = pos;
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
                if(tag.equals("strong")) {
                    start = startBold;
                    builder.setBold(start, end);
                } else if(tag.equals("em")) {
                    start = startItalic;
                    builder.setItalic(start, end);
                } else if(tag.equals("h1")) {
                    start = startHeader;
                    builder.setHeader(start, end);
                } else if (tag.equals("a")) {
                    start = startLink;
                    builder.setLink(start, end, current_href);
                } else if (tag.equals("li")) {
                    start = startBullet;
                    builder.setBullet(start, end);
                } else if (tag.equals("br") || tag.equals("ul")) {
                    builder.addNewline();
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

    public static String MarkdownToXML(String s)  {
        String ris = null;
        try {
            ris = new Markdown4jProcessor().process(s);
        } catch (IOException e) {
            Log.e("Markdown", "Error compiling markdown", e);
        }
        Log.i("Markdown", ris);
        return "<note>" + ris + "</note>";
        /*
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        xmlSerializer.setOutput(writer);
        xmlSerializer.startDocument("UTF-8", true);
        xmlSerializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        xmlSerializer.startTag("", "note");

        String[] lines = s.split("\n");
        boolean bold = false;
        boolean italic = false;
        for(String line: lines) {
            if(line.startsWith("# ")) {
                xmlSerializer.startTag("", "h1");
                xmlSerializer.text(line.substring(2));
                xmlSerializer.endTag("", "h1");
            } else {
                if (contains link) {
                    convert link
                } if (contains)

            }
            xmlSerializer.startTag("", "br");
            xmlSerializer.endTag("", "br");
        }

        xmlSerializer.endTag("", "note");
        xmlSerializer.endDocument();

        return writer.toString();*/
    }

    // NoteConverter is a builder used to convert a note from its XML representation
    // For example: Xml to SpannableString, Xml to Markdown.
    public interface NoteConverter extends CharSequence, Appendable {
        public void setBold(int start, int end);

        public void setItalic(int start, int end);

        public void setHeader(int start, int end);

        public void setLink(int start, int end, Long href);

        void addNewline();

        void setBullet(int start, int end);
    }
}
