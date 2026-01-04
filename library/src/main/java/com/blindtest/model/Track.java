package com.blindtest.model;

/**
 * Représente un morceau de musique avec son titre, artiste et durée.
 */
public class Track {
    private String title;
    private String artist;
    private int duration;

    /**
     * Crée un nouveau morceau.
     * @param title Le titre du morceau
     * @param artist L'artiste du morceau
     * @param duration La durée en secondes
     */
    public Track(String title, String artist, int duration) {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public int getDuration() {
        return duration;
    }
}
