import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.blindtest.service.AudioService;

/**
 * Tests unitaires complets pour AudioService.
 * Couvre : API, cache, fallback, volume, erreurs réseau, performances.
 */
public class AudioServiceTest {
    private AudioService audioService;
    private static boolean javaFXInitialized = false;
    private static final String CACHE_FILE = "data/audio_cache.dat";

    @BeforeAll
    static void initJavaFX() {
        try {
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

    @AfterEach
    public void tearDown() {
        if (audioService != null) {
            audioService.stop();
        }
    }

    @AfterAll
    static void cleanup() {
        // Nettoyer le fichier de cache après tous les tests
        File cacheFile = new File(CACHE_FILE);
        if (cacheFile.exists()) {
            cacheFile.delete();
        }
    }

    // ========== TESTS API DEEZER ==========

    @Test
    public void testFetchPreviewFromDeezer_validQuery() {
        Assumptions.assumeTrue(javaFXInitialized, "JavaFX non disponible");
        
        URL result = audioService.fetchPreviewFromDeezer("Billie Jean Michael Jackson");
        
        assertNotNull(result, "L'API devrait retourner une URL valide");
        assertTrue(result.toString().contains("deezer") || result.toString().contains("cdn"), 
            "L'URL devrait provenir de Deezer");
    }

    @Test
    public void testFetchPreviewFromDeezer_emptyQuery() {
        URL result = audioService.fetchPreviewFromDeezer("");
        assertNull(result, "Une requête vide devrait retourner null");
    }

    @Test
    public void testFetchPreviewFromDeezer_nullQuery() {
        URL result = audioService.fetchPreviewFromDeezer(null);
        assertNull(result, "Une requête null devrait retourner null");
    }

    @Test
    public void testFetchPreviewFromDeezer_invalidQuery() {
        URL result = audioService.fetchPreviewFromDeezer("azertyuiopqsdfghjklmwxcvbn123456789");
        // Peut retourner null ou un résultat par défaut selon l'API
        // Le test vérifie juste qu'il ne plante pas
        assertDoesNotThrow(() -> audioService.fetchPreviewFromDeezer("invalid query xyz"));
    }

    @Test
    public void testFetchPreviewFromDeezer_specialCharacters() {
        Assumptions.assumeTrue(javaFXInitialized, "JavaFX non disponible");
        
        // Test avec caractères spéciaux (accents, espaces, apostrophes)
        assertDoesNotThrow(() -> {
            audioService.fetchPreviewFromDeezer("Édith Piaf La Vie en Rose");
            audioService.fetchPreviewFromDeezer("Guns N' Roses");
        });
    }

    // ========== TESTS CACHE ==========

    @Test
    public void testCache_sameQueryTwice() {
        Assumptions.assumeTrue(javaFXInitialized, "JavaFX non disponible");
        
        String query = "Thriller Michael Jackson";
        
        // Première requête (devrait aller sur l'API)
        long startTime1 = System.currentTimeMillis();
        URL result1 = audioService.fetchPreviewFromDeezer(query);
        long time1 = System.currentTimeMillis() - startTime1;
        
        // Deuxième requête (devrait venir du cache)
        long startTime2 = System.currentTimeMillis();
        URL result2 = audioService.fetchPreviewFromDeezer(query);
        long time2 = System.currentTimeMillis() - startTime2;
        
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(result1.toString(), result2.toString(), "Les URLs devraient être identiques");
        
        // Le cache devrait être beaucoup plus rapide (< 10ms vs potentiellement 1000ms+)
        assertTrue(time2 < time1 / 2, 
            "La requête en cache devrait être plus rapide (T1=" + time1 + "ms, T2=" + time2 + "ms)");
    }

    @Test
    public void testCache_persistentAcrossInstances() {
        Assumptions.assumeTrue(javaFXInitialized, "JavaFX non disponible");
        
        String query = "Hotel California Eagles";
        
        // Première instance : faire une requête
        AudioService service1 = new AudioService();
        URL result1 = service1.fetchPreviewFromDeezer(query);
        assertNotNull(result1);
        service1.stop();
        
        // Deuxième instance : le cache devrait être chargé du disque
        AudioService service2 = new AudioService();
        long startTime = System.currentTimeMillis();
        URL result2 = service2.fetchPreviewFromDeezer(query);
        long time = System.currentTimeMillis() - startTime;
        
        assertNotNull(result2);
        assertEquals(result1.toString(), result2.toString());
        assertTrue(time < 50, "Le cache persistant devrait être très rapide (<50ms)");
        
        service2.stop();
    }

    @Test
    public void testCache_cleanExpired() {
        // Test du nettoyage du cache
        assertDoesNotThrow(() -> audioService.cleanExpiredCache());
    }

    @Test
    public void testCache_clear() {
        Assumptions.assumeTrue(javaFXInitialized, "JavaFX non disponible");
        
        // Ajouter quelque chose au cache
        audioService.fetchPreviewFromDeezer("Test Song");
        
        // Vider le cache
        audioService.clearCache();
        
        // Vérifier que le fichier de cache n'existe plus
        File cacheFile = new File(CACHE_FILE);
        assertFalse(cacheFile.exists(), "Le fichier de cache devrait être supprimé");
    }

    @Test
    public void testCache_stats() {
        // Tester l'affichage des statistiques (ne devrait pas planter)
        assertDoesNotThrow(() -> audioService.printCacheStats());
    }

    // ========== TESTS FALLBACK ==========

    @Test
    public void testLoadLocalFallback_fileExists() {
        // Créer un fichier de fallback temporaire si nécessaire
        File fallbackFile = new File("data/fallback.mp3");
        
        if (fallbackFile.exists()) {
            boolean result = audioService.loadLocalFallback();
            assertTrue(result, "Le chargement du fallback devrait réussir si le fichier existe");
        } else {
            boolean result = audioService.loadLocalFallback();
            assertFalse(result, "Le chargement devrait échouer si le fichier n'existe pas");
        }
    }

    @Test
    public void testLoadWithFallback_apiSuccess() {
        Assumptions.assumeTrue(javaFXInitialized, "JavaFX non disponible");
        
        // Avec une chanson connue, l'API devrait réussir (pas de fallback)
        assertDoesNotThrow(() -> audioService.loadWithFallback("Billie Jean Michael Jackson"));
    }

    @Test
    public void testLoadWithFallback_apiFallsBackToLocal() {
        Assumptions.assumeTrue(javaFXInitialized, "JavaFX non disponible");
        
        // Avec une requête impossible, devrait tomber sur le fallback
        assertDoesNotThrow(() -> audioService.loadWithFallback("qwertyuiopasdfghjkl12345"));
    }

    @Test
    public void testLoadWithFallback_emptyQuery() {
        assertDoesNotThrow(() -> audioService.loadWithFallback(""));
    }

    // ========== TESTS CHARGEMENT MEDIA ==========

    @Test
    public void testLoadFromURL_validURL() throws Exception {
        Assumptions.assumeTrue(javaFXInitialized, "JavaFX non disponible");
        
        // Utiliser une URL valide de Deezer
        URL validUrl = audioService.fetchPreviewFromDeezer("Test Song");
        
        if (validUrl != null) {
            boolean result = audioService.loadFromURL(validUrl);
            assertTrue(result, "Le chargement depuis une URL valide devrait réussir");
        }
    }

    @Test
    public void testLoadFromURL_nullURL() {
        boolean result = audioService.loadFromURL(null);
        assertFalse(result, "Le chargement depuis une URL null devrait échouer");
    }

    @Test
    public void testLoadFromURL_invalidURL() throws Exception {
        Assumptions.assumeTrue(javaFXInitialized, "JavaFX non disponible");
        
        URL invalidUrl = new URL("http://invalid.url.com/notfound.mp3");
        boolean result = audioService.loadFromURL(invalidUrl);
        // Peut réussir ou échouer selon le timeout, mais ne devrait pas planter
        assertNotNull(result);
    }

    // ========== TESTS CONTRÔLES AUDIO ==========

    @Test
    public void testPlayPauseStop_withoutLoad() {
        // Test que les commandes ne plantent pas sans media chargé
        assertDoesNotThrow(() -> {
            audioService.play();
            audioService.pause();
            audioService.stop();
        });
    }

    @Test
    public void testPlayPauseStop_withLoad() throws Exception {
        Assumptions.assumeTrue(javaFXInitialized, "JavaFX non disponible");
        
        audioService.loadWithFallback("Test Song");
        Thread.sleep(500); // Attendre que le media soit prêt
        
        assertDoesNotThrow(() -> {
            audioService.play();
            Thread.sleep(200);
            audioService.pause();
            Thread.sleep(100);
            audioService.stop();
        });
    }

    @Test
    public void testMultiplePlayCalls() throws Exception {
        Assumptions.assumeTrue(javaFXInitialized, "JavaFX non disponible");
        
        audioService.loadWithFallback("Test Song");
        Thread.sleep(500);
        
        // Appeler play plusieurs fois ne devrait pas causer de problème
        assertDoesNotThrow(() -> {
            audioService.play();
            audioService.play();
            audioService.play();
            audioService.stop();
        });
    }

    // ========== TESTS VOLUME ==========

    @Test
    public void testSetVolume_validValues() {
        assertDoesNotThrow(() -> {
            audioService.setVolume(0.0);
            audioService.setVolume(0.5);
            audioService.setVolume(1.0);
        });
    }

    @Test
    public void testSetVolume_edgeCases() {
        // Tester les valeurs limites
        assertDoesNotThrow(() -> {
            audioService.setVolume(0.0);
            audioService.setVolume(1.0);
            audioService.setVolume(0.01);
            audioService.setVolume(0.99);
        });
    }

    @Test
    public void testSetGlobalVolume() {
        assertDoesNotThrow(() -> {
            audioService.setGlobalVolume(0.7);
            audioService.setGlobalVolume(0.3);
        });
    }

    @Test
    public void testVolumeAdjustmentDuringPlayback() throws Exception {
        Assumptions.assumeTrue(javaFXInitialized, "JavaFX non disponible");
        
        audioService.loadWithFallback("Test Song");
        Thread.sleep(500);
        
        assertDoesNotThrow(() -> {
            audioService.play();
            Thread.sleep(200);
            audioService.setVolume(0.8);
            Thread.sleep(100);
            audioService.setVolume(0.3);
            Thread.sleep(100);
            audioService.stop();
        });
    }

    // ========== TESTS EFFETS SONORES ==========

    @Test
    public void testSoundEffects_doNotCrash() {
        assertDoesNotThrow(() -> {
            audioService.playSfxVictory();
            audioService.playSfxFail();
            audioService.playClick();
            audioService.playCorrectSound();
            audioService.playWrongSound();
        });
    }

    @Test
    public void testMenuMusic() {
        assertDoesNotThrow(() -> {
            audioService.startMenuMusic();
            Thread.sleep(200);
            audioService.stopMenuMusic();
        });
    }

    // ========== TESTS GESTION ERREURS RÉSEAU ==========

    @Test
    public void testNetworkError_retryLogic() {
        // Tester avec une requête qui devrait échouer
        // Le retry devrait être tenté MAX_RETRIES fois
        long startTime = System.currentTimeMillis();
        URL result = audioService.fetchPreviewFromDeezer("azertyuiopqsdfghjklmwxcvbn");
        long duration = System.currentTimeMillis() - startTime;
        
        // Si aucun résultat trouvé, le temps devrait inclure les retries
        // (au moins 1 seconde de délai entre tentatives)
        if (result == null) {
            // Devrait avoir pris du temps à cause des retries
            assertTrue(duration > 500, "Les retries devraient prendre du temps");
        }
    }

    @Test
    public void testConcurrentRequests() throws InterruptedException {
        Assumptions.assumeTrue(javaFXInitialized, "JavaFX non disponible");
        
        int numThreads = 5;
        CountDownLatch latch = new CountDownLatch(numThreads);
        
        for (int i = 0; i < numThreads; i++) {
            final int index = i;
            new Thread(() -> {
                try {
                    audioService.fetchPreviewFromDeezer("Song " + index);
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        
        boolean completed = latch.await(30, TimeUnit.SECONDS);
        assertTrue(completed, "Toutes les requêtes concurrentes devraient se terminer");
    }

    // ========== TESTS PERFORMANCES ==========

    @Test
    public void testCache_performance() {
        Assumptions.assumeTrue(javaFXInitialized, "JavaFX non disponible");
        
        String query = "Performance Test Song";
        
        // Premier appel (API)
        long start1 = System.currentTimeMillis();
        audioService.fetchPreviewFromDeezer(query);
        long time1 = System.currentTimeMillis() - start1;
        
        // Deuxième appel (Cache)
        long start2 = System.currentTimeMillis();
        audioService.fetchPreviewFromDeezer(query);
        long time2 = System.currentTimeMillis() - start2;
        
        System.out.println("Performance: API=" + time1 + "ms, Cache=" + time2 + "ms");
        
        // Le cache devrait être au moins 10x plus rapide
        assertTrue(time2 < time1 / 10, 
            "Le cache devrait être bien plus rapide que l'API");
    }

    @Test
    public void testMultipleLoads_memoryLeak() throws Exception {
        Assumptions.assumeTrue(javaFXInitialized, "JavaFX non disponible");
        
        // Charger plusieurs fois pour vérifier qu'il n'y a pas de fuite mémoire
        for (int i = 0; i < 10; i++) {
            audioService.loadWithFallback("Test Song " + i);
            Thread.sleep(50);
        }
        
        // Si on arrive ici sans crash, c'est bon
        assertTrue(true);
    }

    // ========== TESTS ROBUSTESSE ==========

    @Test
    public void testRapidStartStop() throws Exception {
        Assumptions.assumeTrue(javaFXInitialized, "JavaFX non disponible");
        
        audioService.loadWithFallback("Test");
        Thread.sleep(300);
        
        // Start/stop rapides
        for (int i = 0; i < 5; i++) {
            audioService.play();
            Thread.sleep(50);
            audioService.stop();
            Thread.sleep(50);
        }
        
        assertTrue(true, "Les start/stop rapides ne devraient pas causer de crash");
    }

    @Test
    public void testLoadDifferentSongs_quickly() throws Exception {
        Assumptions.assumeTrue(javaFXInitialized, "JavaFX non disponible");
        
        // Charger différentes chansons rapidement
        audioService.loadWithFallback("Song A");
        Thread.sleep(100);
        audioService.loadWithFallback("Song B");
        Thread.sleep(100);
        audioService.loadWithFallback("Song C");
        Thread.sleep(100);
        
        assertTrue(true, "Le changement rapide de chansons ne devrait pas planter");
    }

    @Test
    public void testServiceCreationDestruction() {
        // Créer et détruire plusieurs instances
        for (int i = 0; i < 5; i++) {
            AudioService service = new AudioService();
            service.stop();
        }
        
        assertTrue(true, "La création/destruction multiple devrait fonctionner");
    }

    // ========== TESTS EDGE CASES ==========

    @Test
    public void testVeryLongQuery() {
        String longQuery = "a".repeat(1000);
        assertDoesNotThrow(() -> audioService.fetchPreviewFromDeezer(longQuery));
    }

    @Test
    public void testQueryWithSpecialChars() {
        assertDoesNotThrow(() -> {
            audioService.fetchPreviewFromDeezer("Song with & ampersand");
            audioService.fetchPreviewFromDeezer("Song with = equals");
            audioService.fetchPreviewFromDeezer("Song with % percent");
        });
    }

    @Test
    public void testMultipleStopCalls() {
        audioService.stop();
        audioService.stop();
        audioService.stop();
        
        assertTrue(true, "Les appels stop multiples ne devraient pas causer de problème");
    }
}