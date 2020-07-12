package com.example.cst2335finalgroupproject.SongLyricsSearch;



import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cst2335finalgroupproject.R;
import com.example.cst2335finalgroupproject.SongLyricsSearch.Database.LyricSearchHistory;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The main activity of Song Lyrics Search
 */

public class LyricsSearchActivity extends AppCompatActivity {

    /**
     *  The implemented adapter for list view
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
     *  The instance of SearchHistory class, which is used to record
     *  the search history
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

        // Listview to display search history
        listView = findViewById(R.id.search_history_list);
        listView.setAdapter(myAdapter = new MyListAdapter());
        // get the history from SharedPreferences, and display in the listview
        elements = (ArrayList<String>) getSearchHistory();
        if (elements.size() > 0) {
            myAdapter.notifyDataSetChanged();
        }

        Button btn = findViewById(R.id.search_button);
        btn.setOnClickListener(click -> {

            EditText artistText = findViewById(R.id.artist_input);
            String artist = artistText.getText().toString();
            EditText titleText = findViewById(R.id.title_input);
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

                    Intent goToLyrics = new Intent(LyricsSearchActivity.this, ShowLyricsActivity.class);
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

        listView.setSelection(elements.size());
        listView.setOnItemLongClickListener((parent, view, pos, id) -> {

            // build up a AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Search History");
            builder.setMessage("The selected row is: " + pos);

            // set up two buttons
            builder.setPositiveButton("OK", null);
            builder.setNegativeButton("Cancel", null);

            // create and show the dialog
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            return true;
        });

        Button toFavList = findViewById(R.id.go_to_fav_button);
        toFavList.setOnClickListener(click ->{
            Intent goToFav = new Intent(LyricsSearchActivity.this, FavSongActivity.class);
            startActivity(goToFav);
        });
    }

    /**
     * Save search history using SharedPreferences
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
                newView = inflater.inflate(R.layout.search_history, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.search_history_layout = newView.findViewById(R.id.search_history_layout);
                viewHolder.search_history_text = newView.findViewById(R.id.search_history_text);
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
}
