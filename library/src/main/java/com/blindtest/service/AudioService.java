package com.blindtest.service;

import com.blindtest.model.Settings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
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

    private static final String DEEZER_API =
            "https://api.deezer.com/search/track?q=";
    private static final int MAX_RETRIES = 3;

    private MediaPlayer mediaPlayer;
    private final Gson gson = new Gson();

    private final Settings settings = SettingsService.loadSettings();
    private boolean shouldPlayWhenReady = false;

    private final Map<String, URL> apiCache = new ConcurrentHashMap<>();
    private AudioClip sfxVictory;
    private AudioClip sfxFail;
    private AudioClip sfxBtnClick;
    private MediaPlayer menuMusicPlayer;

    public AudioService() {
        loadSoundEffects();
    }

    private void loadSoundEffects() {
        try {      
            // --- Nouveaux sons ---
            sfxVictory = loadClip("data/sfx/bonne.mp3");
            sfxFail = loadClip("data/sfx/mauvaise.mp3");
            sfxBtnClick = loadClip("data/sfx/bouton.mp3");
            
            // Musique de fond (Menu + Fin)
            File menuMusicFile = new File("data/sfx/menu.mp3");
            if (menuMusicFile.exists()) {
                menuMusicPlayer = new MediaPlayer(new Media(menuMusicFile.toURI().toString()));
                menuMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Joue en boucle
            }
        } catch (Exception e) {
            System.err.println("[AudioService] Erreur chargement sons : " + e.getMessage());
        }
    }

    private AudioClip loadClip(String path) {
        File f = new File(path);
        if (f.exists()) {
            return new AudioClip(f.toURI().toString());
        }
        return null;
    }

    // ===============================
    // DEEZER PREVIEW SEARCH
    // ===============================

    public URL fetchPreviewFromDeezer(String query) {

        if (apiCache.containsKey(query)) {
            System.out.println("[AudioService] Cache hit for: " + query);
            return apiCache.get(query);
        }

        System.out.println("[AudioService] Deezer search: " + query);

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                if (attempt > 1) {
                    System.out.println("[AudioService] Retry attempt " + attempt);
                }

                String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
                URL url = new URL(DEEZER_API + encodedQuery);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                if (responseCode != 200) {
                    throw new Exception("HTTP error " + responseCode);
                }

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)
                );
                JsonObject json = gson.fromJson(br, JsonObject.class);
                br.close();

                JsonArray data = json.getAsJsonArray("data");
                if (data == null || data.size() == 0) {
                    System.out.println("[AudioService] No Deezer result");
                    return null;
                }

                String searchTitle =
                        query.contains(" - ")
                                ? query.split(" - ")[0].trim().toLowerCase()
                                : query.toLowerCase();

                for (int i = 0; i < Math.min(5, data.size()); i++) {
                    JsonObject track = data.get(i).getAsJsonObject();
                    String title = track.get("title").getAsString();
                    String titleLower = title.toLowerCase();

                    if (titleLower.contains("remix")
                            || titleLower.contains("cover")
                            || titleLower.contains("live")
                            || titleLower.contains("karaoke")) {
                        continue;
                    }

                    if (titleLower.contains(searchTitle)) {
                        String previewUrl = track.get("preview").getAsString();
                        URL validUrl = new URL(previewUrl);
                        apiCache.put(query, validUrl);
                        System.out.println("[AudioService] Match found: " + title);
                        return validUrl;
                    }
                }

                for (int i = 0; i < Math.min(5, data.size()); i++) {
                    JsonObject track = data.get(i).getAsJsonObject();
                    String titleLower = track.get("title").getAsString().toLowerCase();

                    if (!titleLower.contains("remix")
                            && !titleLower.contains("cover")
                            && !titleLower.contains("live")
                            && !titleLower.contains("karaoke")) {
                        String previewUrl = track.get("preview").getAsString();
                        URL validUrl = new URL(previewUrl);
                        apiCache.put(query, validUrl);
                        System.out.println("[AudioService] Fallback result used");
                        return validUrl;
                    }
                }

                String previewUrl = data.get(0).getAsJsonObject().get("preview").getAsString();
                URL validUrl = new URL(previewUrl);
                apiCache.put(query, validUrl);
                System.out.println("[AudioService] Default result used");
                return validUrl;

            } catch (Exception e) {
                System.err.println(
                        "[AudioService] Error attempt "
                                + attempt
                                + "/"
                                + MAX_RETRIES
                );
                if (attempt < MAX_RETRIES) {
                    try {
                        Thread.sleep(1000L * attempt);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        }

        System.err.println(
                "[AudioService] Definitive failure after "
                        + MAX_RETRIES
                        + " attempts for: "
                        + query
        );
        return null;
    }

    // ===============================
    // PLAYER MANAGEMENT
    // ===============================

    public boolean loadFromURL(URL url) {
        if (url == null) return false;

        cleanupMediaPlayer();

        try {
            Media media = new Media(url.toExternalForm());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(settings.getDefaultVolume());

            mediaPlayer.statusProperty().addListener((obs, o, n) -> {
                if (n == MediaPlayer.Status.READY && shouldPlayWhenReady) {
                    shouldPlayWhenReady = false;
                    mediaPlayer.play();
                }
            });

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public void loadWithFallback(String query) {
        URL preview = fetchPreviewFromDeezer(query);
        boolean ok = false;

        if (preview != null) {
            ok = loadFromURL(preview);
        }

        if (!ok) {
            loadLocalFallback();
        }
    }

    public boolean loadLocalFallback() {
        cleanupMediaPlayer();
        try {
            File file = new File("data/fallback.mp3");
            if (!file.exists()) return false;

            Media media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(settings.getDefaultVolume());
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    private void cleanupMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }

    // ===============================
    // CONTROLS
    // ===============================

    public void play() {
        if (mediaPlayer != null) {
            javafx.application.Platform.runLater(() -> {
                MediaPlayer.Status status = mediaPlayer.getStatus();
                if (status == MediaPlayer.Status.READY
                        || status == MediaPlayer.Status.PAUSED
                        || status == MediaPlayer.Status.STOPPED) {
                    mediaPlayer.play();
                } else {
                    shouldPlayWhenReady = true;
                }
            });
        }
    }

    public void pause() {
        if (mediaPlayer != null) {
            javafx.application.Platform.runLater(mediaPlayer::pause);
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            javafx.application.Platform.runLater(mediaPlayer::stop);
        }
    }

    public void setVolume(double volume) {
        if (mediaPlayer != null) {
            javafx.application.Platform.runLater(() -> mediaPlayer.setVolume(volume));
        }
    }

    // ===============================
    // SOUND EFFECTS
    // ===============================

    public void playSfxVictory() { 
    if (sfxVictory != null) {
        sfxVictory.setVolume(settings.getDefaultVolume()); // Applique le volume actuel
        sfxVictory.play();
        }
    }
    public void playSfxFail() { 
    if (sfxFail != null) {
        sfxFail.setVolume(settings.getDefaultVolume()); // Applique le volume actuel
        sfxFail.play();
        }
    }
    public void playClick() { 
    if (sfxBtnClick != null) {
        sfxBtnClick.setVolume(settings.getDefaultVolume()); // Applique le volume actuel
        sfxBtnClick.play();
        }   
    }

    // Gardez ces deux là pour que le GameController ne plante pas
    public void playCorrectSound() { playSfxVictory(); }
    public void playWrongSound() { playSfxFail(); }

    public void startMenuMusic() {
        if (menuMusicPlayer != null) {
            menuMusicPlayer.setVolume(settings.getDefaultVolume());
            menuMusicPlayer.play();
        }
    }

    public void stopMenuMusic() {
        if (menuMusicPlayer != null) menuMusicPlayer.stop();
    }

    public void setGlobalVolume(double volume) {
        settings.setDefaultVolume((float)volume); // Sauvegarde
        
        // Baisse le son du Blindtest (jeu)
        if (this.mediaPlayer != null) {
            this.mediaPlayer.setVolume(volume);
            }
        
        // Baisse le son du Menu / Fin de partie
        if (this.menuMusicPlayer != null) {
            this.menuMusicPlayer.setVolume(volume);
            }
        }
}