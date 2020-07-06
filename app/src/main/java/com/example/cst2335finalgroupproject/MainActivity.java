package com.example.cst2335finalgroupproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = findViewById(R.id.go_to_lyric);
        btn.setOnClickListener(click -> {
            Intent goToLyricSearch = new Intent(MainActivity.this, SongLyricsSearchActivity.class);
            startActivity(goToLyricSearch);
        });
    }
}