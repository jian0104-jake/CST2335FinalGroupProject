package com.example.cst2335finalgroupproject.SongLyricsSearch;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.example.cst2335finalgroupproject.SongLyricsSearch.Database.LyricSearchHistory;
import com.example.cst2335finalgroupproject.geodata.GeoDataSource;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.text.InputType.TYPE_CLASS_NUMBER;

/**
 * The main activity of Song Lyrics Search
 */

public class LyricSearchActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * The implemented adapter for list view
     */
    private MyListAdapter myAdapter;

    /**
     * A list view used to display search history
     */
    private ListView listView;

    /**
     * A list store the content of list view
     */
    private ArrayList<String> elements = new ArrayList<>();

    /**
     * The instance of SearchHistory class, which is used to record
     * the search history
     */
    private LyricSearchHistory lyricSearchHistory;

    /**
     * The instance of SharedPreferences, which hold some information
     * and let these information could be used when the application starts next time.
     */
    private SharedPreferences prefs = null;

    /**
     * The file name used in SharedPreferences
     */
    private final static String PREFERENCE_NAME = "search_history";

    /**
     * The attribute name in SharedPreferences file which hold the history records.
     */
    private final static String SEARCH_HISTORY = "lyric_history";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyric_search);

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

        // Listview to display search history
        listView = findViewById(R.id.lyric_search_history_list);
        listView.setAdapter(myAdapter = new MyListAdapter());
        // get the history from SharedPreferences, and display in the listview
        elements = (ArrayList<String>) getSearchHistory();
        if (elements.size() > 0) {
            myAdapter.notifyDataSetChanged();
        }

        EditText artistText = findViewById(R.id.lyric_artist_input);
        EditText titleText = findViewById(R.id.lyric_title_input);

        Button btn = findViewById(R.id.lyric_search_button);
        btn.setOnClickListener(click -> {

            String artist = artistText.getText().toString();
            String title = titleText.getText().toString();

            String message = "The artist is: " + artist + "\nThe title is: " + title;

            if (artist.isEmpty() || title.isEmpty()) {
//                Toast.makeText(this, "artist can not be blank", Toast.LENGTH_LONG).show();
                Snackbar.make(artistText, "Input box can not be blank", Snackbar.LENGTH_SHORT).show();
            } else {
                // build up a AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Do you want to search the song?");
                builder.setMessage(message);

                // set up two buttons
                builder.setPositiveButton("Search", (dialog, which) -> {

                    Intent goToLyrics = new Intent(LyricSearchActivity.this, LyricShowActivity.class);
                    goToLyrics.putExtra("song_artist", artist);
                    goToLyrics.putExtra("song_title", title);
                    startActivity(goToLyrics);
                    // save the searching history list by SharedPreference feature
                    lyricSearchHistory = new LyricSearchHistory(artist, title);
                    saveSearchHistory(lyricSearchHistory);
                    // get the history from SharedPreferences, and display in the listview
                    elements = (ArrayList<String>) getSearchHistory();
                    if (elements.size() > 0) {
                        myAdapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("Cancel", null);

                // create and show the dialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        Button toFavList = findViewById(R.id.lyric_button_go_to_fav);
        toFavList.setOnClickListener(click -> {
            Intent goToFav = new Intent(LyricSearchActivity.this, LyricFavSongActivity.class);
            startActivity(goToFav);
        });

        listView.setSelection(elements.size());
        listView.setOnItemClickListener((parent, view, pos, id) -> {
            String[] strArr = myAdapter.getItem(pos).split(" - ");
            artistText.setText(strArr[0]);
            titleText.setText(strArr[1]);
        });

        listView.setOnItemLongClickListener((parent, view, pos, id) -> {
            Toast.makeText(this, "The selected row is: " + pos, Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    /**
     * Save search history using SharedPreferences
     *
     * @param lyricSearchHistory a search history which want to save and used later
     */
    private void saveSearchHistory(LyricSearchHistory lyricSearchHistory) {
        prefs = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);

        // get SharedPreferences
        String longHistory = prefs.getString(SEARCH_HISTORY, "");
        // split by comma, and save to array
        String[] tmpHistory = longHistory.split(",");
        // convert array to arraylist
        List<String> historyList = new ArrayList<>(Arrays.asList(tmpHistory));
        SharedPreferences.Editor editor = prefs.edit();
        if (historyList.size() > 0) {
            // avoid duplicate element
            for (int i = 0; i < historyList.size(); i++) {
                if (lyricSearchHistory.toString().equals(historyList.get(i))) {
                    historyList.remove(i);
                    break;
                }
            }
            // add new history into the arraylist at the index 0
            historyList.add(0, lyricSearchHistory.toString());
            // arraylist limited to maximum 5, delete earliest record.
            if (historyList.size() > 5) {
                historyList.remove(historyList.size() - 1);
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < historyList.size(); i++) {
                sb.append(historyList.get(i) + ",");
            }
            editor.putString(SEARCH_HISTORY, sb.toString());
        } else {
            // when the first search
            editor.putString(SEARCH_HISTORY, lyricSearchHistory.toString() + ",");
        }
        editor.commit();

    }

    /**
     * Getter for search history, provide a String contains history record
     *
     * @return a list of search history which have 5 elements.
     */
    private List<String> getSearchHistory() {
        prefs = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        String longHistory = prefs.getString(SEARCH_HISTORY, "");
        String[] tmpHistory = longHistory.split(","); //split后长度为1有一个空串对象
        List<String> historyList = new ArrayList<String>(Arrays.asList(tmpHistory));
        // when the history is empty, the list will be a empty list with 1 element
        // so need to clear the empty list under this case
        if (historyList.size() == 1 && historyList.get(0).equals("")) {
            // empty the list
            historyList.clear();
        }
        return historyList;
    }

    /**
     * Implement the BaseAdapter interface
     * Provide data to fill and update list view
     */
    private class MyListAdapter extends BaseAdapter {

        public int getCount() {
            return elements.size();
        }

        public String getItem(int position) {
            return elements.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View old, ViewGroup parent) {

            String searchHistory = getItem(position);
            View newView;
            ViewHolder viewHolder;

            LayoutInflater inflater = getLayoutInflater();

            if (old == null) {
                newView = inflater.inflate(R.layout.lyric_search_history, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.search_history_layout = newView.findViewById(R.id.lyric_search_history_layout);
                viewHolder.search_history_text = newView.findViewById(R.id.lyric_search_history_text);
                newView.setTag(viewHolder);
            } else {
                newView = old;
                viewHolder = (ViewHolder) newView.getTag();
            }
            //
            viewHolder.search_history_layout.setVisibility(View.VISIBLE);
            viewHolder.search_history_text.setText(searchHistory);

            return newView;
        }

        /**
         * Hold the List view place
         */
        class ViewHolder {
            LinearLayout search_history_layout;
            TextView search_history_text;
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
                Intent goToGeoData = new Intent(LyricSearchActivity.this, GeoDataSource.class);
                startActivity(goToGeoData);
                break;
            case R.id.lyric_toolbar_goto_soccerhighlight:
                Intent goToSoccer = new Intent(LyricSearchActivity.this, GameList.class);
                startActivity(goToSoccer);
                break;
            case R.id.lyric_toolbar_goto_deezer:
                Intent goToDeezer = new Intent(LyricSearchActivity.this, DeezerSongSearchActivity.class);
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
                Toast.makeText(this, R.string.lyric_navagation_help_search, Toast.LENGTH_SHORT).show();
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
