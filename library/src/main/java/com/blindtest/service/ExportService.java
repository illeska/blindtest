package com.blindtest.service;

import com.blindtest.model.Score;
import com.blindtest.util.InputValidator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service pour exporter les données du leaderboard dans différents formats.
 * Supporte l'export en CSV et JSON.
 */
public class ExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    /**
     * Exporte une liste de scores au format CSV.
     * 
     * @param scores La liste de scores à exporter
     * @param filePath Le chemin du fichier CSV de destination
     * @throws IOException En cas d'erreur d'écriture
     */
    public static void exportToCSV(List<Score> scores, String filePath) throws IOException {
        PersistenceService.ensureDirectoryExists(filePath);

        try (FileWriter writer = new FileWriter(filePath)) {
            // En-tête CSV
            writer.append("Rang,Pseudo,Score,Mode,Genre,Date,Titres corrects,Artistes corrects,Total morceaux,Indices utilisés,Taux réussite (%)\n");

            // Données
            int rank = 1;
            for (Score score : scores) {
                writer.append(String.valueOf(rank)).append(",");
                writer.append(InputValidator.sanitizeForCSV(score.getPseudo())).append(",");
                writer.append(String.valueOf(score.getScore())).append(",");
                writer.append(InputValidator.sanitizeForCSV(score.getMode())).append(",");
                writer.append(InputValidator.sanitizeForCSV(score.getGenre())).append(",");
                writer.append(score.getDate().format(DATE_FORMATTER)).append(",");
                writer.append(String.valueOf(score.getCorrectTitles())).append(",");
                writer.append(String.valueOf(score.getCorrectArtists())).append(",");
                writer.append(String.valueOf(score.getTotalTracks())).append(",");
                writer.append(String.valueOf(score.getHintsUsed())).append(",");
                writer.append(String.format("%.2f", score.getSuccessRate())).append("\n");
                rank++;
            }

            System.out.println("[ExportService] Export CSV réussi : " + filePath);
        }
    }

    /**
     * Exporte une liste de scores au format JSON.
     * 
     * @param scores La liste de scores à exporter
     * @param filePath Le chemin du fichier JSON de destination
     * @throws IOException En cas d'erreur d'écriture
     */
    public static void exportToJSON(List<Score> scores, String filePath) throws IOException {
        PersistenceService.ensureDirectoryExists(filePath);

        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(scores, writer);
            System.out.println("[ExportService] Export JSON réussi : " + filePath);
        }
    }

    /**
     * Exporte le leaderboard complet au format CSV.
     * 
     * @param filePath Le chemin du fichier CSV de destination
     * @throws IOException En cas d'erreur d'écriture
     */
    public static void exportLeaderboardToCSV(String filePath) throws IOException {
        List<Score> leaderboard = ScoreService.getLeaderboard(100); // Top 100
        exportToCSV(leaderboard, filePath);
    }

    /**
     * Exporte le leaderboard complet au format JSON.
     * 
     * @param filePath Le chemin du fichier JSON de destination
     * @throws IOException En cas d'erreur d'écriture
     */
    public static void exportLeaderboardToJSON(String filePath) throws IOException {
        List<Score> leaderboard = ScoreService.getLeaderboard(100); // Top 100
        exportToJSON(leaderboard, filePath);
    }

    /**
     * Exporte le leaderboard filtré par mode au format CSV.
     * 
     * @param mode Le mode de jeu ("Solo" ou "Duel")
     * @param filePath Le chemin du fichier CSV de destination
     * @throws IOException En cas d'erreur d'écriture
     */
    public static void exportLeaderboardByModeToCSV(String mode, String filePath) throws IOException {
        List<Score> leaderboard = ScoreService.getLeaderboardByMode(mode, 100);
        exportToCSV(leaderboard, filePath);
    }

    /**
     * Exporte le leaderboard filtré par mode au format JSON.
     * 
     * @param mode Le mode de jeu ("Solo" ou "Duel")
     * @param filePath Le chemin du fichier JSON de destination
     * @throws IOException En cas d'erreur d'écriture
     */
    public static void exportLeaderboardByModeToJSON(String mode, String filePath) throws IOException {
        List<Score> leaderboard = ScoreService.getLeaderboardByMode(mode, 100);
        exportToJSON(leaderboard, filePath);
    }

    /**
     * Génère un rapport de statistiques au format texte.
     * 
     * @param stats Les statistiques à inclure dans le rapport
     * @param filePath Le chemin du fichier texte de destination
     * @throws IOException En cas d'erreur d'écriture
     */
    public static void exportStatisticsReport(ScoreService.ScoreStatistics stats, String filePath) throws IOException {
        PersistenceService.ensureDirectoryExists(filePath);

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("=== RAPPORT DE STATISTIQUES BLINDTEST ===\n\n");
            writer.write("Généré le : " + LocalDateTime.now().format(DATE_FORMATTER) + "\n\n");
            writer.write("Nombre total de parties : " + stats.getTotalGames() + "\n");
            writer.write("Score moyen : " + String.format("%.2f", stats.getAverageScore()) + "\n");
            writer.write("Meilleur score : " + stats.getMaxScore() + "\n");
            writer.write("Score le plus bas : " + stats.getMinScore() + "\n\n");
            writer.write("--- Taux de réussite ---\n");
            writer.write("Taux de réussite global : " + String.format("%.2f%%", stats.getAvgSuccessRate()) + "\n");
            writer.write("Taux de réussite titres : " + String.format("%.2f%%", stats.getAvgTitleSuccessRate()) + "\n");
            writer.write("Taux de réussite artistes : " + String.format("%.2f%%", stats.getAvgArtistSuccessRate()) + "\n");

            System.out.println("[ExportService] Rapport de statistiques généré : " + filePath);
        }
    }

    /**
     * Génère un nom de fichier avec timestamp pour l'export.
     * 
     * @param prefix Le préfixe du nom de fichier
     * @param extension L'extension du fichier (ex: "csv", "json")
     * @return Le nom de fichier complet avec timestamp
     */
    public static String generateExportFilename(String prefix, String extension) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return String.format("data/exports/%s_%s.%s", prefix, timestamp, extension);
    }
}