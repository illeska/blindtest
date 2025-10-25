package com.blindtest.service;

import com.blindtest.model.Score;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.List;

/**
 * Service pour la gestion des scores (leaderboard et historique).
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
            e.printStackTrace();
        }
    }

    /**
     * Charge la liste des scores depuis le fichier JSON.
     * @return La liste des scores
     */
    public static List<Score> loadScores() {
        return PersistenceService.loadList(SCORES_FILE, new TypeToken<List<Score>>(){});
    }
}
