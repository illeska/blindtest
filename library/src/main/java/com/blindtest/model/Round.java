package com.blindtest.model;

/**
 * Manche de jeu.
 * Gère l'association entre la piste et l'état actuel des indices révélés.
 */
public class Round {

    /**
     * La piste de musique associée à cette manche.
     */
    private Track track;

    /**
     * L'indice de l'artiste avec caractères masqués.
     */
    private String artistHint = "";

    /**
     * L'indice du titre avec caractères masqués.
     */
    private String titleHint = "";

    /**
     * Crée une nouvelle manche vide sans piste.
     */
    public Round() {
        this.track = null;
    }

    /**
     * Crée une nouvelle manche avec une piste et initialise les indices.
     * @param track La piste de musique
     */
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

    /**
     * Initialise les indices à une chaîne de masques ('*').
     * @param track La piste dont on masque le titre et l'artiste
     */
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