import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.blindtest.model.Settings;
import com.blindtest.service.AudioService;
import com.blindtest.service.SettingsService;

public class AudioServiceTest {

    private AudioService audioService;

    @BeforeEach
    public void setUp() {
        audioService = new AudioService();
    }

    @Test
    public void testFetchPreviewFromITunes_success() {
        URL url = audioService.fetchPreviewFromITunes("The Final Countdown Europe");
        assertNotNull(url, "L'URL ne doit pas être nulle pour une requête valide");
        assertTrue(url.toString().contains("http"), "L'URL doit être valide");
    }

    @Test
    public void testFetchPreviewFromITunes_invalidQuery() {
        URL url = audioService.fetchPreviewFromITunes("hjsdfkhskjdhfkjsdhfksjdhf");
        assertNull(url, "L'URL doit être nulle si aucun résultat n'est trouvé");
    }

    @Test
    public void testLoadFromURL_valid() {
        URL url = audioService.fetchPreviewFromITunes("Thriller Michael Jackson");
        if (url != null) {
            assertDoesNotThrow(() -> audioService.loadFromURL(url));
        }
    }

    @Test
    public void testLoadLocalFallback() {
        boolean result = audioService.loadLocalFallback();
        // Le résultat dépend de la présence du fichier "data/fallback.mp3"
        // On vérifie juste qu'il n'y a pas d'exception
        assertDoesNotThrow(() -> audioService.loadLocalFallback());
    }
    
    @Test
    public void testFetchPreviewWithGenreInfluence() {
        Settings s = new Settings();
        s.setDefaultGenre("Rock");
        SettingsService.saveSettings(s);
        
        AudioService serviceWithGenre = new AudioService();
        
        URL url = serviceWithGenre.fetchPreviewFromITunes("Numb Linkin Park");
        assertNotNull(url, "Devrait trouver un résultat avec le genre Rock");
        
        // Restauration
        s.setDefaultGenre("pop");
        SettingsService.saveSettings(s);
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
}