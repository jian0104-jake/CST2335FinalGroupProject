package com.example.cst2335finalgroupproject.DeezerSongSearch;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cst2335finalgroupproject.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

/**
 * Entrance activity to use Deezer song search api to search songs of artists.
 */
public class DeezerSongSearchActivity extends AppCompatActivity {

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
    private List<String> songs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deezer_song_search);
        setTitle("Search a song(Deezer)");

        /**
         * init components
         */
        EditText edtArtistName = findViewById(R.id.etArtistName);
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
            String song = songs.get(position);

            Intent intent = new Intent(DeezerSongSearchActivity.this, DeezerSongDetailActivity.class);
            intent.putExtra(DeezerSongDetailActivity.KEY_SONG_NAME, song);
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
            // TODO show alert to tell user input something
            String alertMsg = "Please enter the artist name.";
            String title = "Alert";
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(title);
            alertDialogBuilder.setMessage(alertMsg);
            alertDialogBuilder.setPositiveButton("Ok", (v, arg) -> {

            });

            alertDialogBuilder.create().show();

            return;
        }

        Snackbar snackbar = Snackbar.make(lvSong, "Search songs of artist " + artistName, Snackbar.LENGTH_LONG);
        snackbar.show();

        // TODO search Artist first
        progressBar.setVisibility(View.VISIBLE);

        QueryArtist queryArtist = new QueryArtist();
        queryArtist.execute(String.format("https://api.deezer.com/search/artist/?q=%s&output=xml", artistName));
    }

    /**
     * an subclass of AsyncTask to be used for artist query
     */
    class QueryArtist extends AsyncTask<String, Integer, String> {
        private List<String> lstArtistName = new ArrayList<>();

        @Override
        protected String doInBackground(String... strings) {
            lstArtistName.clear();
            try {
                Thread.sleep(500);
            } catch(InterruptedException e) {
                Log.e("DSS", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            songs.clear();

            // TODO hard coded. Move to proper method
            songs.add("Hello");
            songs.add("World");

            songsAdapter.notifyDataSetChanged();

            Toast.makeText(DeezerSongSearchActivity.this, "Retrieved 2 songs",Toast.LENGTH_LONG).show();


            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * a subclass of AsyncTask to be used to query songs
     */
    class QuerySong extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
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
        public Object getItem(int position) {
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
            String songName = (String)getItem(position);
            tvSongName.setText(songName);

            return convertView;
        }
    }
}

