package com.blindtest.model;

import java.time.LocalDateTime;

/**
 * Représente un score pour le leaderboard et l'historique des joueurs.
 */
public class Score {
    private String pseudo;
    private int score;
    private LocalDateTime date;

    /**
     * Constructeur pour créer un score avec pseudo et score, date automatique.
     * @param pseudo Le pseudo du joueur
     * @param score Le score obtenu
     */
    public Score(String pseudo, int score) {
        this.pseudo = pseudo;
        this.score = score;
        this.date = LocalDateTime.now();
    }

    /**
     * Retourne le pseudo du joueur.
     * @return Le pseudo
     */
    public String getPseudo() {
        return pseudo;
    }

    /**
     * Retourne le score.
     * @return Le score
     */
    public int getScore() {
        return score;
    }

    /**
     * Retourne la date du score.
     * @return La date
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * Définit le pseudo du joueur.
     * @param pseudo Le pseudo
     */
    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    /**
     * Définit le score.
     * @param score Le score
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Définit la date du score.
     * @param date La date
     */
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
