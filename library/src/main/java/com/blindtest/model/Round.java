package com.blindtest.model;

/**
 * Manche de jeu.
 * Sprint 1 : on simule la lecture d’un extrait avec un simple println.
 * (L’intégration MediaPlayer / iTunes API viendra plus tarde)
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

    /** Simule la lecture d’un extrait audio. */
    public void playExtract() {
        System.out.println("Lecture d’un extrait…");
    }
}
