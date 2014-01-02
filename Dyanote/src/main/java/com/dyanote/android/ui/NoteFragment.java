package com.dyanote.android.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dyanote.android.BrowseNotesActivity;
import com.dyanote.android.EditNoteActivity;
import com.dyanote.android.Note;
import com.dyanote.android.R;
import com.dyanote.android.utils.DyanoteSpannableStringBuilder;

public class NoteFragment extends Fragment {

    public static NoteFragment newInstance(Note note) {
        NoteFragment fragment = new NoteFragment();
        Bundle args = new Bundle();
        if(note == null) {
            Log.e("BrowseNotesActivity", "Can't create note: null");
            return null;
        }
        args.putParcelable("note", note);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View rootView = inflater.inflate(R.layout.fragment_view, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        Button editButton = (Button) rootView.findViewById(R.id.editButton);
        final Note note = getArguments().getParcelable("note");

        final BrowseNotesActivity activity = (BrowseNotesActivity) getActivity();
        final Context c = activity.getApplicationContext();
        textView.setText(note.getViewRepresentation(new DyanoteSpannableStringBuilder() {
            @Override
            public void setBold(int start, int end) {
                setSpan(new TextAppearanceSpan(c, R.style.BoldText), start, end, 0);
            }

            @Override
            public void setItalic(int start, int end) {
                setSpan(new TextAppearanceSpan(c, R.style.ItalicText), start, end, 0);
            }

            @Override
            public void setHeader(int start, int end) {
                setSpan(new TextAppearanceSpan(c, R.style.HeaderText), start, end, 0);
            }

            @Override
            public void setLink(int start, int end, Long href) {
                final Long id = href;
                final NotesViewPager pager = activity.getPager();
                setSpan(new TextAppearanceSpan(c, R.style.LinkText), start, end, 0);
                setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        Log.i("Click!", "Open note " + id);
                        pager.openNote(id);
                    }
                }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                // TODO: Save span coordinates.
            }
        }));

        textView.setMovementMethod(LinkMovementMethod.getInstance());
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, EditNoteActivity.class);
                intent.putExtra("note", note);
                activity.startActivityForResult(intent, BrowseNotesActivity.EDIT_REQUEST);
            }
        });
        return rootView;
    }
}
