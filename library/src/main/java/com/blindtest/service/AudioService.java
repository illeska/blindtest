package com.blindtest.service;

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

    /**
     * Recherche un aperçu sur iTunes et retourne l'URL du preview_audio.
     */
    public URL fetchPreviewFromITunes(String query) {
        try {
            String encoded = query.replace(" ", "+");
            URL url = new URL(ITUNES_API + encoded);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            JsonObject json = gson.fromJson(br, JsonObject.class);

            if (json.get("resultCount").getAsInt() > 0) {
                String previewUrl = json.getAsJsonArray("results")
                        .get(0).getAsJsonObject()
                        .get("previewUrl").getAsString();

                return new URL(previewUrl);
            }

        } catch (Exception e) {
            System.out.println("[AudioService] Erreur API iTunes: " + e.getMessage());
        }
        return null; // important pour gérer le fallback
    }

    /**
     * Charge un média depuis une URL iTunes.
     */
    public boolean loadFromURL(URL audioUrl) {
        try {
            Media media = new Media(audioUrl.toString());
            mediaPlayer = new MediaPlayer(media);
            return true;

        } catch (Exception e) {
            System.out.println("[AudioService] Impossible de charger depuis URL : " + e.getMessage());
            return false;
        }
    }

    /**
     * Charge un fichier audio local depuis /data/
     */
    public boolean loadLocalFallback() {
        try {
            File file = new File("data/fallback.mp3");
            if (!file.exists()) {
                System.out.println("[AudioService] Aucun fallback local trouvé.");
                return false;
            }

            Media media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            return true;

        } catch (Exception e) {
            System.out.println("[AudioService] Erreur fallback local: " + e.getMessage());
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
        if (mediaPlayer != null) mediaPlayer.play();
    }

    public void pause() {
        if (mediaPlayer != null) mediaPlayer.pause();
    }

    public void stop() {
        if (mediaPlayer != null) mediaPlayer.stop();
    }

    public void setVolume(double volume) {
        if (mediaPlayer != null) mediaPlayer.setVolume(volume);
    }
}
