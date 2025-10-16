/** package com.blindtest.controller;

import com.blindtest.model.Round;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameControllerTest {

    @Test
    void startGame_initializesAndPlaysFirstRound() {
        GameController gc = new GameController(2);
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
        GameController gc = new GameController(2);
        gc.startGame();

        gc.nextRound();
        assertEquals(1, gc.getCurrentRoundIndex());

        // Appel supplémentaire : on ne dépasse pas la dernière manche
        gc.nextRound();
        assertEquals(1, gc.getCurrentRoundIndex());
    }

    @Test
    void nextRound_beforeStart_throws() {
        GameController gc = new GameController(1);
        assertThrows(IllegalStateException.class, gc::nextRound);
    }
}
*/