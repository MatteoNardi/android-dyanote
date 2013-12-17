package com.dyanote.android;

import java.util.HashMap;
import java.util.Map;

// List of notes
public class NoteList {

    private Map<Long, Note> notes;

    public NoteList() {
        notes = new HashMap<Long, Note>();
        Note n0 = new Note(0, "Root", "#Title \n This is the *root* note.  \n");
        addNote(n0);
        Note n1 = new Note(1, "Root", "This is note 1.  \n");
        addNote(n1);
        Note n2 = new Note(2, "Root", "This is note 2.  \n");
        addNote(n2);
        Note n3 = new Note(3, "Root", "This is note 3.  \n");
        addNote(n3);
        Note n4 = new Note(4, "Root", "This is note 4.  \n");
        addNote(n4);
        Note n5 = new Note(5, "Root", "This is note 5.  \n");
        addNote(n5);
    }

    private void addNote(Note note) {
        notes.put(note.getId(), note);
    }

    public int size() {
        return notes.size();
    }

    public Note getById(long id) {
        return notes.get(id);
    }
}
