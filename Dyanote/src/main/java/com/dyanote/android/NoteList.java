package com.dyanote.android;

import android.util.Log;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
            if(note.isRoot()) {
                if (root != null)
                    throw new IllegalStateException("More than one root note.");
                root = note;
            }
        }
        if(root == null)
            throw new IllegalStateException("No root note found.");
        return root;
    }

    // Given two notes, get the list of notes that go from their closest common ancestor (excluded)
    // to destination (included).
    public List<Note> getAncestorNotePath(long destination, long source) {
        // Result path
        List<Note> path = new LinkedList<Note>();
        // Path from root (excluded) to source
        List<Long> p1 = new LinkedList<Long>();
        // Path from root (excluded) to destination
        List<Long> p2 = new LinkedList<Long>();

        for (Note i = notes.get(source); !i.isRoot(); i = notes.get(i.getParentId()))
            p1.add(0, i.getId());

        for (Note i = notes.get(destination); !i.isRoot(); i = notes.get(i.getParentId()))
            p2.add(0, i.getId());
        Log.e("Path", p1.toString());
        Log.e("Path", p2.toString());
        while(p1.size() > 0 && p2.size() > 0 && p1.get(0) == p2.get(0)) {
            p1.remove(0);
            p2.remove(0);
        }
        while(p2.size() > 0) {
            path.add(notes.get(p2.get(0)));
            p2.remove(0);
        }
        Log.e("Path", path.toString());
        return path;
    }
}
