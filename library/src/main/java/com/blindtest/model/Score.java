package com.blindtest.model;

import java.time.LocalDateTime;

/**
 * Représente un score pour le leaderboard et l'historique des joueurs.
 * Contient des informations détaillées sur la partie jouée.
 */
public class Score {
    private String pseudo;
    private int score;
    private LocalDateTime date;
    
    private String mode;           // "Solo" ou "Duel"
    private String genre;          // Genre musical (ex: "Pop", "Rock", etc.)
    private int totalTracks;       // Nombre total de morceaux
    private int correctTitles;     // Nombre de titres corrects
    private int correctArtists;    // Nombre d'artistes corrects
    private int hintsUsed;         // Nombre d'indices utilisés

    /**
     * Constructeur principal avec tous les détails.
     * @param pseudo Le pseudo du joueur
     * @param score Le score obtenu
     * @param mode Le mode de jeu ("Solo" ou "Duel")
     * @param genre Le genre musical
     * @param totalTracks Nombre total de morceaux
     * @param correctTitles Nombre de titres corrects
     * @param correctArtists Nombre d'artistes corrects
     * @param hintsUsed Nombre d'indices utilisés
     */
    public Score(String pseudo, int score, String mode, String genre, 
                 int totalTracks, int correctTitles, int correctArtists, int hintsUsed) {
        this.pseudo = pseudo;
        this.score = score;
        this.mode = mode;
        this.genre = genre;
        this.totalTracks = totalTracks;
        this.correctTitles = correctTitles;
        this.correctArtists = correctArtists;
        this.hintsUsed = hintsUsed;
        this.date = LocalDateTime.now();
    }

    /**
     * Constructeur simplifié (rétrocompatibilité).
     * @param pseudo Le pseudo du joueur
     * @param score Le score obtenu
     */
    public Score(String pseudo, int score) {
        this(pseudo, score, "Solo", "Mixed", 0, 0, 0, 0);
    }

    // === Getters ===
    
    public String getPseudo() {
        return pseudo;
    }

    public int getScore() {
        return score;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getMode() {
        return mode;
    }

    public String getGenre() {
        return genre;
    }

    public int getTotalTracks() {
        return totalTracks;
    }

    public int getCorrectTitles() {
        return correctTitles;
    }

    public int getCorrectArtists() {
        return correctArtists;
    }

    public int getHintsUsed() {
        return hintsUsed;
    }

    // === Setters ===
    
    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setTotalTracks(int totalTracks) {
        this.totalTracks = totalTracks;
    }

    public void setCorrectTitles(int correctTitles) {
        this.correctTitles = correctTitles;
    }

    public void setCorrectArtists(int correctArtists) {
        this.correctArtists = correctArtists;
    }

    public void setHintsUsed(int hintsUsed) {
        this.hintsUsed = hintsUsed;
    }

    // === Méthodes utilitaires ===

    /**
     * Calcule le taux de réussite global (titres + artistes).
     * @return Le taux de réussite en pourcentage (0-100)
     */
    public double getSuccessRate() {
        if (totalTracks == 0) return 0.0;
        int totalPossible = totalTracks * 2; // titre + artiste par morceau
        int totalCorrect = correctTitles + correctArtists;
        return (totalCorrect * 100.0) / totalPossible;
    }

    /**
     * Calcule le taux de réussite pour les titres uniquement.
     * @return Le taux de réussite des titres en pourcentage (0-100)
     */
    public double getTitleSuccessRate() {
        if (totalTracks == 0) return 0.0;
        return (correctTitles * 100.0) / totalTracks;
    }

    /**
     * Calcule le taux de réussite pour les artistes uniquement.
     * @return Le taux de réussite des artistes en pourcentage (0-100)
     */
    public double getArtistSuccessRate() {
        if (totalTracks == 0) return 0.0;
        return (correctArtists * 100.0) / totalTracks;
    }

    /**
     * Retourne une représentation textuelle du score.
     * @return Une chaîne formatée contenant les informations du score
     */
    @Override
    public String toString() {
        return String.format("Score{pseudo='%s', score=%d, mode='%s', genre='%s', date=%s}", 
                             pseudo, score, mode, genre, date);
    }
}