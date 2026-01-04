package com.blindtest.controller;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.blindtest.model.Player;
import com.blindtest.model.Round;

public class GameControllerTest {

    private static boolean javaFXInitialized = false;

    /**
     * Initialise JavaFX pour les tests nécessitant des composants graphiques.
     */
    @BeforeAll
    static void initJavaFX() {
        try {
            // Initialisation de JavaFX pour les tests
            javafx.embed.swing.JFXPanel panel = new javafx.embed.swing.JFXPanel();
            javaFXInitialized = true;
            System.out.println("✓ JavaFX initialisé pour les tests");
        } catch (Exception e) {
            System.err.println("✗ JavaFX ne peut pas être initialisé: " + e.getMessage());
            javaFXInitialized = false;
        }
    }

    /**
     * Teste que startGame initialise le jeu et lance le premier round.
     */
    @Test
    void startGame_initializesAndPlaysFirstRound() {
        Assumptions.assumeTrue(javaFXInitialized, "JavaFX n'est pas disponible");
        
        List<Player> players = Arrays.asList(new Player("Test"));
        GameController gc = new GameController(players);
        assertFalse(gc.isStarted());
        assertEquals(-1, gc.getCurrentRoundIndex());

        gc.startGame();
        
        // Petite pause pour laisser JavaFX s'initialiser
        try { Thread.sleep(500); } catch (InterruptedException e) {}
        
        assertTrue(gc.isStarted());
        assertEquals(0, gc.getCurrentRoundIndex());
        assertNotNull(gc.getCurrentRound());
        
        // Arrêter proprement pour éviter le crash
        try {
            gc.getCurrentRound();
        } catch (Exception e) {
            // Ignorer les erreurs
        }
    }


    /**
     * Teste que nextRound lance une exception si le jeu n'est pas démarré.
     */
    @Test
    void nextRound_beforeStart_throws() {
        List<Player> players = Arrays.asList(new Player("Test"));
        GameController gc = new GameController(players);
        assertThrows(IllegalStateException.class, gc::nextRound);
    }

    /**
     * Teste la vérification d'une réponse correcte pour le titre et l'artiste.
     */
    @Test
    void checkAnswer_correctTitleArtist() {
        Assumptions.assumeTrue(javaFXInitialized, "JavaFX n'est pas disponible");
        
        List<Player> players = Arrays.asList(new Player("Test"));
        GameController gc = new GameController(players);
        gc.startGame();

        // Pause pour initialisation
        try { Thread.sleep(500); } catch (InterruptedException e) {}

        Round round = gc.getCurrentRound();
        assertNotNull(round);
        assertNotNull(round.getTrack());
        
        String correctTitle = round.getTrack().getTitle();
        String correctArtist = round.getTrack().getArtist();

        gc.checkAnswer(correctTitle, correctArtist, 15, 0);
        
        // Vérification après un délai
        try { Thread.sleep(200); } catch (InterruptedException e) {}
        assertEquals(2, players.get(0).getScore());
    }

    /**
     * Teste la vérification d'une réponse partiellement correcte.
     */
    @Test
    void checkAnswer_partialCorrect() {
        Assumptions.assumeTrue(javaFXInitialized, "JavaFX n'est pas disponible");
        
        List<Player> players = Arrays.asList(new Player("Test"));
        GameController gc = new GameController(players);
        gc.startGame();

        try { Thread.sleep(500); } catch (InterruptedException e) {}

        Round round = gc.getCurrentRound();
        String correctTitle = round.getTrack().getTitle();

        gc.checkAnswer(correctTitle, "Wrong Artist", 20, 0);
        
        try { Thread.sleep(200); } catch (InterruptedException e) {}
        assertEquals(1, players.get(0).getScore());
    }


    /**
     * Teste l'attribution d'un bonus de vitesse pour une réponse rapide.
     */
    @Test
    void checkAnswer_speedBonus() {
        Assumptions.assumeTrue(javaFXInitialized, "JavaFX n'est pas disponible");
        
        List<Player> players = Arrays.asList(new Player("Test"));
        GameController gc = new GameController(players);
        gc.startGame();

        try { Thread.sleep(500); } catch (InterruptedException e) {}

        Round round = gc.getCurrentRound();
        String correctTitle = round.getTrack().getTitle();
        String correctArtist = round.getTrack().getArtist();

        int duration = gc.getSettings().getExtractDuration();
        long fastTime = duration / 4;

        gc.checkAnswer(correctTitle, correctArtist, fastTime, 0);
        
        try { Thread.sleep(200); } catch (InterruptedException e) {}
        assertEquals(3, players.get(0).getScore());
    }

    /**
     * Teste la vérification d'une réponse incorrecte.
     */
    @Test
    void checkAnswer_wrongAnswer() {
        Assumptions.assumeTrue(javaFXInitialized, "JavaFX n'est pas disponible");
        
        List<Player> players = Arrays.asList(new Player("Test"));
        GameController gc = new GameController(players);
        gc.startGame();

        try { Thread.sleep(500); } catch (InterruptedException e) {}

        gc.checkAnswer("Wrong", "Wrong", 10, 0);
        
        try { Thread.sleep(200); } catch (InterruptedException e) {}
        assertEquals(0, players.get(0).getScore());
    }


    /**
     * Teste que le constructeur rejette une liste de joueurs vide.
     */
    @Test
    void constructor_invalidPlayers() {
        assertThrows(IllegalArgumentException.class, () -> new GameController(Arrays.asList()));
    }

    /**
     * Teste que le constructeur crée les rounds selon les paramètres.
     */
    @Test
    void constructor_createsRoundsBasedOnSettings() {
        List<Player> players = Arrays.asList(new Player("Test"));
        GameController gc = new GameController(players);
        
        assertTrue(gc.getNumberOfRounds() > 0);
        assertEquals(gc.getSettings().getNumberOfRounds(), gc.getNumberOfRounds());
    }

    /**
     * Teste que getPlayers retourne la liste correcte des joueurs.
     */
    @Test
    void getPlayers_returnsCorrectPlayers() {
        List<Player> players = Arrays.asList(new Player("Alice"), new Player("Bob"));
        GameController gc = new GameController(players);
        
        assertEquals(2, gc.getPlayers().size());
        assertEquals("Alice", gc.getPlayers().get(0).getName());
        assertEquals("Bob", gc.getPlayers().get(1).getName());
    }

    /**
     * Teste la demande d'indice quand les indices sont activés.
     */
    @Test
    void requestHint_returnsHintWhenEnabled() {
        Assumptions.assumeTrue(javaFXInitialized, "JavaFX n'est pas disponible");
        
        List<Player> players = Arrays.asList(new Player("Test"));
        GameController gc = new GameController(players);
        
        if (!gc.getSettings().isHintsEnabled()) {
            System.out.println("Test skipped: hints are disabled in settings");
            return;
        }
        
        gc.startGame();
        
        try { Thread.sleep(500); } catch (InterruptedException e) {}
        
        String hint = gc.requestHint();
        assertNotNull(hint);
    }
    

    /**
     * Teste que getCurrentRound retourne null avant le démarrage.
     */
    @Test
    void getCurrentRound_returnsNullBeforeStart() {
        List<Player> players = Arrays.asList(new Player("Test"));
        GameController gc = new GameController(players);
        
        assertNull(gc.getCurrentRound());
    }
    
    /**
     * Teste que getCurrentRound retourne un round après le démarrage.
     */
    @Test
    void getCurrentRound_returnsRoundAfterStart() {
        Assumptions.assumeTrue(javaFXInitialized, "JavaFX n'est pas disponible");
        
        List<Player> players = Arrays.asList(new Player("Test"));
        GameController gc = new GameController(players);
        
        gc.startGame();
        try { Thread.sleep(500); } catch (InterruptedException e) {}
        
        Round round = gc.getCurrentRound();
        assertNotNull(round);
        assertNotNull(round.getTrack());
    }
}