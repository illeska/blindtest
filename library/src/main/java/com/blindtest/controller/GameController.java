package com.blindtest.controller;

import com.blindtest.model.Round;
import java.util.ArrayList;
import java.util.List;

/**
 * Contrôleur principal de partie.
 * Sprint 1 : logique minimale + enchaînement des manches (mock audio).
 * Voir CDC (Scoring & Round flow, Sprint 1: mock audio). 
 */
public class GameController {

    private final List<Round> rounds = new ArrayList<>();
    private int currentRoundIndex = -1;
    private boolean started = false;

    /**
     * @param numberOfRounds nombre de manches configuré (paramètres jeu)
     */
    public GameController(int numberOfRounds) {
        if (numberOfRounds <= 0) {
            throw new IllegalArgumentException("numberOfRounds must be > 0");
        }
        for (int i = 0; i < numberOfRounds; i++) {
            rounds.add(new Round());
        }
    }

    /** Démarre la partie et lance la 1ère manche (mock lecture). */
    public void startGame() {
        if (started) return;
        started = true;
        currentRoundIndex = 0;
        rounds.get(currentRoundIndex).playExtract(); // mock audio
    }

    /** Passe à la manche suivante et lance la lecture (mock). */
    public void nextRound() {
        if (!started) {
            throw new IllegalStateException("Game not started");
        }
        if (currentRoundIndex + 1 >= rounds.size()) {
            // Fin de partie (Sprint 1: simple message console)
            System.out.println("Partie terminée.");
            return;
        }
        currentRoundIndex++;
        rounds.get(currentRoundIndex).playExtract();
    }

    // --- Helpers pour tests/GUI ---
    public boolean isStarted() { return started; }
    public int getCurrentRoundIndex() { return currentRoundIndex; }
    public int getNumberOfRounds() { return rounds.size(); }
    public Round getCurrentRound() {
        if (currentRoundIndex >= 0 && currentRoundIndex < rounds.size()) {
            return rounds.get(currentRoundIndex);
        }
        return null;
    }
}
