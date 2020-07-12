package com.example.cst2335finalgroupproject.SongLyricsSearch;

/**
 * The class record the lyrics search history
 */
public class LyricSearchHistory {

    /**
     * artist: the singer's name
     * title: the song's name
     */
    private String artist, title;

    /**
     * Default non-argue constructor
     * Chain to full-argue constructor using blank string as default value
     */
    public LyricSearchHistory() {
        this("","");
    }

    /**
     * Full-argue constructor
     * @param artist the singer's name
     * @param title the song's name
     */
    public LyricSearchHistory(String artist, String title) {
        this.artist = artist;
        this.title = title;
    }

    /**
     * Provide artist field
     * @return The singer's name
     */
    public String getArtist() {
        return artist;
    }

    /**
     * Update the singer's name
     * @param artist the new artist for the song
     */
    public void setArtist(String artist) {
        this.artist = artist;
    }

    /**
     * Provide the song's name
     * @return the song's name
     */
    public String getTitle() {
        return title;
    }

    /**
     * Update the song's name
     * @param title the new name for the song
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Predefine the format of search history
     * The format is: Artist - Title
     * @return a predefine string constain artist and title
     */
    public String toString(){
        return this.getArtist() + " - " + this.getTitle();
    }
}
