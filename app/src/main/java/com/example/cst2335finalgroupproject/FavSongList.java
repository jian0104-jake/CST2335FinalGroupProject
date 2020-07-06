package com.example.cst2335finalgroupproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class FavSongList extends SQLiteOpenHelper {

    public final static String DATABASE_NAME = "FavSongLyricsDB";
    public final static String TABLE_NAME = "FavSongLyrics";
    public final static int VERSION_NUM = 1;
    public final static String COL_ID = "_id";
    public final static String COL_ARTIST = "Artist";
    public final static String COL_TITLE = "Title";
    public final static String COL_LYRIC = "Lyric";

    public FavSongList(Context context) {
        super(context, DATABASE_NAME, null, VERSION_NUM);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (" + COL_ID + " integer primary key autoincrement, "
                + COL_ARTIST + " text, " + COL_TITLE + " text, " + COL_ARTIST + " text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop the old table:
        db.execSQL("drop table if exists " + TABLE_NAME);

        //Create the new table:
        onCreate(db);
    }

}
