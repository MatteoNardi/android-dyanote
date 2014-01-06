package com.dyanote.android.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dyanote.android.Note;
import com.dyanote.android.NoteList;
import com.dyanote.android.NoteRestService;
import com.dyanote.android.R;
import com.dyanote.android.User;
import com.dyanote.android.ui.NotesPagerAdapter;
import com.dyanote.android.ui.NotesViewPager;
import com.dyanote.android.utils.Func;
import com.dyanote.android.utils.NetworkReceiver;

public class BrowseNotesActivity extends ActionBarActivity {

    // Code identifying the LoginActivity request
    public static final int LOGIN_REQUEST = 0;
    // Code identifying the EditNoteActivity request (Edit note)
    public static final int EDIT_REQUEST = 1;
    // Code identifying the EditNoteActivity request (New note)
    public static final int NEW_REQUEST = 2;

    NotesViewPager pager; // The widget displaying the pager
    NotesPagerAdapter pagerAdapter; // The model of the ViewPager

    NoteList notes;
    User user;

    NoteRestService restService;

    // Connectivity information
    private NetworkReceiver receiver = new NetworkReceiver();
    boolean needConnection = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Register BroadcastReceiver to track connection changes.
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver();
        receiver.setActionOnConnected(new Runnable() {
            @Override
            public void run() {
                if(needConnection) {
                    Toast.makeText(getApplicationContext(), com.dyanote.android.R.string.connected_message, Toast.LENGTH_LONG).show();
                    needConnection = false;
                    loadNotes();
                }
            }
        });
        this.registerReceiver(receiver, filter);

        // Setup widgets.
        notes = new NoteList();
        setContentView(R.layout.activity_browse);
        pagerAdapter = new NotesPagerAdapter(getSupportFragmentManager());
        pager = (NotesViewPager) findViewById(R.id.pager);
        pager.setAdapter(pagerAdapter);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int i) {
                updateActionBarTitle();
            }
        });

        // Check if we already have stored credentials.
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        user = User.loadFromSettings(settings);

        if (user.isLoggedIn()) {
            loadNotes();
        } else {
            // No stored credentials: the user must login.
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, LOGIN_REQUEST);
        }
    }

    private void loadNotes() {
        if(!isConnected()) {
            Toast.makeText(getApplicationContext(), R.string.no_network_error, Toast.LENGTH_LONG).show();
            needConnection = true;
            return;
        }
        restService = new NoteRestService(user, getApplicationContext());
        Log.i("BrowseNotesActivity", "Updating notes.");
        restService.getAllNotes(new Func<NoteList>() {
            @Override
            public void run(NoteList ris) {
                notes = ris;
                Log.i("BrowseNotesActivity", notes.size() + " notes.");
                pagerAdapter.updateNotes(notes);
                updateActionBarTitle();
            }
        });
    }

    private void updateActionBarTitle() {
        Note currentNote = pager.getCurrentNote();
        String title = currentNote != null ? currentNote.getTitle() : "Dyanote";
        getSupportActionBar().setTitle(title);
    }

    private boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == LOGIN_REQUEST) {
            Log.i("BrowseNotesActivity", "LOGIN_REQUEST -> Loading notes");
            user = data.getParcelableExtra("user");
            if(!user.isLoggedIn())
                new Exception("Login Activity Fail").printStackTrace();
            user.saveToSettings(getPreferences(MODE_PRIVATE));
            loadNotes();
        } else if (resultCode == Activity.RESULT_OK && requestCode == EDIT_REQUEST) {
            Log.i("BrowseNotesActivity", "EDIT_REQUEST -> Updating note");
            Note note = data.getParcelableExtra("note");
            restService.upload(note);
            notes.updateNote(note);
            updateActionBarTitle();
            pagerAdapter.updateNotes(notes);
        } else if (resultCode == Activity.RESULT_OK && requestCode == NEW_REQUEST) {
            Log.i("BrowseNotesActivity", "NEW_REQUEST -> Creating note");
            Note child = data.getParcelableExtra("note");
            restService.create(child, new Func<Note>() {
                @Override
                public void run(Note newNote) {
                    Note parent = notes.getById(newNote.getParentId());
                    notes.addNote(newNote);
                    parent.appendLinkTo(newNote);
                    pagerAdapter.updateNotes(notes);
                    restService.upload(parent);
                }
            });
        } else {
            Log.w("BrowseNotesActivity", "Unknown result for" + requestCode + " with code" + resultCode);
            finish();
            System.exit(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                User.forgetSettings(getPreferences(MODE_PRIVATE));
                Intent intent = new Intent(this, LoginActivity.class);
                startActivityForResult(intent, LOGIN_REQUEST);
                return true;
            case R.id.action_new:
                Note parent = pager.getCurrentNote();
                Note newNote = new Note(Note.NO_ID, parent.getId(), getString(R.string.new_note_title), getString(R.string.new_note_content));
                if (parent != null) {
                    Intent edit_intent = new Intent(this, EditNoteActivity.class);
                    edit_intent.putExtra("note", newNote);
                    startActivityForResult(edit_intent, BrowseNotesActivity.NEW_REQUEST);
                }
                return true;
            case R.id.action_delete:
                //TODO: create new note
                return true;
            case R.id.action_edit:
                Intent edit_intent = new Intent(this, EditNoteActivity.class);
                edit_intent.putExtra("note", pager.getCurrentNote());
                startActivityForResult(edit_intent, BrowseNotesActivity.EDIT_REQUEST);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            this.unregisterReceiver(receiver);
        }
    }

    public NotesViewPager getPager() {
        return pager;
    }

    public static class EndFragment extends Fragment {
        public static EndFragment newInstance() {
            return new EndFragment();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
            return inflater.inflate(R.layout.fragment_view_end, container, false);
        }
    }
}
