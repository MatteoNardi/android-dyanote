package com.dyanote.android.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.dyanote.android.activity.BrowseNotesActivity;
import com.dyanote.android.Note;
import com.dyanote.android.NoteList;

import java.util.ArrayList;
import java.util.List;

public class NotesPagerAdapter extends FragmentStatePagerAdapter {

    private NoteList notes;
    // IDs of the currently opened notes, from the root note to the current note
    private List<Long> openNotesId;
    // Fake fragment shown after all notes
    private Fragment endFragment;

    public NotesPagerAdapter(FragmentManager fm) {
        super(fm);
        this.openNotesId = new ArrayList<Long>();
        this.notes = new NoteList();
        this.endFragment = BrowseNotesActivity.EndFragment.newInstance();
    }

    public void updateNotes(NoteList notes) {
        if(this.notes.size() == 0)
            openNotesId.add(notes.getRoot().getId());
        this.notes = notes;
        notifyDataSetChanged();
    }

    // Insert a new note after its parent (Which must be already opened)
    // (Close the other opened notes).
    // Return position of the added page
    public int addNote(long id) {
        // TODO: This does not work for light-links
        Note note = notes.getById(id);
        Log.e("ADD", note.getXmlBody());
        Long parent = note.getParentId();
        int parent_position = openNotesId.indexOf(parent);
        while (openNotesId.size() > parent_position + 1)
            openNotesId.remove(parent_position + 1);
        openNotesId.add(id);
        notifyDataSetChanged();
        return openNotesId.size() - 1;
    }

    @Override
    public Fragment getItem(int position) {
        if (position >= openNotesId.size())
            return endFragment;
        Note note = notes.getById(openNotesId.get(position));
        return NoteFragment.newInstance(note);
    }

    @Override
    public int getCount() {
        return openNotesId.size() + 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position >= openNotesId.size())
            return "...";
        return notes.getById(openNotesId.get(position)).getTitle();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
