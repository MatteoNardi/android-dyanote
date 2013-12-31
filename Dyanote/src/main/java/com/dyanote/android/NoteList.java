package com.dyanote.android;

import java.util.HashMap;
import java.util.Map;

// List of notes
public class NoteList {

    private Map<Long, Note> notes;

    public NoteList() {
        notes = new HashMap<Long, Note>();
    }

    public void addNote(Note note) {
        notes.put(note.getId(), note);
    }

    public void updateNote(Note note) {
        notes.put(note.getId(), note);
    }

    public int size() {
        return notes.size();
    }

    public Note getById(long id) {
        return notes.get(id);
    }

    public Note getRoot() {
        Note root = null;
        for(Note note: notes.values()) {
            if(note.getParentId() == note.getId()) {
                if (root != null)
                    throw new IllegalStateException("More than one root note.");
                root = note;
            }
        }
        if(root == null)
            throw new IllegalStateException("No root note found.");
        return root;
    }
}
