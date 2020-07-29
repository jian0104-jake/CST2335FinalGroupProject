package com.example.cst2335finalgroupproject.DeezerSongSearch;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.cst2335finalgroupproject.R;

/**
 * show details of a specific song
 */
public class DeezerSongDetailActivity extends AppCompatActivity {

    /**
     * progress bar to show when loading image
     */
    public static final String SONG_DETAIL = "SONG_DETAIL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deezer_song_detail);
        setTitle(getString(R.string.activity_title_song_detail));

        // tool bar
        Toolbar toolBar = findViewById(R.id.deezer_toolbar);
        setSupportActionBar(toolBar);

        Bundle msgInfo = getIntent().getBundleExtra(SONG_DETAIL);

//        FrameLayout frameLayout = findViewById(R.id.fragment_song_detail;

        // show fragment -- referenced professor Islam's work
        SongDetailFragment songDetailFragment = new SongDetailFragment();
        songDetailFragment.setArguments( msgInfo );
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_song_detail, songDetailFragment) //Add the fragment in FrameLayout
                .commit(); //actually load the fragment. Calls onCreate() in DetailFragment
    }

}