package com.example.cst2335finalgroupproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import com.example.cst2335finalgroupproject.DeezerSongSearch.DeezerSongSearchActivity;
import com.example.cst2335finalgroupproject.SoccerMatchHighlights.GameList;
import com.example.cst2335finalgroupproject.SongLyricsSearch.LyricSearchActivity;
import com.example.cst2335finalgroupproject.geodata.GeoDataSource;

public class MainActivity extends AppCompatActivity {
    public SharedPreferences prefs;
    String savedMatchUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences("data",MODE_PRIVATE);
        savedMatchUrl = prefs.getString("gameUrl","");
        Button btnGeoDataSource = findViewById(R.id.btnGeoDataSource);
        btnGeoDataSource.setOnClickListener((view -> {
            Intent goToGeoData = new Intent(MainActivity.this, GeoDataSource.class);
            startActivity(goToGeoData);
        }));

        Button btnSoccer = findViewById(R.id.btnSoccer);
        btnSoccer.setOnClickListener((view -> {

            boolean isEmpty = savedMatchUrl.length() == 0;
            if (isEmpty) {
                Intent goToSoccer = new Intent(MainActivity.this, GameList.class);
                startActivity(goToSoccer);
            }else{
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData( Uri.parse(savedMatchUrl) );
                startActivityForResult(i,100);
            }

        }));

        Button btnSongLyrics = findViewById(R.id.btnSongLyrics);
        btnSongLyrics.setOnClickListener((view -> {
            Intent goToLyricSearch = new Intent(MainActivity.this, LyricSearchActivity.class);
            startActivity(goToLyricSearch);
        }));

        Button btnDeezer = findViewById(R.id.btnDeezer);
        btnDeezer.setOnClickListener((v) -> {
            Intent intent = new Intent(MainActivity.this, DeezerSongSearchActivity.class);
            startActivity(intent);
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 0) savedMatchUrl="";
    }


}