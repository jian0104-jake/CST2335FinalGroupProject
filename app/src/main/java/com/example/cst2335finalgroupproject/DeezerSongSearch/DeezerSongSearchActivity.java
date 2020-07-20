package com.example.cst2335finalgroupproject.DeezerSongSearch;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cst2335finalgroupproject.DeezerSongSearch.entity.Song;
import com.example.cst2335finalgroupproject.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Entrance activity to use Deezer song search api to search songs of artists.
 */
public class DeezerSongSearchActivity extends AppCompatActivity {

    private static final String SEARCH_TEXT = "SEARCH_TEXT";
    /**
     * the progress bar to show that the app is busy fetching data from server
     */
    private ProgressBar progressBar;

    /**
     * the ListView to show the songs
     */
    private ListView lvSong;

    /**
     * the adapter used for the ListView to show songs
     */
    private SongsAdapter songsAdapter;

    /**
     * the data holder of songs
     */
    private List<Song> songs;

    /**
     * sue shared preferences to store the search text user typed
     */
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deezer_song_search);
        setTitle(getString(R.string.activity_title_deezer_song_search));

        sharedPreferences = getSharedPreferences("DeezerSong", MODE_PRIVATE);
        String searchText = sharedPreferences.getString(SEARCH_TEXT, "");

        /**
         * init components
         */
        EditText edtArtistName = findViewById(R.id.etArtistName);
        edtArtistName.setText(searchText);
        Button btnSearch = findViewById(R.id.btnSearch);
        progressBar = findViewById(R.id.process_bar);
        lvSong = findViewById(R.id.lvSong);

        /**
         * init data holder and adapter
         */
        songs = new ArrayList<>();
        songsAdapter = new SongsAdapter();
        lvSong.setAdapter(songsAdapter);

        lvSong.setOnItemClickListener((parent, view, position, id) -> {
            Song song = songs.get(position);

            Intent intent = new Intent(DeezerSongSearchActivity.this, DeezerSongDetailActivity.class);
            intent.putExtra(DeezerSongDetailActivity.KEY_SONG_NAME, song.getTitle());
            intent.putExtra(DeezerSongDetailActivity.KEY_SONG_DURATION, song.getDurationInMMSS());
            intent.putExtra(DeezerSongDetailActivity.KEY_ALBUM_NAME, song.getAlbumName());
            intent.putExtra(DeezerSongDetailActivity.KEY_ALBUM_COVER, song.getAlbumCover());
            startActivity(intent);
        });

        btnSearch.setOnClickListener((v -> {
            searchArtist(edtArtistName.getText().toString().trim());
        }));
    }

    /**
     * search artist first
     * @param artistName the artist name user enteered
     */
    private void searchArtist(String artistName) {
        if (artistName.isEmpty()) {
            // show alert to tell user input something
            showAlertMessageWithTitle(getString(R.string.dialog_title_alert), getString(R.string.dialog_msg_enter_artist_name));

            return;
        }

        // hide keyboard when start searching.
        // reference to: https://stackoverflow.com/questions/1109022/how-do-you-close-hide-the-android-soft-keyboard-using-java
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SEARCH_TEXT, artistName);
        editor.apply();

        Snackbar snackbar = Snackbar.make(lvSong,
                String.format(getString(R.string.search_song_of_artist_template), artistName),
                Snackbar.LENGTH_LONG);
        snackbar.show();

        // search Artist first
        progressBar.setVisibility(View.VISIBLE);

        QueryArtist queryArtist = new QueryArtist();
        queryArtist.execute(String.format("https://api.deezer.com/search/artist/?q=%s", artistName));
    }

    private void showAlertMessageWithTitle(String title, String message) {
        String alertMsg = message;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(alertMsg);
        alertDialogBuilder.setPositiveButton("Ok", (v, arg) -> {
        });

        alertDialogBuilder.create().show();
    }

    /**
     * an subclass of AsyncTask to be used for artist query
     */
    class QueryArtist extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            String trackListUrl = null;
            try {
                URL url;
                HttpURLConnection urlConnection;
                InputStream response;
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                response = urlConnection.getInputStream();
                // parse artist
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                String result = sb.toString();

                // extract track list url of the first item
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getInt("total") > 0) {
                    JSONObject artist = (JSONObject)jsonObject.getJSONArray("data").get(0);
                    trackListUrl = artist.getString("tracklist");
                }
            } catch(IOException | JSONException e) {
                Log.e("DSS", e.getMessage());
            }

            return trackListUrl;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String trackListUrl) {
            super.onPostExecute(trackListUrl);
            if (trackListUrl == null || trackListUrl.isEmpty()) {
                showAlertMessageWithTitle(getString(R.string.dialog_title_alert),
                        getString(R.string.dialog_msg_no_artist_found));

                progressBar.setVisibility(View.GONE);
                return;
            }
            // query songs of the artist by track list url

            QuerySong querySong = new QuerySong();
            querySong.execute(trackListUrl);
        }
    }

    /**
     * a subclass of AsyncTask to be used to query songs
     */
    class QuerySong extends AsyncTask<String, Integer, List<Song>> {

        @Override
        protected List<Song> doInBackground(String... strings) {
            List<Song> songList = new ArrayList<>();
            try {
                URL url;
                HttpURLConnection urlConnection;
                InputStream response;
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                response = urlConnection.getInputStream();
                // parse artist
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                String result = sb.toString();

                // get song info and store it in songList
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getInt("total") > 0) {
                    JSONArray songArray = (JSONArray)jsonObject.getJSONArray("data");
                    for(int i = 0; i < songArray.length(); i++) {
                        JSONObject songObj = (JSONObject)songArray.get(i);

                        Song song = new Song();
                        song.setTitle(songObj.getString("title"));
                        song.setDuration(songObj.getInt("duration"));

                        JSONObject albumObj = songObj.getJSONObject("album");
                        song.setAlbumName(albumObj.getString("title"));
                        song.setAlbumCover(albumObj.getString("cover_big"));

                        songList.add(song);
                    }
                }
            } catch(IOException | JSONException e) {
                Log.e("DSS", e.getMessage());
            }

            return songList;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(List<Song> songList) {
            super.onPostExecute(songList);

            songs.clear();
            songs.addAll(songList);

            songsAdapter.notifyDataSetChanged();

            progressBar.setVisibility(View.GONE);

            Toast.makeText(DeezerSongSearchActivity.this,
                    String.format(getString(R.string.retrieved_songs_template), songList.size()),
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * song adapter used by list view for songs display
     */
    class SongsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return songs.size();
        }

        @Override
        public Song getItem(int position) {
            return songs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater layoutInflater = getLayoutInflater();
                convertView = layoutInflater.inflate(R.layout.item_song, null);
            }
            TextView tvSongName = convertView.findViewById(R.id.tvSongName);
            TextView tvSongDuration = convertView.findViewById(R.id.tvSongDuration);

            Song song = getItem(position);
            tvSongName.setText(String.format("%d. %s", position, song.getTitle()));
            tvSongDuration.setText(song.getDurationInMMSS());

            return convertView;
        }
    }
}

