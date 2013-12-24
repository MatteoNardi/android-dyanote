package com.dyanote.android;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class BrowseNotesActivity extends ActionBarActivity {

    // Code identifying the LoginActivity request
    private static final int LOGIN_REQUEST = 0;
    // Code identifying the EditNoteActivity request
    private static final int EDIT_REQUEST = 1;

    ViewPager pager; // The widget displaying the pager
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
        pagerAdapter = new NotesPagerAdapter(getSupportFragmentManager(), notes);
        pager = (ViewPager) findViewById(R.id.pager);
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
        Log.w("WTF", user.getEmail());
        Log.w("WTF", user.getToken());
        restService = new NoteRestService(user, getApplicationContext());
        notes = restService.getAllNotes();
        pagerAdapter.updateNotes(notes, pager);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == LOGIN_REQUEST) {
            user = (User) data.getParcelableExtra("user");
            if(!user.isLoggedIn())
                new Exception("Login Activity Fail").printStackTrace();
            user.saveToSettings(getPreferences(MODE_PRIVATE));
            loadNotes();
        } else if (resultCode == Activity.RESULT_OK && requestCode == EDIT_REQUEST) {
            Note note = (Note) data.getParcelableExtra("note");
            Log.i("BrowseNotesActivity", "Updating note..");
            restService.upload(note);
            notes.updateNote(note);
            pagerAdapter.updateNotes(notes, pager);
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


    public class NotesPagerAdapter extends FragmentStatePagerAdapter {

        private NoteList notes;

        public NotesPagerAdapter(FragmentManager fm, NoteList notes) {
            super(fm);
            this.notes = notes;
        }

        public void updateNotes(NoteList notes, ViewGroup v) {
            //startUpdate(v);
            this.notes = notes;
            notifyDataSetChanged();
            //finishUpdate(v);
        }

        @Override
        public Fragment getItem(int position) {
            // FIXME: don't pass position.
            Note note = notes.getById(1);
            return NoteFragment.newInstance(note);
        }

        @Override
        public int getCount() {
            return notes.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }


    public static class NoteFragment extends Fragment {

        public static NoteFragment newInstance(Note note) {
            NoteFragment fragment = new NoteFragment();
            Bundle args = new Bundle();
            args.putParcelable("note", note);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            Button editButton = (Button) rootView.findViewById(R.id.editButton);
            final Note note = getArguments().getParcelable("note");
            textView.setText(note.getRepresentation());

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), EditNoteActivity.class);
                    intent.putExtra("note", note);
                    getActivity().startActivityForResult(intent, EDIT_REQUEST);
                }
            });

            return rootView;
        }
    }

}
