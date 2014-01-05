package com.dyanote.android.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import com.dyanote.android.Note;

public class NotesViewPager extends ViewPager {

    public NotesViewPager(Context context) {
        super(context);
    }

    public NotesViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void openNote(long id) {
        NotesPagerAdapter adapter = (NotesPagerAdapter) getAdapter();
        adapter.startUpdate(this);
        int pos = adapter.addNote(id);
        adapter.finishUpdate(this);
        setCurrentItem(pos);
    }

    public Note getCurrentNote() {
        NotesPagerAdapter adapter = (NotesPagerAdapter) getAdapter();
        int current = getCurrentItem();
        return adapter.getNoteAt(current);
    }
}
