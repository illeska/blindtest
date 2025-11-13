package com.blindtest.controller;

import com.blindtest.model.Player;
import com.blindtest.model.Round;
import com.blindtest.model.Score;
import com.blindtest.service.AudioService;
import com.blindtest.service.ScoreService;
import java.util.ArrayList;
import java.util.List;

/**
 * Contrôleur principal de partie.
 * Sprint 1 : logique minimale + enchaînement des manches (mock audio).
 * Voir CDC (Scoring &amp; Round flow, Sprint 1: mock audio).
 * Week 1 : ajout de la persistance des scores.
 */
public class GameController {

    private final AudioService audioService = new AudioService(); // 🔥 ajouté (audio réel)
    private final List<Round> rounds = new ArrayList<>();
    private final List<Player> players = new ArrayList<>();
    private int currentRoundIndex = -1;
    private boolean started = false;

    /**
     * Constructeur pour initialiser le contrôleur de jeu.
     * @param numberOfRounds nombre de manches configuré (paramètres jeu)
     * @param players liste des joueurs (pour le mode solo ou duel)
     */
    public GameController(int numberOfRounds, List<Player> players) {
        if (numberOfRounds <= 0) {
            throw new IllegalArgumentException("numberOfRounds must be > 0");
        }
        if (players == null || players.isEmpty()) {
            throw new IllegalArgumentException("At least one player required");
        }
        this.players.addAll(players);
        for (int i = 0; i < numberOfRounds; i++) {
            rounds.add(new Round());
        }
    }

    /**
     * Démarre la partie et lance la 1ère manche (mock lecture).
     */
    public void startGame() {
        if (started) return;
        started = true;
        currentRoundIndex = 0;

        // 🔥 AUDIO RÉEL AJOUTÉ
        Round round = rounds.get(currentRoundIndex);
        if (round.getTrack() != null) {
            String query = round.getTrack().getTitle() + " " + round.getTrack().getArtist();
            audioService.loadWithFallback(query);
            audioService.play();
        }

        // MOCK CONSERVÉ (RIEN SUPPRIMÉ)
        rounds.get(currentRoundIndex).playExtract(); // mock audio
    }

    /**
     * Passe à la manche suivante et lance la lecture (mock).
     */
    public void nextRound() {
        if (!started) {
            throw new IllegalStateException("Game not started");
        }
        if (currentRoundIndex + 1 >= rounds.size()) {
            // Fin de partie : sauvegarder les scores
            endGame();
            return;
        }
        currentRoundIndex++;

        // 🔥 AUDIO RÉEL AJOUTÉ
        Round round = rounds.get(currentRoundIndex);
        if (round.getTrack() != null) {
            String query = round.getTrack().getTitle() + " " + round.getTrack().getArtist();
            audioService.loadWithFallback(query);
            audioService.play();
        }

        // MOCK CONSERVÉ (RIEN SUPPRIMÉ)
        rounds.get(currentRoundIndex).playExtract();
    }

    /**
     * Termine la partie et sauvegarde les scores.
     */
    private void endGame() {

        // 🔥 AJOUT : stop audio quand la partie finit
        audioService.stop();

        System.out.println("Partie terminée.");
        for (Player player : players) {
            Score score = new Score(player.getName(), player.getScore());
            ScoreService.saveScore(score);
            System.out.println("Score sauvegardé pour " + player.getName() + ": " + player.getScore());
        }
    }

    /**
     * Vérifie si la partie a démarré.
     * @return true si démarrée, false sinon
     */
    public boolean isStarted() { return started; }

    /**
     * Retourne l'index de la manche actuelle.
     * @return L'index de la manche actuelle
     */
    public int getCurrentRoundIndex() { return currentRoundIndex; }

    /**
     * Retourne le nombre total de manches.
     * @return Le nombre de manches
     */
    public int getNumberOfRounds() { return rounds.size(); }

    /**
     * Retourne la manche actuelle.
     * @return La manche actuelle ou null si aucune
     */
    public Round getCurrentRound() {
        if (currentRoundIndex >= 0 && currentRoundIndex < rounds.size()) {
            return rounds.get(currentRoundIndex);
        }
        return null;
    }

    /**
     * Retourne la liste des joueurs.
     * @return La liste des joueurs
     */
    public List<Player> getPlayers() { return players; }
}
