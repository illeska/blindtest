package com.blindtest.model;

/**
 * Représente les paramètres de configuration du jeu.
 */
public class Settings {
    private int numberOfRounds;
    private int extractDuration; // en secondes
    private boolean hintsEnabled;
    private boolean speedBonusEnabled;
    private double defaultVolume; // 0.0 à 1.0
    private String defaultGenre;

    /**
     * Constructeur par défaut avec valeurs par défaut.
     */
    public Settings() {
        this.numberOfRounds = 10;
        this.extractDuration = 20;
        this.hintsEnabled = true;
        this.speedBonusEnabled = true;
        this.defaultVolume = 0.5;
        this.defaultGenre = "pop";
    }

    /**
     * Retourne le nombre de manches.
     * @return Le nombre de manches
     */
    public int getNumberOfRounds() {
        return numberOfRounds;
    }

    /**
     * Définit le nombre de manches.
     * @param numberOfRounds Le nombre de manches
     */
    public void setNumberOfRounds(int numberOfRounds) {
        this.numberOfRounds = numberOfRounds;
    }

    /**
     * Retourne la durée des extraits en secondes.
     * @return La durée des extraits
     */
    public int getExtractDuration() {
        return extractDuration;
    }

    /**
     * Définit la durée des extraits en secondes.
     * @param extractDuration La durée des extraits
     */
    public void setExtractDuration(int extractDuration) {
        this.extractDuration = extractDuration;
    }

    /**
     * Vérifie si les indices sont activés.
     * @return true si activés, false sinon
     */
    public boolean isHintsEnabled() {
        return hintsEnabled;
    }

    /**
     * Active ou désactive les indices.
     * @param hintsEnabled true pour activer, false pour désactiver
     */
    public void setHintsEnabled(boolean hintsEnabled) {
        this.hintsEnabled = hintsEnabled;
    }

    /**
     * Vérifie si le bonus de vitesse est activé.
     * @return true si activé, false sinon
     */
    public boolean isSpeedBonusEnabled() {
        return speedBonusEnabled;
    }

    /**
     * Active ou désactive le bonus de vitesse.
     * @param speedBonusEnabled true pour activer, false pour désactiver
     */
    public void setSpeedBonusEnabled(boolean speedBonusEnabled) {
        this.speedBonusEnabled = speedBonusEnabled;
    }

    /**
     * Retourne le volume par défaut (0.0 à 1.0).
     * @return Le volume par défaut
     */
    public double getDefaultVolume() {
        return defaultVolume;
    }

    /**
     * Définit le volume par défaut (0.0 à 1.0).
     * @param defaultVolume Le volume par défaut
     */
    public void setDefaultVolume(double defaultVolume) {
        this.defaultVolume = defaultVolume;
    }

    /**
     * Retourne le genre musical par défaut.
     * @return Le genre par défaut
     */
    public String getDefaultGenre() {
        return defaultGenre;
    }

    /**
     * Définit le genre musical par défaut.
     * @param defaultGenre Le genre par défaut
     */
    public void setDefaultGenre(String defaultGenre) {
        this.defaultGenre = defaultGenre;
    }
}
