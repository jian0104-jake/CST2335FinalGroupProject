package com.example.cst2335finalgroupproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.cst2335finalgroupproject.DeezerSongSearch.DeezerSongSearchActivity;
import com.example.cst2335finalgroupproject.SongLyricsSearch.LyricsSearchActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnGeoDataSource = findViewById(R.id.btnGeoDataSource);
        btnGeoDataSource.setOnClickListener((view -> {

        }));

        Button btnSoccer = findViewById(R.id.btnSoccer);
        btnSoccer.setOnClickListener((view -> {

        }));

        Button btnSongLyrics = findViewById(R.id.btnSongLyrics);
        btnSongLyrics.setOnClickListener((view -> {
            Intent goToLyricSearch = new Intent(MainActivity.this, LyricsSearchActivity.class);
            startActivity(goToLyricSearch);
        }));

        Button btnDeezer = findViewById(R.id.btnDeezer);
        btnDeezer.setOnClickListener((v) -> {
            Intent intent = new Intent(MainActivity.this, DeezerSongSearchActivity.class);
            startActivity(intent);
        });
    }
}