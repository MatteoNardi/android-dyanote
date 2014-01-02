package com.dyanote.android;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.dyanote.android.utils.Func;
import com.dyanote.android.utils.JsonUtils;
import com.dyanote.android.utils.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

public class NoteRestService {

    // User credentials used to make requests
    private User user;
    // Context needed to access Android resources
    private Context c;

    public NoteRestService(User user, Context c) {
        this.user = user;
        this.c = c;
    }

    public void getAllNotes(Func<NoteList> action) {
        GetAllTask task = new GetAllTask();
        task.execute(action);
    }

    public void upload(Note note) {
        UploadTask task = new UploadTask();
        task.execute(note);
    }

    private class GetAllTask extends AsyncTask<Func<NoteList>, Void, NoteList> {
        Func<NoteList> action = null;

        @Override
        protected NoteList doInBackground(Func<NoteList>... funcs) {
            action = funcs[0];

            NoteList notes = new NoteList();

            String url = String.format(c.getString(R.string.pages_url), user.getEmail());
            String response = NetworkUtils.get(url, user);

            List< Map<String, String> > pages = JsonUtils.parseArray(response);

            for(Map<String, String> page: pages) {
                String title = page.get("title");
                long id = Long.parseLong(page.get("id"));

                // TODO: only get pages which need an update
                String page_url = String.format(c.getString(R.string.page_url), user.getEmail(), id);
                String page_response = NetworkUtils.get(page_url, user);
                Map<String, String> page_details = JsonUtils.parseObject(page_response);

                long parentId = Long.parseLong(page_details.get("parent").split("/")[7]);
                String body = page_details.get("body");

                notes.addNote(new Note(id, parentId, title, body));
            }

            Log.e("GetAllTask", "end.");
            return notes;
        }

        @Override
        protected void onPostExecute(final NoteList notes) {
            if(action != null)
                action.run(notes);
        }
    }

    private class UploadTask extends AsyncTask<Note, Void, Void> {
        @Override
        protected Void doInBackground(Note... notes) {
            Note note = notes[0];
            Log.i("UploadTask", "Updating note");
            String url = String.format(c.getString(R.string.page_url),
                                       user.getEmail(), note.getId());
            JSONObject json = new JSONObject();
            try {
                json.put("title", note.getTitle());
                json.put("body", note.getXmlBody());
                String parentUrl = String.format(c.getString(R.string.page_url),
                        user.getEmail(), note.getParentId());
                json.put("parent", parentUrl);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String response = NetworkUtils.putJson(url, json.toString(), user);
            Log.i("UploadTask", response);
            return null;
        }
    }
}
