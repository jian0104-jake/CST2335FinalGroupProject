package com.example.cst2335finalgroupproject.SongLyricsSearch.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * Define a databse to store the favorite lyrics
 */
public class FavSongDB extends SQLiteOpenHelper {

    /**
     * The name of database
     */
    public final static String DATABASE_NAME = "favSongLyricsDB";

    /**
     * The table's name
     */
    public final static String TABLE_NAME = "FavSongLyrics";

    /**
     * The version of database
     */
    public final static int VERSION_NUM = 1;

    /**
     * The id of database, should be increment automatically
     */
    public final static String COL_ID = "_id";

    /**
     * The name of the song
     */
    public final static String COL_TITLE = "Title";

    /**
     * The name of the singer
     */
    public final static String COL_ARTIST = "Artist";

    /**
     * The name of the singer
     */
    public final static String COL_CONTENT = "Content";


//    public final static String COL_LYRIC = "Lyric";

    /**
     * Constructor to create a instance of database
     * @param context information about this application
     */
    public FavSongDB(Context context) {
        super(context, DATABASE_NAME, null, VERSION_NUM);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (" + COL_ID + " integer primary key autoincrement, "
                + COL_ARTIST + " text, " + COL_TITLE + " text, " + COL_CONTENT + " text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Drop the old table:
        db.execSQL("drop table if exists " + TABLE_NAME);

        //Create the new table:
        onCreate(db);
    }

}
