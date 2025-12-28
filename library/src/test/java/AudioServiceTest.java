import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.blindtest.service.AudioService;

/**
 * Tests unitaires pour AudioService.
 * Tests adaptés aux méthodes publiques disponibles.
 */
public class AudioServiceTest {
    private AudioService audioService;
    private static boolean javaFXInitialized = false;

    @BeforeAll
    static void initJavaFX() {
        try {
            // Initialisation de JavaFX pour les tests
            javafx.embed.swing.JFXPanel panel = new javafx.embed.swing.JFXPanel();
            javaFXInitialized = true;
            System.out.println("✓ JavaFX initialisé pour les tests AudioService");
        } catch (Exception e) {
            System.err.println("✗ JavaFX ne peut pas être initialisé: " + e.getMessage());
            javaFXInitialized = false;
        }
    }

    @BeforeEach
    public void setUp() {
        audioService = new AudioService();
    }

    @Test
    public void testLoadWithFallback_success() {
        // Test que la méthode loadWithFallback ne plante pas
        assertDoesNotThrow(() -> audioService.loadWithFallback("Billie Jean Michael Jackson"));
    }

    @Test
    public void testLoadWithFallback_fallbackActivation() {
        // Test avec une requête qui devrait échouer et activer le fallback
        assertDoesNotThrow(() -> audioService.loadWithFallback("azertyuiopqsdfghjklm"));
    }

    @Test
    public void testLoadWithFallback_validQuery() {
        // Test avec une requête valide
        assertDoesNotThrow(() -> audioService.loadWithFallback("The Final Countdown Europe"));
    }

    @Test
    public void testLoadWithFallback_emptyQuery() {
        // Test avec une requête vide
        assertDoesNotThrow(() -> audioService.loadWithFallback(""));
    }

    @Test
    public void testLoadLocalFallback() {
        // Le résultat dépend de la présence du fichier "data/fallback.mp3"
        // On vérifie juste qu'il n'y a pas d'exception
        assertDoesNotThrow(() -> audioService.loadLocalFallback());
    }

    @Test
    public void testPlayPauseStopCommands() {
        // Test que les commandes de base ne plantent pas
        assertDoesNotThrow(() -> audioService.play());
        assertDoesNotThrow(() -> audioService.pause());
        assertDoesNotThrow(() -> audioService.stop());
    }

    @Test
    public void testSetVolume() {
        // Test que le volume peut être défini sans erreur
        assertDoesNotThrow(() -> audioService.setVolume(0.5));
        assertDoesNotThrow(() -> audioService.setVolume(0.0));
        assertDoesNotThrow(() -> audioService.setVolume(1.0));
    }

    @Test
    public void testSetVolumeEdgeCases() {
        // Test des valeurs limites
        assertDoesNotThrow(() -> audioService.setVolume(0.0));
        assertDoesNotThrow(() -> audioService.setVolume(1.0));
    }

    @Test
    public void testPlayWithoutLoad() {
        // Test que play ne plante pas même si aucun fichier n'est chargé
        assertDoesNotThrow(() -> audioService.play());
    }

    @Test
    public void testPauseWithoutPlay() {
        // Test que pause ne plante pas même si aucun son n'est en cours
        assertDoesNotThrow(() -> audioService.pause());
    }

    @Test
    public void testStopWithoutPlay() {
        // Test que stop ne plante pas même si aucun son n'est en cours
        assertDoesNotThrow(() -> audioService.stop());
    }

    @Test
    public void testMultipleLoadWithFallback() {
        // Test plusieurs appels successifs
        assertDoesNotThrow(() -> {
            audioService.loadWithFallback("Song 1");
            audioService.loadWithFallback("Song 2");
            audioService.loadWithFallback("Song 3");
        });
    }

    @Test
    public void testLoadPlayStop() {
        // Skip si JavaFX n'est pas disponible
        Assumptions.assumeTrue(javaFXInitialized, "JavaFX n'est pas disponible");
        
        // Test d'un cycle complet
        assertDoesNotThrow(() -> {
            audioService.loadWithFallback("Thriller Michael Jackson");
            // Petite pause pour laisser JavaFX s'initialiser
            Thread.sleep(500);
            audioService.play();
            Thread.sleep(200);
            audioService.stop();
        });
    }

    @Test
    public void testVolumeAdjustmentDuringPlayback() {
        // Skip si JavaFX n'est pas disponible
        Assumptions.assumeTrue(javaFXInitialized, "JavaFX n'est pas disponible");
        
        // Test changement de volume pendant la lecture
        assertDoesNotThrow(() -> {
            audioService.loadWithFallback("Test Song");
            Thread.sleep(500);
            audioService.play();
            Thread.sleep(200);
            audioService.setVolume(0.7);
            audioService.setVolume(0.3);
            audioService.stop();
        });
    }

    @Test
    public void testGetAudioService() {
        // Test que l'instance est bien créée
        assertNotNull(audioService);
    }
}