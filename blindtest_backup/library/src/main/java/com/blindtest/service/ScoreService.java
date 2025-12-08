package com.blindtest.service;

import com.blindtest.model.Score;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des scores (leaderboard et historique).
 * Utilise PersistenceService pour sauvegarder et charger les scores au format JSON.
 */
public class ScoreService {
    private static final String SCORES_FILE = "data/scores.json";

    /**
     * Sauvegarde un score dans le fichier JSON.
     * @param score Le score à sauvegarder
     */
    public static void saveScore(Score score) {
        List<Score> scores = loadScores();
        scores.add(score);
        try {
            PersistenceService.save(scores, SCORES_FILE);
        } catch (IOException e) {
            // 🔥 MODIFICATION : Meilleure gestion d'erreur
            System.err.println("[ScoreService] ERREUR: Impossible de sauvegarder les scores dans " + SCORES_FILE + ": " + e.getMessage());
        }
    }

    /**
     * Charge la liste des scores depuis le fichier JSON.
     * @return La liste des scores
     */
    public static List<Score> loadScores() {
        // PersistenceService.loadList gère déjà les erreurs de lecture et retourne une liste vide si le fichier n'existe pas.
        return PersistenceService.loadList(SCORES_FILE, new TypeToken<List<Score>>(){});
    }

    /**
     * Retourne le leaderboard : les meilleurs scores triés par score décroissant,
     * puis par date décroissante en cas d'égalité. Limite à 10 scores maximum.
     * @return La liste des scores du leaderboard
     */
    public static List<Score> getLeaderboard() {
        List<Score> scores = loadScores();
        return scores.stream()
                .sorted(Comparator.comparing(Score::getScore).reversed()
                        .thenComparing(Comparator.comparing(Score::getDate).reversed()))
                .limit(10)
                .collect(Collectors.toList());
    }

    /**
     * Retourne l'historique des scores d'un joueur donné, trié par date décroissante.
     * @param pseudo Le pseudo du joueur
     * @return La liste des scores du joueur
     */
    public static List<Score> getPlayerHistory(String pseudo) {
        List<Score> scores = loadScores();
        return scores.stream()
                .filter(score -> score.getPseudo().equals(pseudo))
                .sorted(Comparator.comparing(Score::getDate).reversed())
                .collect(Collectors.toList());
    }
}