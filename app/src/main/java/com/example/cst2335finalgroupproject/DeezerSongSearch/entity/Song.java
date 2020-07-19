package com.example.cst2335finalgroupproject.DeezerSongSearch.entity;

public class Song {

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
     * the album comver image url;
     */
    private String albumCover;


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
