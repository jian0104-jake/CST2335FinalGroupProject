package com.example.cst2335finalgroupproject.DeezerSongSearch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.cst2335finalgroupproject.R;

/**
 * show details of a specific song
 */
public class DeezerSongDetailActivity extends AppCompatActivity {
    public static final String KEY_SONG_NAME = "SONG_NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deezer_song_detail);
        setTitle("Song Detail");

        Intent fromMain = getIntent();
        String songName = fromMain.getStringExtra(KEY_SONG_NAME);

        TextView tvSongName = findViewById(R.id.tvSongName);

        tvSongName.setText(songName);

    }
}