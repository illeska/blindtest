package com.blindtest.model;

/**
 * Manche de jeu.
 * Gère l'association entre la piste et l'état actuel des indices révélés.
 */
public class Round {

    private Track track;
    // Stockage de l'état des indices (ex: "A***e", "T*****e")
    private String artistHint = "";
    private String titleHint = "";

    public Round() {
        this.track = null;
    }

    public Round(Track track) {
        this.track = track;
        if (track != null) {
            initializeHints(track);
        }
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
        // Initialiser les indices dès qu'une piste est affectée
        if (track != null) {
            initializeHints(track);
        }
    }

    /** Initialise les indices à une chaîne de masques ('*'). */
    private void initializeHints(Track track) {
        this.artistHint = "*".repeat(track.getArtist().length());
        this.titleHint = "*".repeat(track.getTitle().length());
    }

    // --- Getters et Setters pour les indices (Nécessaires pour GameView) ---

    public String getArtistHint() {
        return artistHint;
    }

    public void setArtistHint(String artistHint) {
        this.artistHint = artistHint;
    }

    public String getTitleHint() {
        return titleHint;
    }

    public void setTitleHint(String titleHint) {
        this.titleHint = titleHint;
    }
}