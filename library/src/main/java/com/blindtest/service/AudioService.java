package com.blindtest.service;

import com.blindtest.model.Settings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service optimisé pour la gestion audio avec cache persistant,
 * gestion d'erreurs améliorée et fallback intelligent.
 */
public class AudioService {
    private static final Logger LOGGER = Logger.getLogger(AudioService.class.getName());
    
    private static final String DEEZER_API = "https://api.deezer.com/search/track?q=";
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 1000;
    private static final int CONNECTION_TIMEOUT_MS = 5000;
    private static final int READ_TIMEOUT_MS = 5000;
    
    // Cache avec TTL (Time To Live)
    private static final long CACHE_TTL_MS = 24 * 60 * 60 * 1000; // 24 heures
    private static final int MAX_CACHE_SIZE = 500;
    private static final String CACHE_FILE = "data/audio_cache.dat";
    
    private MediaPlayer mediaPlayer;
    private final Gson gson = new Gson();
    private final Settings settings = SettingsService.loadSettings();
    private boolean shouldPlayWhenReady = false;
    
    // Cache avec timestamp pour gérer le TTL
    private final Map<String, CacheEntry> apiCache = new ConcurrentHashMap<>();
    
    // Effets sonores
    private AudioClip sfxVictory;
    private AudioClip sfxFail;
    private AudioClip sfxBtnClick;
    private MediaPlayer menuMusicPlayer;
    
    // Métriques
    private int apiHits = 0;
    private int cacheHits = 0;
    private int fallbackHits = 0;

    /**
     * Entrée de cache avec timestamp pour TTL
     */
    private static class CacheEntry implements Serializable {
        private static final long serialVersionUID = 1L;
        final String url;
        final long timestamp;
        
        CacheEntry(String url, long timestamp) {
            this.url = url;
            this.timestamp = timestamp;
        }
        
        boolean isExpired() {
            return (System.currentTimeMillis() - timestamp) > CACHE_TTL_MS;
        }
    }

    /**
     * Constructeur du service audio.
     * Charge les effets sonores et le cache persistant.
     */
    public AudioService() {
        loadSoundEffects();
        loadCacheFromDisk();
    }

    // ===============================
    // GESTION DU CACHE PERSISTANT
    // ===============================

    /**
     * Charge le cache depuis le disque au démarrage
     */
    @SuppressWarnings("unchecked")
    private void loadCacheFromDisk() {
        File cacheFile = new File(CACHE_FILE);
        if (!cacheFile.exists()) {
            LOGGER.info("Aucun cache trouvé, démarrage à vide");
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cacheFile))) {
            Map<String, CacheEntry> loadedCache = (Map<String, CacheEntry>) ois.readObject();
            
            // Filtrer les entrées expirées
            loadedCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
            
            apiCache.putAll(loadedCache);
            LOGGER.info("Cache chargé : " + apiCache.size() + " entrées valides");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erreur chargement cache : " + e.getMessage(), e);
        }
    }

    /**
     * Sauvegarde le cache sur disque
     */
    private void saveCacheToDisk() {
        try {
            PersistenceService.ensureDirectoryExists(CACHE_FILE);
            
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CACHE_FILE))) {
                oos.writeObject(apiCache);
                LOGGER.fine("Cache sauvegardé : " + apiCache.size() + " entrées");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Erreur sauvegarde cache : " + e.getMessage(), e);
        }
    }

    /**
     * Ajoute une entrée au cache avec gestion de taille maximale
     */
    private void addToCache(String query, String url) {
        // Si cache plein, supprimer les entrées les plus anciennes
        if (apiCache.size() >= MAX_CACHE_SIZE) {
            String oldestKey = apiCache.entrySet().stream()
                .min(Map.Entry.comparingByValue((a, b) -> 
                    Long.compare(a.timestamp, b.timestamp)))
                .map(Map.Entry::getKey)
                .orElse(null);
            
            if (oldestKey != null) {
                apiCache.remove(oldestKey);
                LOGGER.fine("Cache plein : suppression de l'entrée la plus ancienne");
            }
        }
        
        apiCache.put(query, new CacheEntry(url, System.currentTimeMillis()));
        saveCacheToDisk();
    }

    /**
     * Récupère une entrée du cache si valide
     */
    private URL getCachedUrl(String query) {
        CacheEntry entry = apiCache.get(query);
        
        if (entry == null) {
            return null;
        }
        
        if (entry.isExpired()) {
            apiCache.remove(query);
            saveCacheToDisk();
            LOGGER.fine("Entrée cache expirée : " + query);
            return null;
        }
        
        try {
            cacheHits++;
            return new URL(entry.url);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "URL cache invalide : " + e.getMessage(), e);
            apiCache.remove(query);
            return null;
        }
    }

    // ===============================
    // CHARGEMENT EFFETS SONORES
    // ===============================


    /**
     * Charge les effets sonores depuis le système de fichiers.
     */
    private void loadSoundEffects() {
        try {
            sfxVictory = loadClip("data/sfx/bonne.mp3");
            sfxFail = loadClip("data/sfx/mauvaise.mp3");
            sfxBtnClick = loadClip("data/sfx/bouton.mp3");
            
            File menuMusicFile = new File("data/sfx/menu.mp3");
            if (menuMusicFile.exists()) {
                menuMusicPlayer = new MediaPlayer(new Media(menuMusicFile.toURI().toString()));
                menuMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            }
            
            LOGGER.info("Effets sonores chargés avec succès");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur chargement effets sonores", e);
        }
    }

    /**
     * Charge un clip audio depuis un fichier.
     * @param path Le chemin du fichier audio
     * @return L'AudioClip chargé ou null en cas d'erreur
     */
    private AudioClip loadClip(String path) {
        File f = new File(path);
        if (f.exists()) {
            return new AudioClip(f.toURI().toString());
        }
        LOGGER.warning("Fichier audio introuvable : " + path);
        return null;
    }

    // ===============================
    // RECHERCHE DEEZER AVEC RETRY
    // ===============================

    /**
     * Recherche un extrait sur Deezer avec gestion cache et retry
     */
    public URL fetchPreviewFromDeezer(String query) {
        if (query == null || query.trim().isEmpty()) {
            LOGGER.warning("Requête vide pour Deezer");
            return null;
        }

        // Vérifier le cache
        URL cachedUrl = getCachedUrl(query);
        if (cachedUrl != null) {
            LOGGER.fine("Cache hit : " + query);
            return cachedUrl;
        }

        LOGGER.info("Recherche Deezer : " + query);
        apiHits++;

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                if (attempt > 1) {
                    LOGGER.info("Tentative " + attempt + "/" + MAX_RETRIES);
                    Thread.sleep(RETRY_DELAY_MS * attempt);
                }

                String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
                URL apiUrl = new URL(DEEZER_API + encodedQuery);

                HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(CONNECTION_TIMEOUT_MS);
                connection.setReadTimeout(READ_TIMEOUT_MS);
                connection.setRequestProperty("User-Agent", "BlindTest/1.0");

                int responseCode = connection.getResponseCode();
                if (responseCode != 200) {
                    throw new IOException("HTTP error " + responseCode);
                }

                BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)
                );
                JsonObject json = gson.fromJson(br, JsonObject.class);
                br.close();

                JsonArray data = json.getAsJsonArray("data");
                if (data == null || data.size() == 0) {
                    LOGGER.warning("Aucun résultat Deezer pour : " + query);
                    return null;
                }

                String previewUrl = findBestMatch(data, query);
                if (previewUrl != null) {
                    URL resultUrl = new URL(previewUrl);
                    addToCache(query, previewUrl);
                    LOGGER.info("Résultat trouvé et mis en cache : " + query);
                    return resultUrl;
                }

                return null;

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.log(Level.SEVERE, "Interruption lors de la recherche", e);
                return null;
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, 
                    "Erreur tentative " + attempt + "/" + MAX_RETRIES + " : " + e.getMessage(), e);
                
                if (attempt == MAX_RETRIES) {
                    LOGGER.severe("Échec définitif après " + MAX_RETRIES + " tentatives pour : " + query);
                    return null;
                }
            }
        }

        return null;
    }

    /**
     * Trouve le meilleur match dans les résultats Deezer
     */
    private String findBestMatch(JsonArray data, String query) {
        String searchTitle = query.contains(" - ") 
            ? query.split(" - ")[0].trim().toLowerCase() 
            : query.toLowerCase();

        // Première passe : match exact sans remixes/covers
        for (int i = 0; i < Math.min(5, data.size()); i++) {
            JsonObject track = data.get(i).getAsJsonObject();
            String title = track.get("title").getAsString().toLowerCase();

            if (title.contains("remix") || title.contains("cover") || 
                title.contains("live") || title.contains("karaoke")) {
                continue;
            }

            if (title.contains(searchTitle)) {
                LOGGER.fine("Match exact trouvé : " + track.get("title").getAsString());
                return track.get("preview").getAsString();
            }
        }

        // Deuxième passe : premier résultat acceptable
        for (int i = 0; i < Math.min(5, data.size()); i++) {
            JsonObject track = data.get(i).getAsJsonObject();
            String title = track.get("title").getAsString().toLowerCase();

            if (!title.contains("remix") && !title.contains("cover") && 
                !title.contains("live") && !title.contains("karaoke")) {
                LOGGER.fine("Résultat fallback utilisé");
                return track.get("preview").getAsString();
            }
        }

        // Dernier recours : premier résultat
        LOGGER.warning("Utilisation du premier résultat par défaut");
        return data.get(0).getAsJsonObject().get("preview").getAsString();
    }

    // ===============================
    // GESTION DU MEDIAPLAYER
    // ===============================

    /**
     * Charge un média depuis une URL.
     * @param url L'URL du média à charger
     * @return true si le chargement réussit
     */
    public boolean loadFromURL(URL url) {
        if (url == null) {
            LOGGER.warning("URL null fournie à loadFromURL");
            return false;
        }

        cleanupMediaPlayer();

        try {
            Media media = new Media(url.toExternalForm());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(settings.getDefaultVolume());

            mediaPlayer.setOnError(() -> {
                LOGGER.severe("Erreur MediaPlayer : " + mediaPlayer.getError().getMessage());
            });

            mediaPlayer.statusProperty().addListener((obs, oldStatus, newStatus) -> {
                if (newStatus == MediaPlayer.Status.READY && shouldPlayWhenReady) {
                    shouldPlayWhenReady = false;
                    mediaPlayer.play();
                }
            });

            LOGGER.fine("Media chargé depuis : " + url);
            return true;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur chargement media : " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Charge un extrait avec fallback automatique
     */
    public void loadWithFallback(String query) {
        URL preview = fetchPreviewFromDeezer(query);
        boolean success = false;

        if (preview != null) {
            success = loadFromURL(preview);
        }

        if (!success) {
            LOGGER.warning("Fallback vers fichier local pour : " + query);
            fallbackHits++;
            loadLocalFallback();
        }
    }

    /**
     * Charge le fichier de fallback local
     */
    public boolean loadLocalFallback() {
        cleanupMediaPlayer();
        
        try {
            File file = new File("data/fallback.mp3");
            if (!file.exists()) {
                LOGGER.severe("Fichier fallback introuvable : " + file.getAbsolutePath());
                return false;
            }

            Media media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(settings.getDefaultVolume());
            
            LOGGER.info("Fallback local chargé avec succès");
            return true;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur chargement fallback", e);
            return false;
        }
    }

    /**
     * Nettoie et libère les ressources du MediaPlayer actuel.
     */
    private void cleanupMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }

    // ===============================
    // CONTRÔLES AUDIO
    // ===============================

    /**
     * Démarre la lecture du média.
     */
    public void play() {
        if (mediaPlayer != null) {
            javafx.application.Platform.runLater(() -> {
                MediaPlayer.Status status = mediaPlayer.getStatus();
                if (status == MediaPlayer.Status.READY || 
                    status == MediaPlayer.Status.PAUSED || 
                    status == MediaPlayer.Status.STOPPED) {
                    mediaPlayer.play();
                } else {
                    shouldPlayWhenReady = true;
                }
            });
        }
    }

    /**
     * Met en pause la lecture du média.
     */
    public void pause() {
        if (mediaPlayer != null) {
            javafx.application.Platform.runLater(mediaPlayer::pause);
        }
    }

    /**
     * Arrête la lecture du média.
     */
    public void stop() {
        if (mediaPlayer != null) {
            javafx.application.Platform.runLater(mediaPlayer::stop);
        }
    }

    /**
     * Définit le volume du média actuel.
     * @param volume Le volume (0.0 à 1.0)
     */
    public void setVolume(double volume) {
        if (mediaPlayer != null) {
            javafx.application.Platform.runLater(() -> mediaPlayer.setVolume(volume));
        }
    }

    /**
     * Définit le volume global pour tous les médias.
     * @param volume Le volume (0.0 à 1.0)
     */
    public void setGlobalVolume(double volume) {
        settings.setDefaultVolume((float) volume);
        
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume);
        }
        
        if (menuMusicPlayer != null) {
            menuMusicPlayer.setVolume(volume);
        }
    }

    // ===============================
    // EFFETS SONORES
    // ===============================

    /**
     * Joue l'effet sonore de victoire.
     */
    public void playSfxVictory() {
        playClip(sfxVictory);
    }


    /**
     * Joue l'effet sonore d'échec.
     */
    public void playSfxFail() {
        playClip(sfxFail);
    }

    /**
     * Joue l'effet sonore de clic de bouton.
     */
    public void playClick() {
        playClip(sfxBtnClick);
    }


    /**
     * Joue un clip audio.
     * @param clip Le clip audio à jouer
     */
    private void playClip(AudioClip clip) {
        if (clip != null) {
            clip.setVolume(settings.getDefaultVolume());
            clip.play();
        }
    }

    /**
     * Joue le son de réponse correcte.
     */
    public void playCorrectSound() { playSfxVictory(); }

    /**
     * Joue le son de réponse incorrecte.
     */
    public void playWrongSound() { playSfxFail(); }

    /**
     * Démarre la musique du menu en boucle.
     */
    public void startMenuMusic() {
        if (menuMusicPlayer != null) {
            menuMusicPlayer.setVolume(settings.getDefaultVolume());
            menuMusicPlayer.play();
        }
    }

    /**
     * Arrête la musique du menu.
     */
    public void stopMenuMusic() {
        if (menuMusicPlayer != null) {
            menuMusicPlayer.stop();
        }
    }

    // ===============================
    // MÉTRIQUES & DIAGNOSTICS
    // ===============================

    /**
     * Affiche les statistiques d'utilisation du cache
     */
    public void printCacheStats() {
        int totalRequests = apiHits + cacheHits;
        double hitRate = totalRequests > 0 ? (cacheHits * 100.0 / totalRequests) : 0;
        
        LOGGER.info(String.format(
            "Stats AudioService - API: %d | Cache: %d | Fallback: %d | Hit Rate: %.1f%% | Cache Size: %d",
            apiHits, cacheHits, fallbackHits, hitRate, apiCache.size()
        ));
    }

    /**
     * Nettoie le cache (entrées expirées)
     */
    public void cleanExpiredCache() {
        int sizeBefore = apiCache.size();
        apiCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
        int removed = sizeBefore - apiCache.size();
        
        if (removed > 0) {
            saveCacheToDisk();
            LOGGER.info("Cache nettoyé : " + removed + " entrées expirées supprimées");
        }
    }

    /**
     * Vide complètement le cache
     */
    public void clearCache() {
        apiCache.clear();
        new File(CACHE_FILE).delete();
        LOGGER.info("Cache vidé complètement");
    }
}