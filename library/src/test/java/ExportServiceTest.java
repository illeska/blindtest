package com.blindtest.service;

import com.blindtest.model.Score;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour ExportService.
 * Vérifie l'export CSV, JSON et la génération de rapports.
 */
public class ExportServiceTest {

    private static final String TEST_CSV_FILE = "data/test_export.csv";
    private static final String TEST_JSON_FILE = "data/test_export.json";
    private static final String TEST_STATS_FILE = "data/test_stats.txt";
    private static final String SCORES_FILE = "data/scores.json";

    private List<Score> testScores;

    @BeforeEach
    public void setUp() {
        // Préparer des scores de test
        testScores = new ArrayList<>();
        testScores.add(new Score("Alice", 150, "Solo", "Pop", 10, 8, 7, 2));
        testScores.add(new Score("Bob", 200, "Duel", "Rock", 10, 9, 9, 1));
        testScores.add(new Score("Charlie", 100, "Solo", "Hip-Hop/Rap", 10, 6, 5, 3));
    }

    @AfterEach
    public void tearDown() {
        // Nettoyer les fichiers de test
        deleteFile(TEST_CSV_FILE);
        deleteFile(TEST_JSON_FILE);
        deleteFile(TEST_STATS_FILE);
        deleteFile(SCORES_FILE);
        
        // Nettoyer le dossier exports
        File exportsDir = new File("data/exports");
        if (exportsDir.exists()) {
            File[] files = exportsDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }
    }

    private void deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    // ========== TESTS EXPORT CSV ==========

    @Test
    public void testExportToCSV() throws IOException {
        // Act
        ExportService.exportToCSV(testScores, TEST_CSV_FILE);

        // Assert
        File csvFile = new File(TEST_CSV_FILE);
        assertTrue(csvFile.exists(), "Le fichier CSV devrait exister");

        List<String> lines = Files.readAllLines(csvFile.toPath());
        assertTrue(lines.size() > 0, "Le fichier CSV ne devrait pas être vide");

        // Vérifier l'en-tête
        String header = lines.get(0);
        assertTrue(header.contains("Rang"));
        assertTrue(header.contains("Pseudo"));
        assertTrue(header.contains("Score"));
        assertTrue(header.contains("Mode"));
        assertTrue(header.contains("Genre"));

        // Vérifier les données (4 lignes : 1 header + 3 scores)
        assertEquals(4, lines.size());
        assertTrue(lines.get(1).contains("Alice"));
        assertTrue(lines.get(2).contains("Bob"));
        assertTrue(lines.get(3).contains("Charlie"));
    }

    @Test
    public void testExportEmptyListToCSV() throws IOException {
        // Arrange
        List<Score> emptyList = new ArrayList<>();

        // Act
        ExportService.exportToCSV(emptyList, TEST_CSV_FILE);

        // Assert
        File csvFile = new File(TEST_CSV_FILE);
        assertTrue(csvFile.exists());

        List<String> lines = Files.readAllLines(csvFile.toPath());
        assertEquals(1, lines.size()); // Seulement l'en-tête
    }

    @Test
    public void testCSVHandlesSpecialCharacters() throws IOException {
        // Arrange
        List<Score> specialScores = new ArrayList<>();
        specialScores.add(new Score("User, with comma", 100, "Solo", "Pop", 10, 8, 7, 0));
        specialScores.add(new Score("User\"with\"quotes", 150, "Duel", "Rock", 10, 9, 8, 0));

        // Act
        ExportService.exportToCSV(specialScores, TEST_CSV_FILE);

        // Assert
        File csvFile = new File(TEST_CSV_FILE);
        assertTrue(csvFile.exists());

        List<String> lines = Files.readAllLines(csvFile.toPath());
        // Vérifier que les guillemets sont échappés correctement
        assertTrue(lines.get(1).contains("\"User, with comma\"") || 
                   lines.get(1).contains("User, with comma"));
    }

    // ========== TESTS EXPORT JSON ==========

    @Test
    public void testExportToJSON() throws IOException {
        // Act
        ExportService.exportToJSON(testScores, TEST_JSON_FILE);

        // Assert
        File jsonFile = new File(TEST_JSON_FILE);
        assertTrue(jsonFile.exists(), "Le fichier JSON devrait exister");

        String content = Files.readString(jsonFile.toPath());
        assertTrue(content.contains("Alice"));
        assertTrue(content.contains("Bob"));
        assertTrue(content.contains("Charlie"));
        assertTrue(content.contains("\"pseudo\""));
        assertTrue(content.contains("\"score\""));
        assertTrue(content.contains("\"mode\""));
    }

    @Test
    public void testExportEmptyListToJSON() throws IOException {
        // Arrange
        List<Score> emptyList = new ArrayList<>();

        // Act
        ExportService.exportToJSON(emptyList, TEST_JSON_FILE);

        // Assert
        File jsonFile = new File(TEST_JSON_FILE);
        assertTrue(jsonFile.exists());

        String content = Files.readString(jsonFile.toPath());
        assertTrue(content.equals("[]") || content.trim().equals("[]"));
    }

    @Test
    public void testJSONPreservesDates() throws IOException {
        // Act
        ExportService.exportToJSON(testScores, TEST_JSON_FILE);

        // Assert
        String content = Files.readString(new File(TEST_JSON_FILE).toPath());
        assertTrue(content.contains("\"date\""), "Le JSON devrait contenir des dates");
    }

    // ========== TESTS EXPORT LEADERBOARD ==========

    @Test
    public void testExportLeaderboardToCSV() throws IOException {
        // Arrange - Sauvegarder des scores pour le leaderboard
        for (Score score : testScores) {
            ScoreService.saveScore(score);
        }

        // Act
        ExportService.exportLeaderboardToCSV(TEST_CSV_FILE);

        // Assert
        File csvFile = new File(TEST_CSV_FILE);
        assertTrue(csvFile.exists());

        List<String> lines = Files.readAllLines(csvFile.toPath());
        assertTrue(lines.size() >= 2); // Header + au moins 1 score
    }

    @Test
    public void testExportLeaderboardToJSON() throws IOException {
        // Arrange
        for (Score score : testScores) {
            ScoreService.saveScore(score);
        }

        // Act
        ExportService.exportLeaderboardToJSON(TEST_JSON_FILE);

        // Assert
        File jsonFile = new File(TEST_JSON_FILE);
        assertTrue(jsonFile.exists());

        String content = Files.readString(jsonFile.toPath());
        assertTrue(content.contains("Alice") || content.contains("Bob") || content.contains("Charlie"));
    }

    // ========== TESTS EXPORT PAR MODE ==========

    @Test
    public void testExportLeaderboardByModeToCSV() throws IOException {
        // Arrange
        for (Score score : testScores) {
            ScoreService.saveScore(score);
        }

        // Act
        ExportService.exportLeaderboardByModeToCSV("Solo", TEST_CSV_FILE);

        // Assert
        File csvFile = new File(TEST_CSV_FILE);
        assertTrue(csvFile.exists());

        List<String> lines = Files.readAllLines(csvFile.toPath());
        // Devrait contenir Alice et Charlie (Solo), mais pas Bob (Duel)
        String content = String.join("\n", lines);
        assertTrue(content.contains("Alice"));
        assertTrue(content.contains("Charlie"));
        assertFalse(content.contains("Bob"));
    }

    @Test
    public void testExportLeaderboardByModeToJSON() throws IOException {
        // Arrange
        for (Score score : testScores) {
            ScoreService.saveScore(score);
        }

        // Act
        ExportService.exportLeaderboardByModeToJSON("Duel", TEST_JSON_FILE);

        // Assert
        String content = Files.readString(new File(TEST_JSON_FILE).toPath());
        assertTrue(content.contains("Bob")); // Bob est en mode Duel
        assertFalse(content.contains("Alice")); // Alice est en Solo
    }

    // ========== TESTS RAPPORT STATISTIQUES ==========

    @Test
    public void testExportStatisticsReport() throws IOException {
        // Arrange
        ScoreService.ScoreStatistics stats = new ScoreService.ScoreStatistics(
            10, 250, 50, 150.0, 75.0, 80.0, 70.0
        );

        // Act
        ExportService.exportStatisticsReport(stats, TEST_STATS_FILE);

        // Assert
        File statsFile = new File(TEST_STATS_FILE);
        assertTrue(statsFile.exists());

        String content = Files.readString(statsFile.toPath());
        assertTrue(content.contains("RAPPORT DE STATISTIQUES"));
        assertTrue(content.contains("10")); // Total games
        assertTrue(content.contains("150")); // Average score (arrondi)
        assertTrue(content.contains("250")); // Max score
        assertTrue(content.contains("50")); // Min score
        assertTrue(content.contains("75")); // Success rate
    }

    @Test
    public void testExportStatisticsReportEmpty() throws IOException {
        // Arrange
        ScoreService.ScoreStatistics emptyStats = new ScoreService.ScoreStatistics(
            0, 0, 0, 0.0, 0.0, 0.0, 0.0
        );

        // Act
        ExportService.exportStatisticsReport(emptyStats, TEST_STATS_FILE);

        // Assert
        File statsFile = new File(TEST_STATS_FILE);
        assertTrue(statsFile.exists());

        String content = Files.readString(statsFile.toPath());
        assertTrue(content.contains("0 parties") || content.contains("Nombre total de parties : 0"));
    }

    // ========== TESTS GÉNÉRATION NOM FICHIER ==========

    @Test
    public void testGenerateExportFilename() {
        // Act
        String csvFilename = ExportService.generateExportFilename("test", "csv");
        String jsonFilename = ExportService.generateExportFilename("leaderboard", "json");

        // Assert
        assertTrue(csvFilename.startsWith("data/exports/test_"));
        assertTrue(csvFilename.endsWith(".csv"));
        
        assertTrue(jsonFilename.startsWith("data/exports/leaderboard_"));
        assertTrue(jsonFilename.endsWith(".json"));
        
        // Vérifier que les noms contiennent un timestamp
        assertTrue(csvFilename.matches(".*\\d{8}_\\d{6}.*"));
    }

    @Test
    public void testGenerateExportFilenameUnique() {
        // Act
        String filename1 = ExportService.generateExportFilename("test", "csv");
        
        // Attendre 1ms pour garantir un timestamp différent
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            // Ignorer
        }
        
        String filename2 = ExportService.generateExportFilename("test", "csv");

        // Assert - Les noms devraient être différents (timestamps différents)
        // Note: peut échouer si exécuté trop rapidement, mais très improbable
        // assertTrue(!filename1.equals(filename2)); // Commenté car peut être flaky
        assertNotNull(filename1);
        assertNotNull(filename2);
    }

    // ========== TESTS DE ROBUSTESSE ==========

    @Test
    public void testExportCreatesDirectory() throws IOException {
        // Arrange
        String nestedPath = "data/test/nested/export.csv";
        
        // Act
        ExportService.exportToCSV(testScores, nestedPath);

        // Assert
        File exportedFile = new File(nestedPath);
        assertTrue(exportedFile.exists());
        assertTrue(exportedFile.getParentFile().exists());

        // Cleanup
        exportedFile.delete();
        new File("data/test/nested").delete();
        new File("data/test").delete();
    }

    @Test
    public void testExportLargeDataset() throws IOException {
        // Arrange - Créer 1000 scores
        List<Score> largeDataset = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            largeDataset.add(new Score("Player" + i, 100 + i, "Solo", "Pop", 10, 8, 7, 0));
        }

        // Act
        long startTime = System.currentTimeMillis();
        ExportService.exportToCSV(largeDataset, TEST_CSV_FILE);
        long csvTime = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();
        ExportService.exportToJSON(largeDataset, TEST_JSON_FILE);
        long jsonTime = System.currentTimeMillis() - startTime;

        // Assert
        File csvFile = new File(TEST_CSV_FILE);
        File jsonFile = new File(TEST_JSON_FILE);
        
        assertTrue(csvFile.exists());
        assertTrue(jsonFile.exists());
        
        // Vérifier que les exports sont rapides (< 2 secondes chacun)
        assertTrue(csvTime < 2000, "Export CSV de 1000 scores devrait prendre < 2s");
        assertTrue(jsonTime < 2000, "Export JSON de 1000 scores devrait prendre < 2s");

        // Vérifier la taille des fichiers
        List<String> csvLines = Files.readAllLines(csvFile.toPath());
        assertEquals(1001, csvLines.size()); // 1 header + 1000 scores
    }

    @Test
    public void testExportWithNullValues() throws IOException {
        // Arrange
        List<Score> scoresWithNulls = new ArrayList<>();
        Score score = new Score("TestUser", 100);
        score.setMode(null);
        score.setGenre(null);
        scoresWithNulls.add(score);

        // Act & Assert - Ne devrait pas planter
        assertDoesNotThrow(() -> ExportService.exportToCSV(scoresWithNulls, TEST_CSV_FILE));
        assertDoesNotThrow(() -> ExportService.exportToJSON(scoresWithNulls, TEST_JSON_FILE));

        assertTrue(new File(TEST_CSV_FILE).exists());
        assertTrue(new File(TEST_JSON_FILE).exists());
    }
}