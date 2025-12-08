package com.blindtest.service;

import com.blindtest.model.Score;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des scores (leaderboard et historique).
 * Utilise PersistenceService pour sauvegarder et charger les scores au format JSON.
 * 
 * 🆕 Sprint 4 : Ajout de filtres, statistiques et fonctionnalités avancées.
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
            System.err.println("[ScoreService] ERREUR: Impossible de sauvegarder les scores dans " 
                               + SCORES_FILE + ": " + e.getMessage());
        }
    }

    /**
     * Charge la liste des scores depuis le fichier JSON.
     * @return La liste des scores
     */
    public static List<Score> loadScores() {
        return PersistenceService.loadList(SCORES_FILE, new TypeToken<List<Score>>(){});
    }

    /**
     * Retourne le leaderboard : les meilleurs scores triés par score décroissant,
     * puis par date décroissante en cas d'égalité. Limite à 10 scores maximum.
     * @return La liste des scores du leaderboard
     */
    public static List<Score> getLeaderboard() {
        return getLeaderboard(10);
    }

    /**
     * Retourne le leaderboard avec une limite personnalisée.
     * @param limit Le nombre maximum de scores à retourner
     * @return La liste des scores du leaderboard
     */
    public static List<Score> getLeaderboard(int limit) {
        List<Score> scores = loadScores();
        return scores.stream()
                .sorted(Comparator.comparing(Score::getScore).reversed()
                        .thenComparing(Comparator.comparing(Score::getDate).reversed()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    // ========== 🆕 FILTRES (Sprint 4) ==========

    /**
     * Retourne le leaderboard filtré par mode de jeu.
     * @param mode Le mode de jeu ("Solo" ou "Duel")
     * @param limit Le nombre maximum de scores à retourner
     * @return La liste des scores filtrés
     */
    public static List<Score> getLeaderboardByMode(String mode, int limit) {
        List<Score> scores = loadScores();
        return scores.stream()
                .filter(score -> score.getMode() != null && score.getMode().equalsIgnoreCase(mode))
                .sorted(Comparator.comparing(Score::getScore).reversed()
                        .thenComparing(Comparator.comparing(Score::getDate).reversed()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Retourne le leaderboard filtré par genre musical.
     * @param genre Le genre musical (ex: "Pop", "Rock", etc.)
     * @param limit Le nombre maximum de scores à retourner
     * @return La liste des scores filtrés
     */
    public static List<Score> getLeaderboardByGenre(String genre, int limit) {
        List<Score> scores = loadScores();
        return scores.stream()
                .filter(score -> score.getGenre() != null && score.getGenre().equalsIgnoreCase(genre))
                .sorted(Comparator.comparing(Score::getScore).reversed()
                        .thenComparing(Comparator.comparing(Score::getDate).reversed()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Retourne le leaderboard filtré par mode ET genre.
     * @param mode Le mode de jeu
     * @param genre Le genre musical
     * @param limit Le nombre maximum de scores à retourner
     * @return La liste des scores filtrés
     */
    public static List<Score> getLeaderboardByModeAndGenre(String mode, String genre, int limit) {
        List<Score> scores = loadScores();
        return scores.stream()
                .filter(score -> score.getMode() != null && score.getMode().equalsIgnoreCase(mode))
                .filter(score -> score.getGenre() != null && score.getGenre().equalsIgnoreCase(genre))
                .sorted(Comparator.comparing(Score::getScore).reversed()
                        .thenComparing(Comparator.comparing(Score::getDate).reversed()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    // ========== 🆕 STATISTIQUES (Sprint 4) ==========

    /**
     * Calcule les statistiques globales de tous les scores.
     * @return Un objet ScoreStatistics avec les statistiques calculées
     */
    public static ScoreStatistics getGlobalStatistics() {
        List<Score> scores = loadScores();
        return calculateStatistics(scores);
    }

    /**
     * Calcule les statistiques filtrées par mode.
     * @param mode Le mode de jeu
     * @return Un objet ScoreStatistics avec les statistiques calculées
     */
    public static ScoreStatistics getStatisticsByMode(String mode) {
        List<Score> scores = loadScores().stream()
                .filter(score -> score.getMode() != null && score.getMode().equalsIgnoreCase(mode))
                .collect(Collectors.toList());
        return calculateStatistics(scores);
    }

    /**
     * Calcule les statistiques d'un joueur spécifique.
     * @param pseudo Le pseudo du joueur
     * @return Un objet ScoreStatistics avec les statistiques calculées
     */
    public static ScoreStatistics getPlayerStatistics(String pseudo) {
        List<Score> scores = getPlayerHistory(pseudo);
        return calculateStatistics(scores);
    }

    /**
     * Méthode privée pour calculer les statistiques à partir d'une liste de scores.
     * @param scores La liste de scores
     * @return Un objet ScoreStatistics
     */
    private static ScoreStatistics calculateStatistics(List<Score> scores) {
        if (scores.isEmpty()) {
            return new ScoreStatistics(0, 0, 0, 0, 0.0, 0.0, 0.0);
        }

        int totalGames = scores.size();
        int totalScore = scores.stream().mapToInt(Score::getScore).sum();
        int maxScore = scores.stream().mapToInt(Score::getScore).max().orElse(0);
        int minScore = scores.stream().mapToInt(Score::getScore).min().orElse(0);
        double averageScore = (double) totalScore / totalGames;

        // Calcul des taux de réussite moyens
        double avgSuccessRate = scores.stream()
                .mapToDouble(Score::getSuccessRate)
                .average()
                .orElse(0.0);

        double avgTitleSuccessRate = scores.stream()
                .mapToDouble(Score::getTitleSuccessRate)
                .average()
                .orElse(0.0);

        double avgArtistSuccessRate = scores.stream()
                .mapToDouble(Score::getArtistSuccessRate)
                .average()
                .orElse(0.0);

        return new ScoreStatistics(
                totalGames,
                maxScore,
                minScore,
                averageScore,
                avgSuccessRate,
                avgTitleSuccessRate,
                avgArtistSuccessRate
        );
    }

    /**
     * Retourne la distribution des scores par mode de jeu.
     * @return Une map avec le nombre de parties par mode
     */
    public static Map<String, Long> getScoreDistributionByMode() {
        List<Score> scores = loadScores();
        return scores.stream()
                .filter(score -> score.getMode() != null)
                .collect(Collectors.groupingBy(
                        Score::getMode,
                        Collectors.counting()
                ));
    }

    /**
     * Retourne la distribution des scores par genre musical.
     * @return Une map avec le nombre de parties par genre
     */
    public static Map<String, Long> getScoreDistributionByGenre() {
        List<Score> scores = loadScores();
        return scores.stream()
                .filter(score -> score.getGenre() != null)
                .collect(Collectors.groupingBy(
                        Score::getGenre,
                        Collectors.counting()
                ));
    }

    // ========== MÉTHODES EXISTANTES ==========

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

    // ========== CLASSE INTERNE POUR LES STATISTIQUES ==========

    /**
     * Classe pour encapsuler les statistiques calculées.
     */
    public static class ScoreStatistics {
        private final int totalGames;
        private final int maxScore;
        private final int minScore;
        private final double averageScore;
        private final double avgSuccessRate;
        private final double avgTitleSuccessRate;
        private final double avgArtistSuccessRate;

        public ScoreStatistics(int totalGames, int maxScore, int minScore, double averageScore,
                               double avgSuccessRate, double avgTitleSuccessRate, double avgArtistSuccessRate) {
            this.totalGames = totalGames;
            this.maxScore = maxScore;
            this.minScore = minScore;
            this.averageScore = averageScore;
            this.avgSuccessRate = avgSuccessRate;
            this.avgTitleSuccessRate = avgTitleSuccessRate;
            this.avgArtistSuccessRate = avgArtistSuccessRate;
        }

        public int getTotalGames() { return totalGames; }
        public int getMaxScore() { return maxScore; }
        public int getMinScore() { return minScore; }
        public double getAverageScore() { return averageScore; }
        public double getAvgSuccessRate() { return avgSuccessRate; }
        public double getAvgTitleSuccessRate() { return avgTitleSuccessRate; }
        public double getAvgArtistSuccessRate() { return avgArtistSuccessRate; }

        @Override
        public String toString() {
            return String.format(
                    "Stats: %d parties | Score moyen: %.1f | Meilleur: %d | Taux réussite: %.1f%%",
                    totalGames, averageScore, maxScore, avgSuccessRate
            );
        }
    }
}