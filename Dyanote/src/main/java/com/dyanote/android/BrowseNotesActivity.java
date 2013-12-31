package com.dyanote.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dyanote.android.utils.DyanoteSpannableStringBuilder;

import java.util.ArrayList;
import java.util.List;

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
        pagerAdapter = new NotesPagerAdapter(getSupportFragmentManager());
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
            this.endFragment = EndFragment.newInstance();
        }

        public void updateNotes(NoteList notes) {
            if(this.notes.size() == 0)
                openNotesId.add(notes.getRoot().getId());
            this.notes = notes;
            notifyDataSetChanged();
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
    }


    public static class NoteFragment extends Fragment {

        public static NoteFragment newInstance(Note note) {
            NoteFragment fragment = new NoteFragment();
            Bundle args = new Bundle();
            if(note == null) {
                Log.e("BrowseNotesActivity", "Can't create note: null");
                return null;
            }
            args.putParcelable("note", note);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
            View rootView = inflater.inflate(R.layout.fragment_view, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            Button editButton = (Button) rootView.findViewById(R.id.editButton);
            final Note note = getArguments().getParcelable("note");

            final Context c = getActivity().getApplicationContext();
            textView.setText(note.getViewRepresentation(new DyanoteSpannableStringBuilder() {
                @Override
                public void setBold(int start, int end) {
                    setSpan(new TextAppearanceSpan(c, R.style.BoldText), start, end, 0);
                }

                @Override
                public void setItalic(int start, int end) {
                    setSpan(new TextAppearanceSpan(c, R.style.ItalicText), start, end, 0);
                }

                @Override
                public void setHeader(int start, int end) {
                    setSpan(new TextAppearanceSpan(c, R.style.HeaderText), start, end, 0);
                }

                @Override
                public void setLink(int start, int end, Long href) {
                    setSpan(new TextAppearanceSpan(c, R.style.LinkText), start, end, 0);
                }
            }));

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
