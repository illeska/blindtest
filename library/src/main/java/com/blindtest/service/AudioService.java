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
    private static final int MAX_RETRIES = 3; // Nombre de tentatives max pour l'API

    private MediaPlayer mediaPlayer;
    private final Gson gson = new Gson();

    private final Settings settings = SettingsService.loadSettings();
    private boolean shouldPlayWhenReady = false;

    // --- NOUVEAU : Cache et Effets Sonores ---
    private final Map<String, URL> apiCache = new ConcurrentHashMap<>();
    
    private AudioClip sfxCorrect;
    private AudioClip sfxWrong;
    private AudioClip sfxRoundEnd;

    public AudioService() {
        loadSoundEffects();
    }

    /**
     * Charge les effets sonores (bruitages courts).
     */
    private void loadSoundEffects() {
        try {
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
        } else {
            // Log discret pour ne pas spammer si les fichiers manquent
            // System.out.println("[AudioService] Info: Bruitage non trouvé : " + path);
        }
        return null;
    }

    /**
     * Recherche un aperçu sur iTunes et retourne l'URL du preview_audio.
     * Intègre Cache + Retry Automatique.
     */
    public URL fetchPreviewFromITunes(String query) {
        // 1. Vérification du Cache
        if (apiCache.containsKey(query)) {
            System.out.println("[AudioService] ✅ Cache hit pour : " + query);
            return apiCache.get(query);
        }

        String genre = settings.getDefaultGenre();
        String fullQuery = query;
        if (genre != null && !genre.isEmpty() && !genre.equalsIgnoreCase("All") && !genre.equalsIgnoreCase("Tout")) {
            fullQuery += " " + genre;
        }

        System.out.println("[AudioService] Recherche brute : " + fullQuery);

        // 2. Boucle de Retry
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                if (attempt > 1) System.out.println("[AudioService] 🔄 Tentative #" + attempt + "...");

                String encodedQuery = URLEncoder.encode(fullQuery, StandardCharsets.UTF_8);
                URL url = new URL(ITUNES_API + encodedQuery);
                
                System.out.println("[AudioService] Requête envoyée : " + url.toString());

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                if (responseCode != 200) {
                    System.err.println("[AudioService] Erreur HTTP : " + responseCode);
                    // On lance une exception pour déclencher le retry
                    throw new Exception("HTTP Error " + responseCode);
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                JsonObject json = gson.fromJson(br, JsonObject.class);
                br.close();
                
                int resultCount = json.get("resultCount").getAsInt();
                System.out.println("[AudioService] Réponse iTunes : " + resultCount + " résultats");

                if (resultCount > 0) {
                    String previewUrl = json.getAsJsonArray("results")
                            .get(0).getAsJsonObject()
                            .get("previewUrl").getAsString();
                    System.out.println("[AudioService] URL preview trouvée : " + previewUrl);
                    
                    URL validUrl = new URL(previewUrl);
                    // Mise en cache
                    apiCache.put(query, validUrl);
                    return validUrl;
                } else {
                    System.out.println("[AudioService] Aucun résultat trouvé pour : " + fullQuery);
                    return null; // Pas la peine de retry si la recherche ne donne rien
                }

            } catch (Exception e) {
                System.err.println("[AudioService] Erreur essai " + attempt + "/" + MAX_RETRIES + " : " + e.getMessage());
                if (attempt < MAX_RETRIES) {
                    try {
                        Thread.sleep(1000 * attempt); // Backoff : 1s, 2s...
                    } catch (InterruptedException ignored) {}
                }
            }
        }
        
        System.err.println("[AudioService] ❌ Échec définitif après " + MAX_RETRIES + " tentatives pour : " + query);
        return null;
    }

    /**
     * Charge un Media à partir d'une URL avec gestion d'erreur améliorée.
     */
    public boolean loadFromURL(URL url) {
        if (url == null) {
            System.err.println("[AudioService] URL nulle, impossible de charger");
            return false;
        }

        cleanupMediaPlayer();

        try {
            System.out.println("[AudioService] Tentative de chargement : " + url.toExternalForm());
            
            Media media = new Media(url.toExternalForm());
            mediaPlayer = new MediaPlayer(media);

            mediaPlayer.setVolume(settings.getDefaultVolume());
            
            // Gestion des statuts avec logs détaillés
            mediaPlayer.statusProperty().addListener((obs, oldStatus, newStatus) -> {
                System.out.println("[AudioService] Statut MediaPlayer : " + oldStatus + " -> " + newStatus);
                
                if (newStatus == MediaPlayer.Status.READY) {
                    System.out.println("[AudioService] ✅ Media prêt à être joué");
                    if (shouldPlayWhenReady) {
                        shouldPlayWhenReady = false;
                        System.out.println("[AudioService] Lecture automatique...");
                        mediaPlayer.play();
                    }
                } else if (newStatus == MediaPlayer.Status.HALTED) {
                    System.err.println("[AudioService] ❌ MediaPlayer HALTED (échec critique)");
                    if (mediaPlayer.getError() != null) {
                        System.err.println("[AudioService] Erreur : " + mediaPlayer.getError().getMessage());
                        mediaPlayer.getError().printStackTrace();
                    }
                }
            });
            
            // Gestion des erreurs
            mediaPlayer.setOnError(() -> {
                System.err.println("[AudioService] ❌ Erreur MediaPlayer détectée");
                if (mediaPlayer.getError() != null) {
                    System.err.println("[AudioService] Détails : " + mediaPlayer.getError().getMessage());
                    mediaPlayer.getError().printStackTrace();
                }
            });

            // Gestion de la fin de lecture
            mediaPlayer.setOnEndOfMedia(() -> {
                System.out.println("[AudioService] Fin de l'extrait audio");
            });

            System.out.println("[AudioService] ✅ MediaPlayer créé avec succès");
            return true;

        } catch (Exception e) {
            System.err.println("[AudioService] ❌ Exception lors de la création du MediaPlayer : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Charge un fichier audio local depuis /data/ (fallback).
     */
    public boolean loadLocalFallback() {
        System.out.println("[AudioService] 🔄 Tentative de chargement du fallback local...");
        
        cleanupMediaPlayer();

        try {
            File file = new File("data/fallback.mp3");
            if (!file.exists()) {
                System.err.println("[AudioService] ❌ Fichier fallback introuvable : " + file.getAbsolutePath());
                return false;
            }

            System.out.println("[AudioService] Chargement de : " + file.getAbsolutePath());
            Media media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(settings.getDefaultVolume());

            mediaPlayer.setOnError(() -> {
                System.err.println("[AudioService] Erreur fallback : " + mediaPlayer.getError());
            });

            System.out.println("[AudioService] ✅ Fallback chargé");
            return true;

        } catch (Exception e) {
            System.err.println("[AudioService] ❌ Erreur fallback local : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Charge un son, avec fallback automatique si iTunes échoue.
     */
    public void loadWithFallback(String query) {
        System.out.println("[AudioService] 🎵 Chargement audio pour : " + query);
        
        URL preview = fetchPreviewFromITunes(query); // Utilise maintenant le Cache + Retry
        boolean ok = false;
        
        if (preview != null) {
            ok = loadFromURL(preview);
            
            // Petite pause pour laisser le temps au MediaPlayer de s'initialiser
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Vérification du statut après chargement
            if (mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.HALTED) {
                System.err.println("[AudioService] ⚠️ MediaPlayer en état HALTED, passage au fallback");
                ok = false;
            }
        }

        if (!ok) {
            System.out.println("[AudioService] 🔄 Passage au fallback local");
            loadLocalFallback();
        }
    }

    private void cleanupMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            } catch (Exception e) {
                System.err.println("[AudioService] Erreur lors du nettoyage du MediaPlayer : " + e.getMessage());
            }
            mediaPlayer = null;
        }
    }

    // --- Contrôles de lecture ---

    public void play() {
        if (mediaPlayer != null) {
            javafx.application.Platform.runLater(() -> {
                try {
                    MediaPlayer.Status status = mediaPlayer.getStatus();
                    System.out.println("[AudioService] Commande play() - Statut actuel : " + status);
                    
                    if (status == MediaPlayer.Status.READY || status == MediaPlayer.Status.PAUSED || status == MediaPlayer.Status.STOPPED) {
                        mediaPlayer.play();
                        System.out.println("[AudioService] ▶️ Lecture lancée");
                    } else {
                        System.out.println("[AudioService] Attente du statut READY...");
                        shouldPlayWhenReady = true;
                    }
                } catch (Exception e) {
                    System.err.println("[AudioService] Erreur play() : " + e.getMessage());
                }
            });
        } else {
            System.err.println("[AudioService] ❌ Aucun MediaPlayer disponible");
        }
    }

    public void pause() {
        if (mediaPlayer != null) {
            javafx.application.Platform.runLater(() -> {
                try {
                    mediaPlayer.pause();
                    System.out.println("[AudioService] ⏸️ Lecture mise en pause");
                } catch (Exception e) {
                    System.err.println("[AudioService] Erreur pause() : " + e.getMessage());
                }
            });
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            javafx.application.Platform.runLater(() -> {
                try {
                    mediaPlayer.stop();
                    System.out.println("[AudioService] ⏹️ Lecture arrêtée");
                } catch (Exception e) {
                    System.err.println("[AudioService] Erreur stop() : " + e.getMessage());
                }
            });
        }
    }

    public void setVolume(double volume) {
        if (mediaPlayer != null) {
            javafx.application.Platform.runLater(() -> {
                try {
                    mediaPlayer.setVolume(volume);
                } catch (Exception e) {
                    System.err.println("[AudioService] Erreur setVolume() : " + e.getMessage());
                }
            });
        }
    }

    // --- Gestion des Effets Sonores (SFX) ---
    // Méthodes requises par GameController

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