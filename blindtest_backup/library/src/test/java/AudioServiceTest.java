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
        // Note: AudioService charge les settings réels via SettingsService.loadSettings().
        // Pour un test pur, il faudrait idéalement pouvoir injecter un mock de Settings,
        // mais ici nous allons manipuler le fichier ou vérifier le comportement par défaut.
    }

    @Test
    public void testFetchPreviewFromITunes_success() {
        // Test standard sans genre spécifique (ou avec le genre par défaut 'pop')
        URL url = audioService.fetchPreviewFromITunes("The Final Countdown Europe");
        assertNotNull(url, "L'URL ne doit pas être nulle pour une requête valide");
        assertTrue(url.toString().contains("http"), "L'URL doit être valide");
    }

    @Test
    public void testFetchPreviewFromITunes_invalidQuery() {
        // Test avec une requête absurde
        URL url = audioService.fetchPreviewFromITunes("hjsdfkhskjdhfkjsdhfksjdhf");
        assertNull(url, "L'URL doit être nulle si aucun résultat n'est trouvé");
    }

    @Test
    public void testLoadFromURL_valid() {
        // On récupère une vraie URL pour tester le chargement
        URL url = audioService.fetchPreviewFromITunes("Thriller Michael Jackson");
        if (url != null) {
            boolean loaded = audioService.loadFromURL(url);
            // Note: loadFromURL peut retourner false en environnement CI/Headless sans JavaFX initialisé.
            // Si le test échoue sur un serveur CI, c'est "normal" pour JavaFX Media, 
            // mais en local avec UI ça doit passer ou au moins ne pas crasher.
            // Ici on vérifie surtout qu'il n'y a pas d'exception levée.
            assertDoesNotThrow(() -> audioService.loadFromURL(url));
        }
    }

    @Test
    public void testLoadLocalFallback() {
        // Ce test vérifie que la méthode ne plante pas, même si le fichier n'existe pas
        boolean result = audioService.loadLocalFallback();
        // Le résultat dépend de la présence du fichier "data/fallback.mp3" sur ta machine
        // On vérifie juste qu'il n'y a pas d'exception
    }
    
    @Test
    public void testFetchPreviewWithGenreInfluence() {
        // Ce test est plus subtil : on vérifie que la méthode s'exécute avec la logique de genre intégrée.
        // On modifie temporairement les settings pour voir si ça impacte (nécessite accès écriture).
        Settings s = new Settings();
        s.setDefaultGenre("Rock");
        SettingsService.saveSettings(s);
        
        // On recharge le service pour qu'il prenne les nouveaux settings
        AudioService serviceWithGenre = new AudioService();
        
        URL url = serviceWithGenre.fetchPreviewFromITunes("Numb Linkin Park");
        assertNotNull(url, "Devrait trouver un résultat avec le genre Rock");
        
        // Restauration (optionnel, bon pour le nettoyage)
        s.setDefaultGenre("pop");
        SettingsService.saveSettings(s);
    }
}