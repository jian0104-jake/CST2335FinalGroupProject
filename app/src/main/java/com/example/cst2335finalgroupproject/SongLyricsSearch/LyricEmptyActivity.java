package com.example.cst2335finalgroupproject.SongLyricsSearch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.cst2335finalgroupproject.R;

public class LyricEmptyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyric_empty);

        Bundle dataToPass = getIntent().getExtras(); //get the data that was passed from FragmentExample

        //This is copied directly from FragmentExample.java lines 47-54
        LyricDetailsFragment dFragment = new LyricDetailsFragment();
        dFragment.setArguments(dataToPass); //pass data to the the fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.lyric_frame_layout_empty_location, dFragment)
                .commit();
    }
}