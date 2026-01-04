import com.blindtest.model.Score;
import com.blindtest.service.ScoreService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires complets pour ScoreService.
 * Couvre : sauvegarde, chargement, leaderboard, filtres, statistiques, distribution.
 */
public class ScoreServiceTest {
    private static final String SCORES_FILE = "data/scores.json";

    /**
     * Nettoie le fichier de scores avant et après chaque test.
     */
    @BeforeEach
    @AfterEach
    public void cleanUp() {
        // Reset le fichier de scores
        File file = new File(SCORES_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    // ========== TESTS DE BASE (SAVE/LOAD) ==========

    /**
     * Teste la sauvegarde et le chargement de scores.
     */
    @Test
    public void testSaveAndLoadScores() {
        Score score1 = new Score("Alice", 150);
        Score score2 = new Score("Bob", 200);
        
        ScoreService.saveScore(score1);
        ScoreService.saveScore(score2);
        
        List<Score> scores = ScoreService.loadScores();
        assertEquals(2, scores.size());
        assertEquals("Alice", scores.get(0).getPseudo());
        assertEquals(150, scores.get(0).getScore());
        assertEquals("Bob", scores.get(1).getPseudo());
        assertEquals(200, scores.get(1).getScore());
    }

    /**
     * Teste le chargement d'un fichier de scores vide.
     */
    @Test
    public void testLoadScoresEmptyFile() {
        List<Score> scores = ScoreService.loadScores();
        assertNotNull(scores);
        assertTrue(scores.isEmpty());
    }

    // ========== TESTS LEADERBOARD ==========

    /**
     * Teste le tri du leaderboard par score décroissant puis par date.
     */
    @Test
    public void testGetLeaderboard() {
        Score score1 = new Score("Alice", 150);
        score1.setDate(LocalDateTime.of(2023, 10, 1, 10, 0));
        
        Score score2 = new Score("Bob", 200);
        score2.setDate(LocalDateTime.of(2023, 10, 2, 10, 0));
        
        Score score3 = new Score("Charlie", 200);
        score3.setDate(LocalDateTime.of(2023, 10, 1, 11, 0));
        
        ScoreService.saveScore(score1);
        ScoreService.saveScore(score2);
        ScoreService.saveScore(score3);
        
        List<Score> leaderboard = ScoreService.getLeaderboard();
        assertEquals(3, leaderboard.size());
        
        // Devrait être trié par score décroissant, puis date décroissante
        assertEquals("Bob", leaderboard.get(0).getPseudo()); // 200, date plus récente
        assertEquals("Charlie", leaderboard.get(1).getPseudo()); // 200, date plus ancienne
        assertEquals("Alice", leaderboard.get(2).getPseudo()); // 150
    }


    /**
     * Teste la limitation du nombre de résultats du leaderboard.
     */
    @Test
    public void testGetLeaderboardWithLimit() {
        // Ajouter 15 scores
        for (int i = 0; i < 15; i++) {
            ScoreService.saveScore(new Score("Player" + i, 100 + i));
        }
        
        List<Score> leaderboard = ScoreService.getLeaderboard(10);
        assertEquals(10, leaderboard.size());
        
        // Vérifier que ce sont les 10 meilleurs
        assertEquals(114, leaderboard.get(0).getScore()); // 100 + 14
    }

    // ========== TESTS FILTRES PAR MODE ==========

    /**
     * Teste le filtrage du leaderboard par mode de jeu.
     */
    @Test
    public void testGetLeaderboardByMode() {
        Score solo1 = new Score("Alice", 100, "Solo", "Pop", 10, 8, 7, 0);
        Score solo2 = new Score("Bob", 150, "Solo", "Rock", 10, 9, 8, 0);
        Score duel1 = new Score("Charlie", 120, "Duel", "Pop", 10, 7, 6, 0);
        Score duel2 = new Score("David", 180, "Duel", "Rock", 10, 9, 9, 0);
        
        ScoreService.saveScore(solo1);
        ScoreService.saveScore(solo2);
        ScoreService.saveScore(duel1);
        ScoreService.saveScore(duel2);
        
        List<Score> soloLeaderboard = ScoreService.getLeaderboardByMode("Solo", 10);
        assertEquals(2, soloLeaderboard.size());
        assertEquals("Bob", soloLeaderboard.get(0).getPseudo()); // Meilleur score solo
        assertEquals(150, soloLeaderboard.get(0).getScore());
        
        List<Score> duelLeaderboard = ScoreService.getLeaderboardByMode("Duel", 10);
        assertEquals(2, duelLeaderboard.size());
        assertEquals("David", duelLeaderboard.get(0).getPseudo()); // Meilleur score duel
        assertEquals(180, duelLeaderboard.get(0).getScore());
    }

    /**
     * Teste que le filtrage par mode ignore la casse.
     */
    @Test
    public void testGetLeaderboardByModeIgnoresCase() {
        Score score = new Score("Alice", 100, "Solo", "Pop", 10, 8, 7, 0);
        ScoreService.saveScore(score);
        
        List<Score> leaderboard = ScoreService.getLeaderboardByMode("solo", 10);
        assertEquals(1, leaderboard.size());
        
        leaderboard = ScoreService.getLeaderboardByMode("SOLO", 10);
        assertEquals(1, leaderboard.size());
    }

    // ========== TESTS FILTRES PAR GENRE ==========

    /**
     * Teste le filtrage du leaderboard par genre musical.
     */
    @Test
    public void testGetLeaderboardByGenre() {
        Score pop1 = new Score("Alice", 100, "Solo", "Pop", 10, 8, 7, 0);
        Score pop2 = new Score("Bob", 150, "Solo", "Pop", 10, 9, 8, 0);
        Score rock1 = new Score("Charlie", 120, "Duel", "Rock", 10, 7, 6, 0);
        
        ScoreService.saveScore(pop1);
        ScoreService.saveScore(pop2);
        ScoreService.saveScore(rock1);
        
        List<Score> popLeaderboard = ScoreService.getLeaderboardByGenre("Pop", 10);
        assertEquals(2, popLeaderboard.size());
        assertEquals("Bob", popLeaderboard.get(0).getPseudo());
        
        List<Score> rockLeaderboard = ScoreService.getLeaderboardByGenre("Rock", 10);
        assertEquals(1, rockLeaderboard.size());
        assertEquals("Charlie", rockLeaderboard.get(0).getPseudo());
    }

    // ========== TESTS FILTRES PAR MODE ET GENRE ==========

    /**
     * Teste le filtrage du leaderboard par mode et genre combinés.
     */
    @Test
    public void testGetLeaderboardByModeAndGenre() {
        Score soloPop = new Score("Alice", 100, "Solo", "Pop", 10, 8, 7, 0);
        Score soloRock = new Score("Bob", 150, "Solo", "Rock", 10, 9, 8, 0);
        Score duelPop = new Score("Charlie", 120, "Duel", "Pop", 10, 7, 6, 0);
        Score duelRock = new Score("David", 180, "Duel", "Rock", 10, 9, 9, 0);
        
        ScoreService.saveScore(soloPop);
        ScoreService.saveScore(soloRock);
        ScoreService.saveScore(duelPop);
        ScoreService.saveScore(duelRock);
        
        List<Score> soloPops = ScoreService.getLeaderboardByModeAndGenre("Solo", "Pop", 10);
        assertEquals(1, soloPops.size());
        assertEquals("Alice", soloPops.get(0).getPseudo());
        
        List<Score> duelRocks = ScoreService.getLeaderboardByModeAndGenre("Duel", "Rock", 10);
        assertEquals(1, duelRocks.size());
        assertEquals("David", duelRocks.get(0).getPseudo());
    }

    // ========== TESTS STATISTIQUES GLOBALES ==========


    /**
     * Teste le calcul des statistiques globales de tous les scores.
     */
    @Test
    public void testGetGlobalStatistics() {
        Score score1 = new Score("Alice", 100, "Solo", "Pop", 10, 8, 7, 2);
        Score score2 = new Score("Bob", 200, "Solo", "Rock", 10, 9, 9, 1);
        Score score3 = new Score("Charlie", 150, "Duel", "Pop", 10, 7, 8, 3);
        
        ScoreService.saveScore(score1);
        ScoreService.saveScore(score2);
        ScoreService.saveScore(score3);
        
        ScoreService.ScoreStatistics stats = ScoreService.getGlobalStatistics();
        
        assertEquals(3, stats.getTotalGames());
        assertEquals(200, stats.getMaxScore());
        assertEquals(100, stats.getMinScore());
        assertEquals(150.0, stats.getAverageScore(), 0.1);
        
        // Vérifier les taux de réussite
        assertTrue(stats.getAvgSuccessRate() > 0);
        assertTrue(stats.getAvgTitleSuccessRate() > 0);
        assertTrue(stats.getAvgArtistSuccessRate() > 0);
    }

    /**
     * Teste les statistiques globales quand aucun score n'existe.
     */
    @Test
    public void testGetGlobalStatisticsEmpty() {
        ScoreService.ScoreStatistics stats = ScoreService.getGlobalStatistics();
        
        assertEquals(0, stats.getTotalGames());
        assertEquals(0, stats.getMaxScore());
        assertEquals(0, stats.getMinScore());
        assertEquals(0.0, stats.getAverageScore());
    }

    // ========== TESTS STATISTIQUES PAR MODE ==========

    /**
     * Teste le calcul des statistiques filtrées par mode de jeu.
     */
    @Test
    public void testGetStatisticsByMode() {
        Score solo1 = new Score("Alice", 100, "Solo", "Pop", 10, 8, 7, 0);
        Score solo2 = new Score("Bob", 150, "Solo", "Rock", 10, 9, 8, 0);
        Score duel1 = new Score("Charlie", 200, "Duel", "Pop", 10, 9, 9, 0);
        
        ScoreService.saveScore(solo1);
        ScoreService.saveScore(solo2);
        ScoreService.saveScore(duel1);
        
        ScoreService.ScoreStatistics soloStats = ScoreService.getStatisticsByMode("Solo");
        assertEquals(2, soloStats.getTotalGames());
        assertEquals(150, soloStats.getMaxScore());
        assertEquals(100, soloStats.getMinScore());
        assertEquals(125.0, soloStats.getAverageScore(), 0.1);
        
        ScoreService.ScoreStatistics duelStats = ScoreService.getStatisticsByMode("Duel");
        assertEquals(1, duelStats.getTotalGames());
        assertEquals(200, duelStats.getMaxScore());
    }

    // ========== TESTS STATISTIQUES PAR JOUEUR ==========

    /**
     * Teste le calcul des statistiques d'un joueur spécifique.
     */
    @Test
    public void testGetPlayerStatistics() {
        Score score1 = new Score("Alice", 100, "Solo", "Pop", 10, 8, 7, 0);
        Score score2 = new Score("Alice", 150, "Solo", "Rock", 10, 9, 8, 0);
        Score score3 = new Score("Bob", 200, "Duel", "Pop", 10, 9, 9, 0);
        
        ScoreService.saveScore(score1);
        ScoreService.saveScore(score2);
        ScoreService.saveScore(score3);
        
        ScoreService.ScoreStatistics aliceStats = ScoreService.getPlayerStatistics("Alice");
        assertEquals(2, aliceStats.getTotalGames());
        assertEquals(150, aliceStats.getMaxScore());
        assertEquals(100, aliceStats.getMinScore());
        assertEquals(125.0, aliceStats.getAverageScore(), 0.1);
        
        ScoreService.ScoreStatistics bobStats = ScoreService.getPlayerStatistics("Bob");
        assertEquals(1, bobStats.getTotalGames());
        assertEquals(200, bobStats.getMaxScore());
    }

    /**
     * Teste les statistiques d'un joueur inexistant.
     */
    @Test
    public void testGetPlayerStatisticsNonExistent() {
        ScoreService.ScoreStatistics stats = ScoreService.getPlayerStatistics("NonExistent");
        assertEquals(0, stats.getTotalGames());
    }

    // ========== TESTS DISTRIBUTION ==========

    /**
     * Teste la distribution des scores par mode de jeu.
     */
    @Test
    public void testGetScoreDistributionByMode() {
        ScoreService.saveScore(new Score("Alice", 100, "Solo", "Pop", 10, 8, 7, 0));
        ScoreService.saveScore(new Score("Bob", 150, "Solo", "Rock", 10, 9, 8, 0));
        ScoreService.saveScore(new Score("Charlie", 120, "Duel", "Pop", 10, 7, 6, 0));
        ScoreService.saveScore(new Score("David", 180, "Duel", "Rock", 10, 9, 9, 0));
        ScoreService.saveScore(new Score("Eve", 200, "Duel", "Hip-Hop/Rap", 10, 10, 10, 0));
        
        Map<String, Long> distribution = ScoreService.getScoreDistributionByMode();
        
        assertEquals(2, distribution.size());
        assertEquals(2L, distribution.get("Solo"));
        assertEquals(3L, distribution.get("Duel"));
    }

    /**
     * Teste la distribution des scores par genre musical.
     */
    @Test
    public void testGetScoreDistributionByGenre() {
        ScoreService.saveScore(new Score("Alice", 100, "Solo", "Pop", 10, 8, 7, 0));
        ScoreService.saveScore(new Score("Bob", 150, "Solo", "Rock", 10, 9, 8, 0));
        ScoreService.saveScore(new Score("Charlie", 120, "Duel", "Pop", 10, 7, 6, 0));
        ScoreService.saveScore(new Score("David", 180, "Duel", "Rock", 10, 9, 9, 0));
        
        Map<String, Long> distribution = ScoreService.getScoreDistributionByGenre();
        
        assertEquals(2, distribution.size());
        assertEquals(2L, distribution.get("Pop"));
        assertEquals(2L, distribution.get("Rock"));
    }

    /**
     * Teste la distribution quand aucun score n'existe.
     */
    @Test
    public void testGetScoreDistributionEmpty() {
        Map<String, Long> modeDistribution = ScoreService.getScoreDistributionByMode();
        Map<String, Long> genreDistribution = ScoreService.getScoreDistributionByGenre();
        
        assertTrue(modeDistribution.isEmpty());
        assertTrue(genreDistribution.isEmpty());
    }

    // ========== TESTS HISTORIQUE JOUEUR ==========

    /**
     * Teste la récupération de l'historique d'un joueur trié par date.
     */
    @Test
    public void testGetPlayerHistory() {
        Score score1 = new Score("Alice", 150);
        score1.setDate(LocalDateTime.of(2023, 10, 1, 10, 0));
        
        Score score2 = new Score("Bob", 200);
        score2.setDate(LocalDateTime.of(2023, 10, 2, 10, 0));
        
        Score score3 = new Score("Alice", 100);
        score3.setDate(LocalDateTime.of(2023, 10, 3, 10, 0));
        
        ScoreService.saveScore(score1);
        ScoreService.saveScore(score2);
        ScoreService.saveScore(score3);
        
        List<Score> history = ScoreService.getPlayerHistory("Alice");
        assertEquals(2, history.size());
        
        // Devrait être trié par date décroissante
        assertEquals(100, history.get(0).getScore()); // Date plus récente
        assertEquals(150, history.get(1).getScore()); // Date plus ancienne
    }

    /**
     * Teste l'historique d'un joueur sans scores enregistrés.
     */
    @Test
    public void testGetPlayerHistoryNoMatches() {
        ScoreService.saveScore(new Score("Alice", 100));
        
        List<Score> history = ScoreService.getPlayerHistory("Bob");
        assertTrue(history.isEmpty());
    }

    // ========== TESTS SCORES ENRICHIS ==========

    /**
     * Teste la sauvegarde et le chargement d'un score avec statistiques détaillées.
     */
    @Test
    public void testScoreWithDetailedStatistics() {
        Score score = new Score("TestPlayer", 250, "Solo", "Pop", 10, 8, 7, 2);
        
        ScoreService.saveScore(score);
        List<Score> loaded = ScoreService.loadScores();
        
        Score loadedScore = loaded.get(0);
        assertEquals("TestPlayer", loadedScore.getPseudo());
        assertEquals(250, loadedScore.getScore());
        assertEquals("Solo", loadedScore.getMode());
        assertEquals("Pop", loadedScore.getGenre());
        assertEquals(10, loadedScore.getTotalTracks());
        assertEquals(8, loadedScore.getCorrectTitles());
        assertEquals(7, loadedScore.getCorrectArtists());
        assertEquals(2, loadedScore.getHintsUsed());
        
        // Vérifier les taux calculés
        assertEquals(75.0, loadedScore.getSuccessRate(), 0.1); // (8+7)/(10*2) = 75%
        assertEquals(80.0, loadedScore.getTitleSuccessRate(), 0.1); // 8/10 = 80%
        assertEquals(70.0, loadedScore.getArtistSuccessRate(), 0.1); // 7/10 = 70%
    }

    // ========== TESTS DE ROBUSTESSE ==========

    /**
     * Teste la sauvegarde d'un score avec un mode null.
     */
    @Test
    public void testSaveScoreWithNullMode() {
        Score score = new Score("Alice", 100);
        score.setMode(null);
        
        assertDoesNotThrow(() -> ScoreService.saveScore(score));
        
        List<Score> soloScores = ScoreService.getLeaderboardByMode("Solo", 10);
        assertTrue(soloScores.isEmpty());
    }

    /**
     * Teste les performances de sauvegarde de multiples scores.
     */
    @Test
    public void testMultipleSavesPerformance() {
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 100; i++) {
            ScoreService.saveScore(new Score("Player" + i, 100 + i, "Solo", "Pop", 10, 8, 7, 0));
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        List<Score> scores = ScoreService.loadScores();
        assertEquals(100, scores.size());
        
        // Vérifier que ça ne prend pas trop de temps (< 5 secondes pour 100 scores)
        assertTrue(duration < 5000, "Sauvegarde de 100 scores devrait prendre moins de 5 secondes");
    }
}