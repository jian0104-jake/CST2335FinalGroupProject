package com.example.cst2335finalgroupproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LyricsShowActivity extends AppCompatActivity {

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyricshow);

        Intent fromSearch = getIntent();
        String lyric = fromSearch.getStringExtra("lyrics");
        TextView textView = findViewById(R.id.lyric);
        textView.setText(lyric);

    }
}
