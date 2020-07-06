package com.example.cst2335finalgroupproject;

/**
 * @author:
 * @version:
 */

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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

    private final String URL = "https://api.lyrics.ovh/v1/";

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyricsearch);

        Button btn = findViewById(R.id.search_button);
        btn.setOnClickListener(click -> {

            EditText artistText = findViewById(R.id.artist_input);
            String artist = artistText.getText().toString();
            EditText titleText = findViewById(R.id.title_input);
            String title = titleText.getText().toString();

            String path = URL + artist + "/" + title;
            String message = "The artist is: " + artist + "\nThe title is: " + title;

            if (artist == null || artist.isEmpty()) {
                Toast.makeText(this, "artist can not be blank", Toast.LENGTH_LONG).show();
            } else if (title == null || title.isEmpty()) {
                Toast.makeText(this, "title can not be blank", Toast.LENGTH_LONG).show();
            } else {
                // build up a AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Do you want to search the song?");
                builder.setMessage(message);

                // set up two buttons
                builder.setPositiveButton("Search", (dialog, which) -> {

                        String lyric = "Roxanne\r\nYou don't";
                        Intent goToLyrics = new Intent(SongLyricsSearchActivity.this, LyricsShowActivity.class);
                        goToLyrics.putExtra("lyrics", lyric);
                        startActivity(goToLyrics);

                });
                builder.setNegativeButton("Cancel", null);

                // create and show the dialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

    }

    private void getLyrics(String path) {
        new Thread(() -> {
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
    }
}
