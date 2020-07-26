package com.example.cst2335finalgroupproject.SongLyricsSearch;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.cst2335finalgroupproject.DeezerSongSearch.DeezerSongSearchActivity;
import com.example.cst2335finalgroupproject.R;
import com.example.cst2335finalgroupproject.SoccerMatchHighlights.GameList;
import com.example.cst2335finalgroupproject.SongLyricsSearch.Database.FavSongDB;
import com.example.cst2335finalgroupproject.geodata.GeoDataSource;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static android.text.InputType.TYPE_CLASS_NUMBER;

/**
 * A new page to display lyrics
 */
public class LyricShowActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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

    /**
     * Display the information of the requested song
     */
    private TextView searchTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyric_show);

        // tool bar
        Toolbar toolBar = findViewById(R.id.lyric_toolbar);
        setSupportActionBar(toolBar);

        // navigation bar
        DrawerLayout drawerLayout = findViewById(R.id.lyric_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolBar, R.string.lyric_navigation_open, R.string.lyric_navigation_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.lyric_navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        progressBar = findViewById(R.id.lyric_process_bar_show);
        progressBar.setVisibility(View.VISIBLE);


        Intent fromSearch = getIntent();
        String artist = fromSearch.getStringExtra("song_artist");
        String title = fromSearch.getStringExtra("song_title");

        searchTitle = findViewById(R.id.lyric_search_info);
        searchTitle.setText(artist + "\n" + title);

        String artistURL = "";
        String titleURL = "";
        // deal with special character in url, like space.
        artistURL = Uri.encode(artist, "UTF-8");
        titleURL = Uri.encode(title, "UTF-8");
        String link = URL + artistURL + "/" + titleURL;
        Log.i("URL", link);

        Button favButton = findViewById(R.id.lyric_button_fav);
        favButton.setOnClickListener(click -> {
            // Check if a song already exists
            favSongDB = new FavSongDB(this);
            sqLiteDatabase = favSongDB.getWritableDatabase();
            String query = "SELECT * FROM " + FavSongDB.TABLE_NAME + " WHERE " + FavSongDB.COL_ARTIST + " like ? AND " + FavSongDB.COL_TITLE + " like ?";
            Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{artist, title});
            if (cursor.getCount() <= 0) {
                // add song into database
                ContentValues contentValues = new ContentValues();
                contentValues.put(FavSongDB.COL_ARTIST, artist);
                contentValues.put(FavSongDB.COL_TITLE, title);
                TextView textView = findViewById(R.id.lyric_content);
                contentValues.put(FavSongDB.COL_CONTENT, textView.getText().toString());
                sqLiteDatabase.insert(FavSongDB.TABLE_NAME, "NullColumnName", contentValues);
                Toast.makeText(this, "Successfully Add to Favorite List", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "This song already in the Favorite List", Toast.LENGTH_LONG).show();
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

        progressBar.setVisibility(View.GONE);

    }

    class LyricSearch extends AsyncTask<String, Integer, String> {

        /**
         * Store the search result
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

                // When can not find result, then display a message
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

            }
            // inputStream will throw an FileNotFoundException if url is wrong
            catch (FileNotFoundException e) {
                return "File not found";
            } catch (Exception e) {
                return e.getMessage();
            }
            return "Done";

        }

        public void onProgressUpdate(Integer... value) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(value[0]);
        }

        public void onPostExecute(String fromDoInBackground) {
            publishProgress(100);
            progressBar.setVisibility(View.GONE);

            TextView textView = findViewById(R.id.lyric_content);

            if (!fromDoInBackground.equals("Done")) {
                if (fromDoInBackground.equals("File not found")) {
                    Toast.makeText(getApplicationContext(), "Can not find the song", Toast.LENGTH_SHORT).show();
                    textView.setText("Can not find the song");
                } else {
                    Toast.makeText(getApplicationContext(), fromDoInBackground, Toast.LENGTH_SHORT).show();
                    textView.setText(fromDoInBackground);
                }
            } else {
                textView.setText(lyric);
            }
        }
    }

    /**
     * Used to display tool bar items
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.lyric_toolbar_items, menu);
        return true;
    }

    /**
     * Used to capture and react with tool bar
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //what to do when the menu item is selected:
            case R.id.lyric_toolbar_goto_findcity:
                Intent goToGeoData = new Intent(LyricShowActivity.this, GeoDataSource.class);
                startActivity(goToGeoData);
                break;
            case R.id.lyric_toolbar_goto_soccerhighlight:
                Intent goToSoccer = new Intent(LyricShowActivity.this, GameList.class);
                startActivity(goToSoccer);
                break;
            case R.id.lyric_toolbar_goto_deezer:
                Intent goToDeezer = new Intent(LyricShowActivity.this, DeezerSongSearchActivity.class);
                startActivity(goToDeezer);
                break;
            case R.id.lyric_toolbar_overflow:
                Toast.makeText(this, "This is the Lyrics Search activity, written by Eric Wu", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    /**
     * Implement interface method, to reactive with navigation items
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int selection = 0;
        switch (item.getItemId()) {
            case R.id.lyric_navigation_help_item:
                Toast.makeText(this, R.string.lyric_navagation_help_show, Toast.LENGTH_SHORT).show();
                break;
            case R.id.lyric_navigation_api_item:
                String apiLink = "https://lyricsovh.docs.apiary.io/#";
                Intent launchBrower = new Intent(Intent.ACTION_VIEW, Uri.parse(apiLink));
                startActivity(launchBrower);
                break;
            case R.id.lyric_navigation_donate_item:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Do you want to search the song?");
                builder.setMessage("How much money do you want to donate?");

                EditText editText = new EditText(this);
                editText.setHint(R.string.lyric_navigation_donate_number);
                editText.setInputType(TYPE_CLASS_NUMBER);

                // set up two buttons
                builder.setPositiveButton("Thank You", null);
                builder.setNegativeButton("Cancel", null);

                // create and show the dialog
                AlertDialog alertDialog = builder.create();
                alertDialog.setView(editText, 0, 0, 0, 0);

                alertDialog.setOnShowListener(dialog -> {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                });
                alertDialog.show();
                break;
        }

        DrawerLayout drawerLayout = findViewById(R.id.lyric_drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        return false;
    }
}
