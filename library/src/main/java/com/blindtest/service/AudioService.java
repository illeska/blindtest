package com.blindtest.service;

import com.blindtest.model.Settings;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class AudioService {

    private static final String ITUNES_API = "https://itunes.apple.com/search?limit=1&media=music&term=";
    private MediaPlayer mediaPlayer;
    private final Gson gson = new Gson();

    // Chargement des paramètres de configuration du jeu
    private final Settings settings = SettingsService.loadSettings();

    private boolean shouldPlayWhenReady = false;

    /**
     * Recherche un aperçu sur iTunes et retourne l'URL du preview_audio.
     * Prend en compte le genre musical défini dans les paramètres.
     */
    public URL fetchPreviewFromITunes(String query) {
        try {
            // 1. Récupération du genre par défaut
            String genre = settings.getDefaultGenre();
            String fullQuery = query;

            // 2. Ajout du genre à la recherche si pertinent
            if (genre != null && !genre.isEmpty() && !genre.equalsIgnoreCase("All") && !genre.equalsIgnoreCase("Tout")) {
                fullQuery += " " + genre;
            }

            System.out.println("[AudioService] Recherche brute : " + fullQuery);

            // 3. Encodage propre de l'URL
            String encodedQuery = URLEncoder.encode(fullQuery, StandardCharsets.UTF_8);
            URL url = new URL(ITUNES_API + encodedQuery);
            
            System.out.println("[AudioService] Requête iTunes envoyée : " + url.toString());

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            JsonObject json = gson.fromJson(br, JsonObject.class);
            
            int resultCount = json.get("resultCount").getAsInt();
            System.out.println("[AudioService] Réponse iTunes : " + resultCount + " résultats");

            if (resultCount > 0) {
                String previewUrl = json.getAsJsonArray("results")
                        .get(0).getAsJsonObject()
                        .get("previewUrl").getAsString();
                System.out.println("[AudioService] URL preview trouvée : " + previewUrl);
                return new URL(previewUrl);
            } else {
                System.out.println("[AudioService] Aucun résultat trouvé pour : " + fullQuery);
                // Si la recherche précise échoue (ex: Artiste + Titre + Genre), on pourrait tenter sans le genre ici (optionnel)
            }

        } catch (Exception e) {
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
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose(); // Libérer les ressources
        }

        try {
            Media media = new Media(url.toExternalForm());
            mediaPlayer = new MediaPlayer(media);

            // Appliquer le volume par défaut des Settings
            mediaPlayer.setVolume(settings.getDefaultVolume());
            
            // Gestion des statuts du lecteur
            mediaPlayer.statusProperty().addListener((obs, oldStatus, newStatus) -> {
                if (newStatus == MediaPlayer.Status.READY) {
                    if (shouldPlayWhenReady) {
                        shouldPlayWhenReady = false;
                        mediaPlayer.play();
                    }
                }
            });
            
            // Gestion des erreurs internes au lecteur
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
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

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
            System.out.println("[AudioService] Échec iTunes, passage au fallback local.");
            loadLocalFallback();
        }
    }

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
}