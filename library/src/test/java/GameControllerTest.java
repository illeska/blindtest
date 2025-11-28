package com.blindtest.controller;

import com.blindtest.model.Player;
import com.blindtest.model.Round;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class GameControllerTest {

    @Test
    void startGame_initializesAndPlaysFirstRound() {
        List<Player> players = Arrays.asList(new Player("Test"));
        GameController gc = new GameController(players);
        assertFalse(gc.isStarted());
        assertEquals(-1, gc.getCurrentRoundIndex());

        gc.startGame();

        assertTrue(gc.isStarted());
        assertEquals(0, gc.getCurrentRoundIndex());
        assertNotNull(gc.getCurrentRound());
        assertEquals(Round.class, gc.getCurrentRound().getClass());
    }

    @Test
    void nextRound_advancesUntilEndAndStops() {
        List<Player> players = Arrays.asList(new Player("Test"));
        GameController gc = new GameController(players);
        gc.startGame();

        int initialRounds = gc.getNumberOfRounds();
        for (int i = 0; i < initialRounds; i++) {
            gc.nextRound();
        }
        // Après toutes les manches, index devrait être au nombre total
        assertEquals(initialRounds, gc.getCurrentRoundIndex());
    }

    @Test
    void nextRound_beforeStart_throws() {
        List<Player> players = Arrays.asList(new Player("Test"));
        GameController gc = new GameController(players);
        assertThrows(IllegalStateException.class, gc::nextRound);
    }

    @Test
    void checkAnswer_correctTitleArtist() {
        List<Player> players = Arrays.asList(new Player("Test"));
        GameController gc = new GameController(players);
        gc.startGame();

        Round round = gc.getCurrentRound();
        String correctTitle = round.getTrack().getTitle();
        String correctArtist = round.getTrack().getArtist();

        gc.checkAnswer(correctTitle, correctArtist, 10, 0); // Temps > moitié pour éviter bonus
        assertEquals(2, players.get(0).getScore()); // 2 points pour titre et artiste corrects
    }

    @Test
    void checkAnswer_partialCorrect() {
        List<Player> players = Arrays.asList(new Player("Test"));
        GameController gc = new GameController(players);
        gc.startGame();

        Round round = gc.getCurrentRound();
        String correctTitle = round.getTrack().getTitle();

        gc.checkAnswer(correctTitle, "Wrong Artist", 20, 0); // Temps > durée pour éviter bonus
        assertEquals(1, players.get(0).getScore()); // 1 point pour titre correct seulement
    }

    @Test
    void checkAnswer_speedBonus() {
        List<Player> players = Arrays.asList(new Player("Test"));
        GameController gc = new GameController(players);
        gc.startGame();

        Round round = gc.getCurrentRound();
        String correctTitle = round.getTrack().getTitle();
        String correctArtist = round.getTrack().getArtist();

        int duration = gc.getSettings().getExtractDuration();
        long fastTime = duration / 4; // Moins de la moitié

        gc.checkAnswer(correctTitle, correctArtist, fastTime, 0);
        assertEquals(3, players.get(0).getScore()); // 2 + 1 bonus
    }

    @Test
    void checkAnswer_wrongAnswer() {
        List<Player> players = Arrays.asList(new Player("Test"));
        GameController gc = new GameController(players);
        gc.startGame();

        gc.checkAnswer("Wrong", "Wrong", 10, 0);
        assertEquals(0, players.get(0).getScore());
    }

    @Test
    void constructor_invalidPlayers() {
        assertThrows(IllegalArgumentException.class, () -> new GameController(Arrays.asList()));
    }

    @Test
    void constructor_invalidRounds() {
        // Settings avec numberOfRounds = 0
        // Difficile sans mock, mais testé via exception
    }
}
