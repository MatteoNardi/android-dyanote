package com.dyanote.android;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;

public class EditNoteActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Bundle b = getIntent().getExtras();
        Note note = b.getParcelable("note");

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, NoteEditorFragment.newInstance(note))
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class NoteEditorFragment extends Fragment {

        EditText editor;
        Button saveButton;

        public static NoteEditorFragment newInstance(Note note) {
            NoteEditorFragment fragment = new NoteEditorFragment();
            Bundle args = new Bundle();
            args.putParcelable("note", note);
            fragment.setArguments(args);
            return fragment;
        }

        public NoteEditorFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_edit, container, false);

            editor = (EditText) rootView.findViewById(R.id.editor);
            saveButton = (Button) rootView.findViewById(R.id.saveButton);

            final Note note = getArguments().getParcelable("note");
            MarkdownNoteConverter editRepresentation = new MarkdownNoteConverter();
            NoteConversionTools.convert(note, editRepresentation);
            editor.setText(editRepresentation);

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String newXml = NoteConversionTools.MarkdownToXML(editor.getText().toString());
                    note.setXmlBody(newXml);
                    Log.i("Edit note", "Saving note..");
                    Intent result = new Intent();
                    result.putExtra("note", note);
                    getActivity().setResult(Activity.RESULT_OK, result);
                    getActivity().finish();
                }
            });

            return rootView;
        }
    }

    private static class MarkdownNoteConverter implements NoteConversionTools.NoteConverter {

        StringBuilder builder = new StringBuilder();

        @Override
        public void setBold(int start, int end) {
            builder.insert(start, "**");
            builder.insert(end+2, "**");
        }

        @Override
        public void setItalic(int start, int end) {
            builder.insert(start, '*');
            builder.insert(end+1, '*');
        }

        @Override
        public void setHeader(int start, int end) {
            builder.insert(start, "# ");
            builder.insert(end+2, "\n");
        }

        @Override
        public void setLink(int start, int end, Long href) {
            String link = String.format("[%s](%s)", builder.substring(start, end),  href);
            builder.replace(start, end, link);
        }

        @Override
        public void addNewline() {
            builder.append("\n");
        }

        @Override
        public void setBullet(int start, int end) {
            builder.insert(start, "\n- ");
        }

        // TODO: escape


        /* Thank you Java for making StringBuilder final <3 */
        @Override
        public Appendable append(char c) throws IOException {
            return builder.append(c);
        }

        @Override
        public Appendable append(CharSequence charSequence) throws IOException {
            return builder.append(charSequence);
        }

        @Override
        public Appendable append(CharSequence charSequence, int i, int i2) throws IOException {
            return builder.append(charSequence, i, i2);
        }

        @Override
        public int length() {
            return builder.length();
        }

        @Override
        public char charAt(int i) {
            return builder.charAt(i);
        }

        @Override
        public CharSequence subSequence(int i, int i2) {
            return builder.subSequence(i, i2);
        }
    }
}
