package com.example.cst2335finalgroupproject.DeezerSongSearch.entity;

public class Song {

    public final static String TABLE_NAME_SEARCH_RESULT = "DEEZER_SONG_LIST";
    public final static String TABLE_NAME_FAVORITE = "DEEZER_SONG_FAV";

    public final static String COL_ID= "_id";
    public final static String COL_TITLE= "Title";
    public final static String COL_DURATION= "Duration";
    public final static String COL_ALBUM_NAME= "AlbumName";
    public final static String COL_ALBUM_COVER = "AlbumCover";


    /**
     * the id in db
     */
    private long id;

    /**
     * the title of the song
     */
    private String title;

    /**
     * the duration of the song in seconds
     */
    private int duration; // in second

    /**
     * the album name
     */
    private String albumName;

    /**
     * the album cover image url;
     */
    private String albumCover;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getAlbumCover() {
        return albumCover;
    }

    public void setAlbumCover(String albumCover) {
        this.albumCover = albumCover;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDurationInMMSS() {
        int minute = duration / 60;
        int second = duration % 60;
        return String.format("%d:%02d", minute, second);
    }

}
