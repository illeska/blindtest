package com.blindtest.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour la classe Player.
 */
public class PlayerTest {
    
    /**
     * Vérifie que le constructeur initialise correctement le nom et un score à zéro.
     */
    @Test
    void constructor_shouldInitializeNameAndZeroScore() {
        Player player = new Player("Alice");
        
        assertEquals("Alice", player.getName());
        assertEquals(0, player.getScore());
    }
    
    /**
     * Vérifie que addScore incrémente correctement le score du joueur.
     */
    @Test
    void addScore_shouldIncrementScore() {
        Player player = new Player("Bob");
        
        player.addScore(10);
        assertEquals(10, player.getScore());
        
        player.addScore(5);
        assertEquals(15, player.getScore());
    }
    
    /**
     * Vérifie que addScore avec des points négatifs diminue le score.
     */
    @Test
    void addScore_withNegativePoints_shouldDecreaseScore() {
        Player player = new Player("Charlie");
        player.addScore(20);
        
        player.addScore(-5);
        assertEquals(15, player.getScore());
    }
    
    /**
     * Vérifie que plusieurs ajouts de score s'accumulent correctement.
     */
    @Test
    void addScore_multipleAdditions_shouldAccumulateCorrectly() {
        Player player = new Player("Diana");
        
        player.addScore(10);
        player.addScore(20);
        player.addScore(30);
        
        assertEquals(60, player.getScore());
    }
    
    /**
     * Vérifie que le constructeur accepte un nom vide.
     */
    @Test
    void constructor_withEmptyName_shouldStillWork() {
        Player player = new Player("");
        assertEquals("", player.getName());
    }
}