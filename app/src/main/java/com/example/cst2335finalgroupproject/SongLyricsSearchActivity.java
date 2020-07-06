package com.example.cst2335finalgroupproject;

/**
 * @author:
 * @version:
 */

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

/**
 *
 */
public class SongLyricsSearchActivity extends AppCompatActivity {

    private String artist, title;
    private final String URL = "https://api.lyrics.ovh/v1/";
    private ArrayList<searchHistory> history = new ArrayList<>();
    private MyListAdapter myAdapter;
    private ListView listView;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyricsearch);

        TextView artistText = findViewById(R.id.artist);
        artist = artistText.getText().toString();
        TextView titleText = findViewById(R.id.artist);
        title = titleText.getText().toString();

        Button btn = findViewById(R.id.search_button);
        btn.setOnClickListener(click -> {
            String path = URL + artist +"/"+"title";

            // build up a AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Do you want to search this song?");
            builder.setMessage("The artist is: " + artist + "\nThe title is: " + title);

            // set up two buttons
            builder.setPositiveButton("Search", (dialog, which) -> {
                try {
                    URL searchURL = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) searchURL.openConnection();
                    conn.setConnectTimeout(5 * 1000);
                    conn.connect();

                    // determine if the request is successful
                    if (conn.getResponseCode() == 200) {
                        // search successfully then redirect to LyricsShowActivity
                        String lyric = "Roxanne\r\nYou don't";
                        Intent goToLyrics = new Intent(SongLyricsSearchActivity.this, LyricsShowActivity.class);
                        goToLyrics.putExtra("lyrics", lyric);
                        startActivity(goToLyrics);
                    } else {
                        // if search failed then display a toast
                        Toast.makeText(this, "Can not find this song", Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            builder.setNegativeButton("Cancel", null);

            // create and show the dialog
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
    }

    private class MyListAdapter extends BaseAdapter {

        public int getCount() {
            return history.size();
        }

        public searchHistory getItem(int position) {
            return history.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View old, ViewGroup parent) {

            searchHistory his = getItem(position);
            View newView = null;
            ViewHolder viewHolder;
            LayoutInflater inflater = getLayoutInflater();

            newView = inflater.inflate(R.layout.activity_lyricsearch, parent, false);

            return newView;
        }

        // Hold the List view place
        class ViewHolder {
            LinearLayout receiveLayout;
            LinearLayout sendLayout;
            TextView receiveMessage;
            TextView sentMessage;
        }
    }

}
