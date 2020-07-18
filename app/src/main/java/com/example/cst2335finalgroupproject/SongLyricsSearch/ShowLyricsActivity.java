package com.example.cst2335finalgroupproject.SongLyricsSearch;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cst2335finalgroupproject.R;
import com.example.cst2335finalgroupproject.SongLyricsSearch.Database.FavSongDB;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * A new page to display lyrics
 */
public class ShowLyricsActivity extends AppCompatActivity {

    /**
     * A process bar
     */
    private ProgressBar progressBar;

    /**
     * An instance of favorite song database class
     */
    private FavSongDB favSongDB;

    /**
     * An object of SQLiteDatabase which is SQL util class
     */
    private SQLiteDatabase sqLiteDatabase;

    /**
     * Hold the main part of the search hyperlink
     * Append the title and artist to the end when before searching
     */
    private final String URL = "https://api.lyrics.ovh/v1/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyric_show);

        progressBar = findViewById(R.id.lyric_process_bar_show);
        progressBar.setVisibility(View.VISIBLE);

        Intent fromSearch = getIntent();
        String artist = fromSearch.getStringExtra("song_artist");
        String title = fromSearch.getStringExtra("song_title");
        String link = URL + artist + "/" + title;

        Button favButton = findViewById(R.id.lyric_button_fav);
        favButton.setOnClickListener(click -> {
            // Check if a song already exists
            favSongDB = new FavSongDB(this);
            sqLiteDatabase = favSongDB.getWritableDatabase();
            Cursor cursor = sqLiteDatabase.query(FavSongDB.TABLE_NAME,
                    new String[]{FavSongDB.COL_ID, FavSongDB.COL_ARTIST, FavSongDB.COL_TITLE},
                    null, null, null, null, FavSongDB.COL_ID);
            if (cursor.moveToNext()) {
                Toast.makeText(this,"This song already in the Favorite List", Toast.LENGTH_LONG).show();
            } else {
                // add song into database
                ContentValues contentValues = new ContentValues();
                contentValues.put(FavSongDB.COL_ARTIST, artist);
                contentValues.put(FavSongDB.COL_TITLE, title);
                sqLiteDatabase.insert(FavSongDB.TABLE_NAME, "NullColumnName", contentValues);
                Toast.makeText(this,"Successfully Add to Favorite List", Toast.LENGTH_LONG).show();
            }
        });

        Button googleButton = findViewById(R.id.lyric_button_search_with_google);
        googleButton.setOnClickListener(click -> {
            String searchLink = "https://www.google.com/search?q=" + artist + "+" + title;
            Intent launchBrower = new Intent(Intent.ACTION_VIEW, Uri.parse(searchLink));
            startActivity(launchBrower);
        });

        LyricSearch lyricSearch = new LyricSearch();
        lyricSearch.execute(link);

    }

    class LyricSearch extends AsyncTask<String, Integer, String> {

        /**
         *
         */
        private String lyric;

        @Override
        protected String doInBackground(String... strings) {
            try {
                //create a URL object of what server to contact:
                URL url = new URL(strings[0]);

                //open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                Thread.sleep(50);
                publishProgress(20);

                //wait for data:
                InputStream response = urlConnection.getInputStream();

                //JSON reader

                //Build the entire string response:
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, StandardCharsets.UTF_8), 8);
                StringBuilder sb = new StringBuilder();
                Thread.sleep(50);
                publishProgress(40);

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String result = sb.toString(); //result is the whole string
                Log.i("sb", result);

                JSONObject lyricReport = new JSONObject(result);

                //get the double associated with "value" and convert to string
                lyric = lyricReport.getString("lyrics");

                Thread.sleep(50);
                publishProgress(80);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "Done";

        }

        public void onProgressUpdate(Integer... value) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(value[0]);
        }

        public void onPostExecute(String fromDoInBackground) {
            publishProgress(100);
            TextView textView = findViewById(R.id.lyric_content);
            textView.setText(lyric);

            progressBar.setVisibility(View.GONE);
        }
    }
}
