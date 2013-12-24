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

    public int size() {
        return notes.size();
    }

    public Note getById(long id) {
        return notes.get(id);
    }
}
