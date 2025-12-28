package com.blindtest.service;

import com.blindtest.model.Player;
import com.blindtest.model.Score;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour PersistenceService.
 * Vérifie la sauvegarde et le chargement de différents types de données.
 */
public class PersistenceServiceTest {
    
    private static final String TEST_FILE = "data/test_persistence.json";
    private static final String TEST_LIST_FILE = "data/test_list.json";

    @BeforeEach
    public void setUp() {
        // Nettoyer les fichiers de test avant chaque test
        deleteTestFiles();
    }

    @AfterEach
    public void tearDown() {
        // Nettoyer après chaque test
        deleteTestFiles();
    }

    private void deleteTestFiles() {
        new File(TEST_FILE).delete();
        new File(TEST_LIST_FILE).delete();
    }

    // ========== TESTS POUR SAVE/LOAD OBJET SIMPLE ==========

    @Test
    public void testSaveAndLoadPlayer() throws IOException {
        // Arrange
        Player player = new Player("TestPlayer");
        player.addScore(100);

        // Act
        PersistenceService.save(player, TEST_FILE);
        Player loadedPlayer = PersistenceService.load(TEST_FILE, Player.class);

        // Assert
        assertNotNull(loadedPlayer);
        assertEquals("TestPlayer", loadedPlayer.getName());
        assertEquals(100, loadedPlayer.getScore());
    }

    @Test
    public void testLoadNonExistentFile() {
        // Act
        Player loadedPlayer = PersistenceService.load("nonexistent.json", Player.class);

        // Assert
        assertNull(loadedPlayer, "Devrait retourner null pour un fichier inexistant");
    }

    @Test
    public void testSaveNullObject() {
        // Act & Assert
        assertDoesNotThrow(() -> PersistenceService.save(null, TEST_FILE));
    }

    @Test
    public void testOverwriteExistingFile() throws IOException {
        // Arrange
        Player player1 = new Player("Player1");
        player1.addScore(50);
        PersistenceService.save(player1, TEST_FILE);

        // Act
        Player player2 = new Player("Player2");
        player2.addScore(75);
        PersistenceService.save(player2, TEST_FILE);

        Player loadedPlayer = PersistenceService.load(TEST_FILE, Player.class);

        // Assert
        assertNotNull(loadedPlayer);
        assertEquals("Player2", loadedPlayer.getName());
        assertEquals(75, loadedPlayer.getScore());
    }

    // ========== TESTS POUR SAVE/LOAD LISTES ==========

    @Test
    public void testSaveAndLoadList() throws IOException {
        // Arrange
        List<Score> scores = new ArrayList<>();
        scores.add(new Score("Alice", 100));
        scores.add(new Score("Bob", 150));
        scores.add(new Score("Charlie", 200));

        // Act
        PersistenceService.save(scores, TEST_LIST_FILE);
        List<Score> loadedScores = PersistenceService.loadList(
            TEST_LIST_FILE, 
            new TypeToken<List<Score>>(){}
        );

        // Assert
        assertNotNull(loadedScores);
        assertEquals(3, loadedScores.size());
        assertEquals("Alice", loadedScores.get(0).getPseudo());
        assertEquals(100, loadedScores.get(0).getScore());
        assertEquals("Charlie", loadedScores.get(2).getPseudo());
        assertEquals(200, loadedScores.get(2).getScore());
    }

    @Test
    public void testLoadListNonExistentFile() {
        // Act
        List<Score> loadedScores = PersistenceService.loadList(
            "nonexistent.json", 
            new TypeToken<List<Score>>(){}
        );

        // Assert
        assertNotNull(loadedScores);
        assertTrue(loadedScores.isEmpty(), "Devrait retourner une liste vide pour un fichier inexistant");
    }

    @Test
    public void testSaveEmptyList() throws IOException {
        // Arrange
        List<Score> emptyList = new ArrayList<>();

        // Act
        PersistenceService.save(emptyList, TEST_LIST_FILE);
        List<Score> loadedScores = PersistenceService.loadList(
            TEST_LIST_FILE, 
            new TypeToken<List<Score>>(){}
        );

        // Assert
        assertNotNull(loadedScores);
        assertTrue(loadedScores.isEmpty());
    }

    @Test
    public void testAppendToList() throws IOException {
        // Arrange - Première sauvegarde
        List<Score> initialScores = new ArrayList<>();
        initialScores.add(new Score("Player1", 100));
        PersistenceService.save(initialScores, TEST_LIST_FILE);

        // Act - Charger, ajouter, sauvegarder
        List<Score> loadedScores = PersistenceService.loadList(
            TEST_LIST_FILE, 
            new TypeToken<List<Score>>(){}
        );
        loadedScores.add(new Score("Player2", 200));
        PersistenceService.save(loadedScores, TEST_LIST_FILE);

        // Assert - Recharger et vérifier
        List<Score> finalScores = PersistenceService.loadList(
            TEST_LIST_FILE, 
            new TypeToken<List<Score>>(){}
        );
        assertEquals(2, finalScores.size());
        assertEquals("Player1", finalScores.get(0).getPseudo());
        assertEquals("Player2", finalScores.get(1).getPseudo());
    }

    // ========== TESTS POUR GESTION DES RÉPERTOIRES ==========

    @Test
    public void testEnsureDirectoryExists() {
        // Arrange
        String nestedPath = "data/test/nested/dir/file.json";
        File parentDir = new File("data/test/nested/dir");

        // Act
        PersistenceService.ensureDirectoryExists(nestedPath);

        // Assert
        assertTrue(parentDir.exists(), "Le répertoire parent devrait exister");
        assertTrue(parentDir.isDirectory(), "Devrait être un répertoire");

        // Cleanup
        deleteDirectory(new File("data/test"));
    }

    @Test
    public void testEnsureDirectoryExistsForExistingDir() {
        // Arrange
        File existingDir = new File("data");
        assertTrue(existingDir.exists());

        // Act & Assert
        assertDoesNotThrow(() -> PersistenceService.ensureDirectoryExists("data/test.json"));
    }

    // ========== TESTS POUR SCORE AVEC LocalDateTime ==========

    @Test
    public void testSaveAndLoadScoreWithDate() throws IOException {
        // Arrange
        Score score = new Score("TestUser", 250, "Solo", "Pop", 10, 8, 7, 2);

        // Act
        PersistenceService.save(score, TEST_FILE);
        Score loadedScore = PersistenceService.load(TEST_FILE, Score.class);

        // Assert
        assertNotNull(loadedScore);
        assertEquals("TestUser", loadedScore.getPseudo());
        assertEquals(250, loadedScore.getScore());
        assertEquals("Solo", loadedScore.getMode());
        assertEquals("Pop", loadedScore.getGenre());
        assertEquals(10, loadedScore.getTotalTracks());
        assertEquals(8, loadedScore.getCorrectTitles());
        assertEquals(7, loadedScore.getCorrectArtists());
        assertEquals(2, loadedScore.getHintsUsed());
        assertNotNull(loadedScore.getDate(), "La date ne devrait pas être nulle");
    }

    // ========== TESTS DE ROBUSTESSE ==========

    @Test
    public void testLoadCorruptedFile() throws IOException {
        // Arrange - Créer un fichier JSON corrompu
        File corruptedFile = new File(TEST_FILE);
        corruptedFile.getParentFile().mkdirs();
        java.nio.file.Files.write(
            corruptedFile.toPath(), 
            "{ invalid json content".getBytes()
        );

        // Act
        Score loadedScore = PersistenceService.load(TEST_FILE, Score.class);

        // Assert
        assertNull(loadedScore, "Devrait retourner null pour un fichier corrompu");
    }

    @Test
    public void testSaveAndLoadMultipleTypes() throws IOException {
        // Arrange
        Player player = new Player("MultiTest");
        player.addScore(100);

        // Act - Sauvegarder différents types dans différents fichiers
        PersistenceService.save(player, "data/test_player.json");
        
        List<Score> scores = new ArrayList<>();
        scores.add(new Score("User1", 50));
        PersistenceService.save(scores, "data/test_scores.json");

        // Assert - Charger et vérifier
        Player loadedPlayer = PersistenceService.load("data/test_player.json", Player.class);
        List<Score> loadedScores = PersistenceService.loadList(
            "data/test_scores.json", 
            new TypeToken<List<Score>>(){}
        );

        assertNotNull(loadedPlayer);
        assertEquals("MultiTest", loadedPlayer.getName());
        assertNotNull(loadedScores);
        assertEquals(1, loadedScores.size());

        // Cleanup
        new File("data/test_player.json").delete();
        new File("data/test_scores.json").delete();
    }

    // ========== HELPER METHODS ==========

    /**
     * Supprime récursivement un répertoire et son contenu.
     */
    private void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
            directory.delete();
        }
    }
}