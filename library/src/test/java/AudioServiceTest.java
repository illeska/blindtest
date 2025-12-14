import com.blindtest.service.AudioService;
import com.blindtest.service.SettingsService;
import com.blindtest.model.Settings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

public class AudioServiceTest {

    private AudioService audioService;

    @BeforeEach
    public void setUp() {
        audioService = new AudioService();
        audioService.clearCache(); // Nettoyage avant chaque test
    }

    @Test
    public void testFetchPreviewFromITunes_success() {
        URL url = audioService.fetchPreviewFromITunes("The Final Countdown Europe");
        assertNotNull(url, "L'URL ne doit pas être nulle pour une requête valide");
        assertTrue(url.toString().contains("http"), "L'URL doit être valide");
    }

    @Test
    public void testCacheBehavior() {
        String query = "Bohemian Rhapsody Queen";

        // Premier appel (Doit déclencher le réseau)
        long start1 = System.currentTimeMillis();
        URL url1 = audioService.fetchPreviewFromITunes(query);
        long time1 = System.currentTimeMillis() - start1;

        assertNotNull(url1, "La première requête doit réussir");

        // Deuxième appel (Doit utiliser le cache)
        long start2 = System.currentTimeMillis();
        URL url2 = audioService.fetchPreviewFromITunes(query);
        long time2 = System.currentTimeMillis() - start2;

        assertNotNull(url2, "La requête cachée doit réussir");
        assertEquals(url1.toString(), url2.toString(), "Les URLs doivent être identiques");
        
        System.out.println("Temps Réseau : " + time1 + "ms | Temps Cache : " + time2 + "ms");
        
        // On vérifie simplement que le deuxième appel n'est pas anormalement long
        // (Note: sur certains réseaux rapides, la diff peut être faible, mais le cache est instantané)
        assertTrue(time2 < time1 || time2 < 50, "Le cache devrait être rapide");
    }

    @Test
    public void testRetryLogicWithInvalidQuery() {
        // Une requête invalide/vide ne doit pas planter mais retourner null proprement après les tentatives
        URL url = audioService.fetchPreviewFromITunes("dhfksjdhfksjdhfksjdfhksjdfh");
        assertNull(url, "Doit retourner null si aucun résultat n'est trouvé");
    }

    @Test
    public void testSoundEffectsDoNotCrash() {
        // Vérifie que l'appel aux méthodes de son est sûr même sans interface graphique ou fichiers
        assertDoesNotThrow(() -> audioService.playCorrectSound());
        assertDoesNotThrow(() -> audioService.playWrongSound());
        assertDoesNotThrow(() -> audioService.playRoundEndSound());
    }

    @Test
    public void testLoadLocalFallback() {
        boolean result = audioService.loadLocalFallback();
        // Vérifie juste l'absence d'exception
        assertDoesNotThrow(() -> audioService.loadLocalFallback());
    }
}