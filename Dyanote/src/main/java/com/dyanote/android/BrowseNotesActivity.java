package com.dyanote.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.style.ClickableSpan;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dyanote.android.ui.NotesPagerAdapter;
import com.dyanote.android.ui.NotesViewPager;
import com.dyanote.android.utils.DyanoteSpannableStringBuilder;

public class BrowseNotesActivity extends ActionBarActivity {

    // Code identifying the LoginActivity request
    public static final int LOGIN_REQUEST = 0;
    // Code identifying the EditNoteActivity request
    public static final int EDIT_REQUEST = 1;

    NotesViewPager pager; // The widget displaying the pager

    NotesPagerAdapter pagerAdapter; // The model of the ViewPager
    NoteList notes;
    User user;
    NoteRestService restService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup widgets.
        notes = new NoteList();
        setContentView(R.layout.activity_browse);
        pagerAdapter = new NotesPagerAdapter(getSupportFragmentManager());
        pager = (NotesViewPager) findViewById(R.id.pager);
        pager.setAdapter(pagerAdapter);

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
        restService = new NoteRestService(user, getApplicationContext());
        notes = restService.getAllNotes();
        Log.i("BrowseNotesActivity", notes.size() + " notes.");
        pagerAdapter.updateNotes(notes);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == LOGIN_REQUEST) {
            user = data.getParcelableExtra("user");
            if(!user.isLoggedIn())
                new Exception("Login Activity Fail").printStackTrace();
            user.saveToSettings(getPreferences(MODE_PRIVATE));
            loadNotes();
        } else if (resultCode == Activity.RESULT_OK && requestCode == EDIT_REQUEST) {
            Note note = data.getParcelableExtra("note");
            Log.i("BrowseNotesActivity", "Updating note..");
            restService.upload(note);
            notes.updateNote(note);
            pagerAdapter.updateNotes(notes);
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
        }
        return super.onOptionsItemSelected(item);
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
