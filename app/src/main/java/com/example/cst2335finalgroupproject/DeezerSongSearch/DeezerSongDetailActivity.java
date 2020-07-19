package com.example.cst2335finalgroupproject.DeezerSongSearch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.sax.TextElementListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cst2335finalgroupproject.R;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * show details of a specific song
 */
public class DeezerSongDetailActivity extends AppCompatActivity {
    public static final String KEY_SONG_NAME = "SONG_NAME";
    public static final String KEY_SONG_DURATION = "SONG_DURATION";
    public static final String KEY_ALBUM_NAME = "ALBUM_NAME";
    public static final String KEY_ALBUM_COVER = "ALBUM_COVER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deezer_song_detail);
        setTitle("Song Detail");

        Intent fromMain = getIntent();
        String songName = fromMain.getStringExtra(KEY_SONG_NAME);
        String songDuration = fromMain.getStringExtra(KEY_SONG_DURATION);
        String albumName = fromMain.getStringExtra(KEY_ALBUM_NAME);
        String albumCover = fromMain.getStringExtra(KEY_ALBUM_COVER);

        TextView tvSongName = findViewById(R.id.tvSongName);
        tvSongName.setText(songName);

        TextView tvSongDuration = findViewById(R.id.tvSongDuration);
        tvSongDuration.setText(songDuration);

        TextView tvAlbumName = findViewById(R.id.tvAlbumNamee);
        tvAlbumName.setText(albumName);

        ImageView imgAlbumCover = findViewById(R.id.imgAlbumCover);
//        try {
//            imgAlbumCover.setImageURI(new Uri(albumCover));
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }

    }
}