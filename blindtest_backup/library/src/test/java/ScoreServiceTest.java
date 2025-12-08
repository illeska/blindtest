import com.blindtest.model.Score;
import com.blindtest.service.ScoreService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ScoreServiceTest {

    private static final String SCORES_FILE = "data/scores.json";

    @BeforeEach
    @AfterEach
    public void cleanUp() {
        // Reset the scores file to empty array
        File file = new File(SCORES_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

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

    @Test
    public void testGetLeaderboard() {
        Score score1 = new Score("Alice", 150);
        score1.setDate(LocalDateTime.of(2023, 10, 1, 10, 0));
        Score score2 = new Score("Bob", 200);
        score2.setDate(LocalDateTime.of(2023, 10, 2, 10, 0));
        Score score3 = new Score("Charlie", 200);
        score3.setDate(LocalDateTime.of(2023, 10, 1, 11, 0)); // Same score as Bob, earlier date

        ScoreService.saveScore(score1);
        ScoreService.saveScore(score2);
        ScoreService.saveScore(score3);

        List<Score> leaderboard = ScoreService.getLeaderboard();
        assertEquals(3, leaderboard.size());
        // Should be sorted by score desc, then date desc
        assertEquals("Bob", leaderboard.get(0).getPseudo()); // 200, later date
        assertEquals("Charlie", leaderboard.get(1).getPseudo()); // 200, earlier date
        assertEquals("Alice", leaderboard.get(2).getPseudo()); // 150
    }

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
        // Should be sorted by date desc
        assertEquals(100, history.get(0).getScore()); // Later date
        assertEquals(150, history.get(1).getScore()); // Earlier date
    }
}
