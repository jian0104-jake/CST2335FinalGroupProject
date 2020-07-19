package com.example.cst2335finalgroupproject.SongLyricsSearch.Entity;

/**
 * The class record the lyrics search history
 */
public class FavLyricsEntity {

    /**
     * artist: the singer's name
     * title: the song's name
     */
    private String artist, title, content;


    /**
     * Auto generated ID in database.
     */
    private long dbId;

    /**
     * Default non-argue constructor
     * Chain to full-argue constructor using blank string as default value
     */
    public FavLyricsEntity() {
        this("","", 0, "");
    }

    /**
     * Full-argue constructor
     * @param artist the singer's name
     * @param title the song's name
     * @param dbId the song's database id.
     */
    public FavLyricsEntity(String artist, String title, long dbId, String content) {
        this.artist = artist;
        this.title = title;
        this.dbId = dbId;
        this.content = content;
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
     * Provide database ID of the song
     * @return the song's database ID
     */
    public long getDbId() {
        return dbId;
    }

    /**
     * Update the song's ID
     * @param dbId the new database ID for the song
     */
    public void setDbId(long dbId) {
        this.dbId = dbId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
