package com.blindtest.model;

/**
 * Manche de jeu.
 * (La gestion de la lecture de l'extrait audio est désormais gérée par GameController et AudioService).
 */
public class Round {

    private Track track; 

    public Round() {
        this.track = null;
    }

    public Round(Track track) {
        this.track = track;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    // 🔥 SUPPRIMÉ : La méthode playExtract() a été retirée.
}