package com.blindtest.service;

import com.blindtest.model.Settings; // Import pour les paramètres
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AudioService {

    private static final String ITUNES_API = "https://itunes.apple.com/search?limit=1&term=";
    private MediaPlayer mediaPlayer;
    private final Gson gson = new Gson();

    // 🔥 AJOUT : Chargement des paramètres de configuration du jeu
    private final Settings settings = SettingsService.loadSettings();

    private boolean shouldPlayWhenReady = false;

    /**
     * Recherche un aperçu sur iTunes et retourne l'URL du preview_audio.
     */
    public URL fetchPreviewFromITunes(String query) {
        try {
            String encoded = query.replace(" ", "+");
            URL url = new URL(ITUNES_API + encoded);
            System.out.println("[AudioService] Recherche iTunes pour: " + url.toString());

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            JsonObject json = gson.fromJson(br, JsonObject.class);
            System.out.println("[AudioService] Réponse iTunes: " + json.get("resultCount").getAsInt() + " résultats");

            if (json.get("resultCount").getAsInt() > 0) {
                String previewUrl = json.getAsJsonArray("results")
                        .get(0).getAsJsonObject()
                        .get("previewUrl").getAsString();
                System.out.println("[AudioService] URL preview trouvée: " + previewUrl);
                return new URL(previewUrl);
            }

        } catch (Exception e) {
            // Amélioration de la gestion d'erreur
            System.err.println("[AudioService] Erreur lors de la recherche iTunes pour '" + query + "': " + e.getMessage());
        }
        return null;
    }

    /**
     * Tente de charger un Media à partir d'une URL.
     */
    public boolean loadFromURL(URL url) {
        if (url == null) return false;

        // Si un lecteur existe déjà, l'arrêter avant de charger le nouveau
        if (mediaPlayer != null) mediaPlayer.stop();

        try {
            Media media = new Media(url.toExternalForm());
            mediaPlayer = new MediaPlayer(media);

            // 🔥 INTÉGRATION VOLUME: Appliquer le volume par défaut des Settings
            mediaPlayer.setVolume(settings.getDefaultVolume());
            System.out.println("[AudioService] Volume réglé à: " + settings.getDefaultVolume());

            // Attendre que le MediaPlayer soit prêt avant de jouer
            mediaPlayer.statusProperty().addListener((obs, oldStatus, newStatus) -> {
                System.out.println("[AudioService] Status changé: " + oldStatus + " -> " + newStatus);
                if (newStatus == MediaPlayer.Status.READY) {
                    System.out.println("[AudioService] Media prêt, peut jouer.");
                    if (shouldPlayWhenReady) {
                        shouldPlayWhenReady = false;
                        mediaPlayer.play();
                        System.out.println("[AudioService] Lecture démarrée automatiquement.");
                    }
                } else if (newStatus == MediaPlayer.Status.STALLED) {
                    System.err.println("[AudioService] Media stalled.");
                } else if (newStatus == MediaPlayer.Status.UNKNOWN) {
                    System.err.println("[AudioService] Erreur inconnue du MediaPlayer.");
                } else if (newStatus == MediaPlayer.Status.PLAYING) {
                    System.out.println("[AudioService] Media en cours de lecture.");
                } else if (newStatus == MediaPlayer.Status.STOPPED) {
                    System.out.println("[AudioService] Media arrêté.");
                } else if (newStatus == MediaPlayer.Status.PAUSED) {
                    System.out.println("[AudioService] Media en pause.");
                }
            });

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
        // Si un lecteur existe déjà, l'arrêter avant de charger le nouveau
        if (mediaPlayer != null) mediaPlayer.stop();

        try {
            File file = new File("data/fallback.mp3");
            System.out.println("[AudioService] Recherche fallback local: " + file.getAbsolutePath());
            if (!file.exists()) {
                System.out.println("[AudioService] Aucun fallback local trouvé (data/fallback.mp3).");
                return false;
            }

            Media media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            // 🔥 INTÉGRATION VOLUME: Appliquer le volume par défaut des Settings
            mediaPlayer.setVolume(settings.getDefaultVolume());
            System.out.println("[AudioService] Volume réglé à: " + settings.getDefaultVolume());

            return true;

        } catch (Exception e) {
            System.err.println("[AudioService] Erreur fallback local: " + e.getMessage());
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
            System.out.println("[AudioService] Fallback → playlist locale");
            loadLocalFallback();
        }
    }

    public void play() {
        if (mediaPlayer != null) {
            javafx.application.Platform.runLater(() -> {
                if (mediaPlayer.getStatus() == MediaPlayer.Status.READY || mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
                    mediaPlayer.play();
                    System.out.println("[AudioService] Lecture démarrée.");
                } else {
                    shouldPlayWhenReady = true;
                    System.out.println("[AudioService] MediaPlayer pas prêt, statut: " + mediaPlayer.getStatus() + " - lecture programmée.");
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
}