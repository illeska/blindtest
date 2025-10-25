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

    // Constructeur par défaut
    public Settings() {
        this.numberOfRounds = 10;
        this.extractDuration = 20;
        this.hintsEnabled = true;
        this.speedBonusEnabled = true;
        this.defaultVolume = 0.5;
        this.defaultGenre = "pop";
    }

    // Getters et setters
    public int getNumberOfRounds() {
        return numberOfRounds;
    }

    public void setNumberOfRounds(int numberOfRounds) {
        this.numberOfRounds = numberOfRounds;
    }

    public int getExtractDuration() {
        return extractDuration;
    }

    public void setExtractDuration(int extractDuration) {
        this.extractDuration = extractDuration;
    }

    public boolean isHintsEnabled() {
        return hintsEnabled;
    }

    public void setHintsEnabled(boolean hintsEnabled) {
        this.hintsEnabled = hintsEnabled;
    }

    public boolean isSpeedBonusEnabled() {
        return speedBonusEnabled;
    }

    public void setSpeedBonusEnabled(boolean speedBonusEnabled) {
        this.speedBonusEnabled = speedBonusEnabled;
    }

    public double getDefaultVolume() {
        return defaultVolume;
    }

    public void setDefaultVolume(double defaultVolume) {
        this.defaultVolume = defaultVolume;
    }

    public String getDefaultGenre() {
        return defaultGenre;
    }

    public void setDefaultGenre(String defaultGenre) {
        this.defaultGenre = defaultGenre;
    }
}
