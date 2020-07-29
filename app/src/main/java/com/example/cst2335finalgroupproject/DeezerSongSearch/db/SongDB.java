package com.example.cst2335finalgroupproject.DeezerSongSearch.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.cst2335finalgroupproject.DeezerSongSearch.entity.Song;

public class SongDB extends SQLiteOpenHelper {

    protected final static  String DATABASE_NAME = "SongDB";
    protected final static int VERSION_NUM = 1;

    public SongDB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION_NUM);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format("CREATE TABLE %s " +
                "(%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " %s text," +
                " %s integer, " +
                " %s text," +
                " %s text); ", Song.TABLE_NAME_FAVORITE, Song.COL_ID, Song.COL_TITLE,
                Song.COL_DURATION, Song.COL_ALBUM_NAME, Song.COL_ALBUM_COVER));

        db.execSQL(String.format("CREATE TABLE %s " +
                        "(%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        " %s text," +
                        " %s integer, " +
                        " %s text," +
                        " %s text); ", Song.TABLE_NAME_SEARCH_RESULT, Song.COL_ID, Song.COL_TITLE,
                Song.COL_DURATION, Song.COL_ALBUM_NAME, Song.COL_ALBUM_COVER));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL( "DROP TABLE IF EXISTS " + Song.TABLE_NAME_SEARCH_RESULT);
        db.execSQL( "DROP TABLE IF EXISTS " + Song.TABLE_NAME_FAVORITE);

        //Create the new table:
        onCreate(db);
    }
}
