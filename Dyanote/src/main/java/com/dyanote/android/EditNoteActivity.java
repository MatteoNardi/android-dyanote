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
            editor.setText(note.getBody());

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    note.setBody(editor.getText().toString());
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

}
