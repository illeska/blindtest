package com.blindtest.model;

import java.time.LocalDateTime;

/**
 * Représente un score pour le leaderboard et l'historique des joueurs.
 */
public class Score {
    private String pseudo;
    private int score;
    private LocalDateTime date;

    public Score(String pseudo, int score) {
        this.pseudo = pseudo;
        this.score = score;
        this.date = LocalDateTime.now();
    }

    // Getters
    public String getPseudo() {
        return pseudo;
    }

    public int getScore() {
        return score;
    }

    public LocalDateTime getDate() {
        return date;
    }

    // Setters (si nécessaire)
    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
