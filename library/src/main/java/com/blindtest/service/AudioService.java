package com.blindtest.service;

import com.blindtest.model.Settings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AudioService {

    private static final String ITUNES_API = "https://itunes.apple.com/search?limit=1&media=music&term=";
    private static final int MAX_RETRIES = 3; // Nombre de tentatives max

    private MediaPlayer mediaPlayer;
    private final Gson gson = new Gson();

    // Chargement des paramètres de configuration du jeu
    private final Settings settings = SettingsService.loadSettings();

    // Cache pour les requêtes API (Query -> URL Preview)
    private final Map<String, URL> apiCache = new ConcurrentHashMap<>();

    // Effets sonores
    private AudioClip sfxCorrect;
    private AudioClip sfxWrong;
    private AudioClip sfxRoundEnd;

    private boolean shouldPlayWhenReady = false;

    public AudioService() {
        loadSoundEffects();
    }

    /**
     * Charge les effets sonores depuis les fichiers locaux.
     */
    private void loadSoundEffects() {
        try {
            // Assurez-vous que ces fichiers existent dans votre dossier data/sfx/
            sfxCorrect = loadClip("data/sfx/correct.mp3");
            sfxWrong = loadClip("data/sfx/wrong.mp3");
            sfxRoundEnd = loadClip("data/sfx/round_end.mp3");
        } catch (Exception e) {
            System.err.println("[AudioService] Impossible de charger les effets sonores : " + e.getMessage());
        }
    }

    private AudioClip loadClip(String path) {
        File f = new File(path);
        if (f.exists()) {
            return new AudioClip(f.toURI().toString());
        }
        return null;
    }

    /**
     * Recherche un aperçu sur iTunes et retourne l'URL du preview_audio.
     * Intègre un système de Cache et de Retry automatique.
     */
    public URL fetchPreviewFromITunes(String query) {
        // 1. Vérification du cache
        if (apiCache.containsKey(query)) {
            System.out.println("[AudioService] Cache hit pour : " + query);
            return apiCache.get(query);
        }

        // 2. Préparation de la requête
        String fullQuery = query;
        String genre = settings.getDefaultGenre();

        if (genre != null && !genre.isEmpty() && !genre.equalsIgnoreCase("All") && !genre.equalsIgnoreCase("Tout")) {
            fullQuery += " " + genre;
        }

        System.out.println("[AudioService] Recherche brute : " + fullQuery);

        // 3. Tentatives avec Retry
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                URL url = executeITunesRequest(fullQuery);
                
                if (url != null) {
                    // Mise en cache et retour
                    apiCache.put(query, url);
                    return url;
                } else {
                    // Pas de résultat trouvé, inutile de retry
                    System.out.println("[AudioService] Aucun résultat iTunes trouvé.");
                    break;
                }

            } catch (Exception e) {
                System.err.println("[AudioService] Erreur réseau (essai " + attempt + "/" + MAX_RETRIES + ") : " + e.getMessage());
                if (attempt < MAX_RETRIES) {
                    try {
                        Thread.sleep(1000 * attempt); // Backoff simple (1s, 2s...)
                    } catch (InterruptedException ignored) {}
                }
            }
        }
        return null;
    }

    private URL executeITunesRequest(String query) throws Exception {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        URL url = new URL(ITUNES_API + encodedQuery);
        
        System.out.println("[AudioService] Requête envoyée : " + url.toString());

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(3000);
        connection.setReadTimeout(3000);

        if (connection.getResponseCode() != 200) {
            throw new Exception("HTTP Error " + connection.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        JsonObject json = gson.fromJson(br, JsonObject.class);
        
        int resultCount = json.get("resultCount").getAsInt();

        if (resultCount > 0) {
            String previewUrl = json.getAsJsonArray("results")
                    .get(0).getAsJsonObject()
                    .get("previewUrl").getAsString();
            System.out.println("[AudioService] URL preview trouvée : " + previewUrl);
            return new URL(previewUrl);
        }
        return null;
    }

    /**
     * Tente de charger un Media à partir d'une URL.
     */
    public boolean loadFromURL(URL url) {
        if (url == null) return false;
        cleanupMediaPlayer();

        try {
            Media media = new Media(url.toExternalForm());
            mediaPlayer = new MediaPlayer(media);

            mediaPlayer.setVolume(settings.getDefaultVolume());
            
            mediaPlayer.statusProperty().addListener((obs, oldStatus, newStatus) -> {
                if (newStatus == MediaPlayer.Status.READY) {
                    if (shouldPlayWhenReady) {
                        shouldPlayWhenReady = false;
                        mediaPlayer.play();
                    }
                }
            });
            
            mediaPlayer.setOnError(() -> System.err.println("[AudioService] Erreur MediaPlayer : " + mediaPlayer.getError()));

            return true;

        } catch (Exception e) {
            System.err.println("[AudioService] Impossible de charger depuis URL : " + e.getMessage());
            return false;
        }
    }

    /**
     * Charge un fichier audio local depuis /data/ (fallback).
     */
    public boolean loadLocalFallback() {
        cleanupMediaPlayer();

        try {
            File file = new File("data/fallback.mp3");
            if (!file.exists()) {
                System.err.println("[AudioService] AVERTISSEMENT : Aucun fallback local trouvé (data/fallback.mp3).");
                return false;
            }

            Media media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(settings.getDefaultVolume());

            return true;

        } catch (Exception e) {
            System.err.println("[AudioService] Erreur fallback local : " + e.getMessage());
            return false;
        }
    }

    /**
     * Charge un son, avec fallback automatique.
     */
    public void loadWithFallback(String query) {
        URL preview = fetchPreviewFromITunes(query);
        boolean ok = preview != null && loadFromURL(preview);

        if (!ok) {
            System.out.println("[AudioService] Échec iTunes ou chargement, passage au fallback local.");
            loadLocalFallback();
        }
    }

    private void cleanupMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
    }

    // --- Contrôles de lecture ---

    public void play() {
        if (mediaPlayer != null) {
            javafx.application.Platform.runLater(() -> {
                if (mediaPlayer.getStatus() == MediaPlayer.Status.READY || mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
                    mediaPlayer.play();
                } else {
                    shouldPlayWhenReady = true;
                }
            });
        }
    }

    public void pause() {
        if (mediaPlayer != null) {
            javafx.application.Platform.runLater(() -> mediaPlayer.pause());
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            javafx.application.Platform.runLater(() -> mediaPlayer.stop());
        }
    }

    public void setVolume(double volume) {
        if (mediaPlayer != null) {
            javafx.application.Platform.runLater(() -> mediaPlayer.setVolume(volume));
        }
    }

    // --- Gestion des Effets Sonores (SFX) ---

    public void playCorrectSound() {
        if (sfxCorrect != null) sfxCorrect.play();
    }

    public void playWrongSound() {
        if (sfxWrong != null) sfxWrong.play();
    }

    public void playRoundEndSound() {
        if (sfxRoundEnd != null) sfxRoundEnd.play();
    }
    
    // Utile pour les tests
    public void clearCache() {
        apiCache.clear();
    }
}