package com.example.cst2335finalgroupproject.DeezerSongSearch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cst2335finalgroupproject.DeezerSongSearch.db.SongDB;
import com.example.cst2335finalgroupproject.DeezerSongSearch.entity.Song;
import com.example.cst2335finalgroupproject.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DeezerFavSongActivity extends AppCompatActivity implements SongDetailFragment.OnRemoveFavoriteSongListener {

    /**
     * the data holder of favorite songs
     */
    private List<Song> songs;

    /**
     * favorite song adapter for list view
     */
    private SongsAdapter songsAdapter;

    /**
     * db used to get/save favorite song
     */
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deezer_fav_song);

        setTitle("My Favorite Song");

        // tool bar
        Toolbar toolBar = findViewById(R.id.deezer_toolbar);
        setSupportActionBar(toolBar);

        boolean isTablet = findViewById(R.id.fragment_song_detail) != null;

        /**
         * the ListView to show the favorite songs
         */
        ListView lvFavSong = findViewById(R.id.lv_fav_song);

        songs = new ArrayList<>();
        songsAdapter = new SongsAdapter();
        lvFavSong.setAdapter(songsAdapter);

        lvFavSong.setOnItemClickListener((parent, view, position, id) -> {
            Song song = songs.get(position);
            // TODO view detail
            Bundle bundle = new Bundle();
            bundle.putBoolean(SongDetailFragment.KEY_IS_FAVORITE, true);
            bundle.putLong(SongDetailFragment.KEY_SONG_ID, song.getId());
            bundle.putString(SongDetailFragment.KEY_SONG_NAME, song.getTitle());
            bundle.putInt(SongDetailFragment.KEY_SONG_DURATION, song.getDuration());
            bundle.putString(SongDetailFragment.KEY_SONG_DURATION_STR, song.getDurationInMMSS());
            bundle.putString(SongDetailFragment.KEY_ALBUM_NAME, song.getAlbumName());
            bundle.putString(SongDetailFragment.KEY_ALBUM_COVER, song.getAlbumCover());

            if (isTablet) {
                // init fragment
                // show fragment -- referenced professor Islam's work
                SongDetailFragment songDetailFragment = new SongDetailFragment();
                songDetailFragment.setArguments( bundle );
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_song_detail, songDetailFragment) //Add the fragment in FrameLayout
                        .commit(); //actually load the fragment. Calls onCreate() in DetailFragment

            } else {
                Intent intent = new Intent(DeezerFavSongActivity.this, DeezerSongDetailActivity.class);
                intent.putExtra(DeezerSongDetailActivity.SONG_DETAIL, bundle);
                startActivity(intent);
            }
        });

        SongDB songDB = new SongDB(this);
        db = songDB.getWritableDatabase();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // always reload favorite songs when enter or back from other activity
        loadFavoriteSong();
    }

    private void loadFavoriteSong() {
        String[] columns = {Song.COL_ID, Song.COL_TITLE, Song.COL_DURATION, Song.COL_ALBUM_NAME, Song.COL_ALBUM_COVER,};
        Cursor results = db.query(false, Song.TABLE_NAME_FAVORITE, columns,
                null, null, null, null, null, null);

        int idColIndex = results.getColumnIndex(Song.COL_ID);
        int titleColIndex = results.getColumnIndex(Song.COL_TITLE);
        int durationColIndex = results.getColumnIndex(Song.COL_DURATION);
        int albumNameColIndex = results.getColumnIndex(Song.COL_ALBUM_NAME);
        int albumCoverColIndex = results.getColumnIndex(Song.COL_ALBUM_COVER);

        while (results.moveToNext()) {
            Song song = new Song();
            song.setId(results.getInt(idColIndex));
            song.setTitle(results.getString(titleColIndex));
            song.setDuration(results.getInt(durationColIndex));
            song.setAlbumName(results.getString(albumNameColIndex));
            song.setAlbumCover(results.getString(albumCoverColIndex));

            songs.add(song);
        }

        songsAdapter.notifyDataSetChanged();
    }

    /**
     * reference to: https://developer.android.com/training/basics/fragments/communicating.html
     * @param fragment
     */
    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof SongDetailFragment) {
            ((SongDetailFragment)fragment).setCallback(this);
        }
    }

    @Override
    public void removeSong(long songId) {
        // TODO remove song from songs and update list view
        Song song = null;
        for (Song item : songs) {
            if (item.getId() == songId) {
                song = item;
                break;
            }
        }

        if (song != null) {
            songs.remove(song);
            songsAdapter.notifyDataSetChanged();
        }
    }

    /**
     * song adapter used by list view for songs display
     */
    class SongsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return songs.size();
        }

        @Override
        public Song getItem(int position) {
            return songs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater layoutInflater = getLayoutInflater();
                convertView = layoutInflater.inflate(R.layout.item_song, parent, false);
            }
            TextView tvSongName = convertView.findViewById(R.id.tv_song_name);
            TextView tvSongDuration = convertView.findViewById(R.id.tv_song_duration);

            Song song = getItem(position);
            tvSongName.setText(String.format(Locale.getDefault(), "%d. %s", position + 1, song.getTitle()));
            tvSongDuration.setText(song.getDurationInMMSS());

            return convertView;
        }
    }
}