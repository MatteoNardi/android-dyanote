package com.dyanote.android.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.dyanote.android.Note;

import java.util.ArrayList;
import java.util.List;

public class NotesPagerAdapter extends FragmentStatePagerAdapter {

    // Currently opened notes, from the root note to the current note
    private List<Note> notes;
    // Fragments of the currently opened notes, from the root note to the current note
    private List<NoteFragment> fragments;

    public NotesPagerAdapter(FragmentManager fm) {
        super(fm);
        this.notes = new ArrayList<Note>();
        this.fragments = new ArrayList<NoteFragment>();
    }

    // Insert a new note after its parent (Which must be already opened)
    // (Close the other opened notes).
    // Return position of the added page
    public int open(Note note) {
        Log.i("NotesPagerAdapter", "Opening note " + note.getId());

        // Close all descendants of parent
        closeChildren(note.getParentId());

        // Add note
        notes.add(note);
        int position = notes.size() - 1;
        reloadFragmentAt(position);
        return position;
    }

    // Close all open descendants of the given note.
    private void closeChildren(long parentId) {
        // Search for parent note
        int position = findPosition(parentId);
        if (position == -1)
            Log.i("NotesPagerAdapter", "Parent not found");

        // Close notes opened after parent
        while (notes.size() > position + 1)
            notes.remove(position + 1);
    }

    public void close(Note note) {
        Log.i("NotesPagerAdapter", "Closing note " + note.getId());

        closeChildren(note.getParentId());
        notifyDataSetChanged();
    }

    // Returns the position of the opened note with a given id.
    public int findPosition(long id) {
        for(int i = 0; i < notes.size(); i++)
            if (notes.get(i).getId() == id)
                return i;
        return -1;
    }

    // Update the given note, refreshing its fragment
    public void reload(Note note) {
        int position = findPosition(note.getId());
        if (position == -1) {
            Log.e("NotesPagerAdapter", "Reloading a note which was never opened");
            return;
        }
        notes.set(position, note);
        reloadFragmentAt(position);
    }

    // Load a note at a given position.
    // If a fragment already exists it that position we update it. If it doesn't we create it.
    private void reloadFragmentAt(int position) {
        Note note = notes.get(position);
        if(position == fragments.size()) {
            fragments.add(NoteFragment.newInstance(note));
        } else if (position < fragments.size()) {
            fragments.get(position).reload(note);
        } else {
            Log.e("NotesPagerAdapter", "Can't reload fragment at position " + position);
        }
        notifyDataSetChanged();
    }

    // Returns the note opened at the given position.
    public Note getNoteAt(int position) {
        if(position < notes.size())
            return notes.get(position);
        else
            return null;
    }

    // Returns the last opened note.
    public Note getLastNote() {
        return notes.get(notes.size() - 1);
    }

    // Implement FragmentStatePagerAdapter interface.

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return notes.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return getNoteAt(position).getTitle();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
