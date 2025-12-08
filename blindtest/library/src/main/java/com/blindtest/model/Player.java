package com.blindtest.model;

/**
 * Représente un joueur avec un nom et un score.
 */
public class Player {
    private String name;
    private int score;

    /**
     * Constructeur pour créer un joueur avec un nom.
     * @param name Le nom du joueur
     */
    public Player(String name) {
        this.name = name;
        this.score = 0;
    }

    /**
     * Retourne le nom du joueur.
     * @return Le nom du joueur
     */
    public String getName() {
        return name;
    }

    /**
     * Retourne le score actuel du joueur.
     * @return Le score du joueur
     */
    public int getScore() {
        return score;
    }

    /**
     * Ajoute des points au score du joueur.
     * @param points Le nombre de points à ajouter
     */
    public void addScore(int points) {
        this.score += points;
    }
}
