/**package com.blindtest.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    @Test
    void player_hasName_and_defaultScoreIsZero_thenCanAddScore() {
        Player p = new Player("Alice");
        assertEquals("Alice", p.getName());

        int initial = p.getScore();
        assertEquals(0, initial);

        p.addScore(10);
        assertEquals(initial + 10, p.getScore());
    }
}
*/