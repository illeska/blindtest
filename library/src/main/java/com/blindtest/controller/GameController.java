package com.blindtest.controller;

import com.blindtest.model.Player;
import com.blindtest.model.Playlist;
import com.blindtest.model.Round;
import com.blindtest.model.Score;
import com.blindtest.model.Settings;
import com.blindtest.model.Track;
import com.blindtest.service.AudioService;
import com.blindtest.service.PlaylistService;
import com.blindtest.service.ScoreService;
import com.blindtest.service.SettingsService;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Contrôleur principal de partie.
 * Intègre la logique de jeu (scoring), la gestion de l'audio et la persistance.
 */
public class GameController {

    private final AudioService audioService = new AudioService();
    private final Settings settings;
    private final PlaylistService playlistService = new PlaylistService();
    private Playlist activePlaylist;
    
    private final List<Round> rounds = new ArrayList<>();
    private final List<Player> players = new ArrayList<>();
    
    private int currentRoundIndex = -1;
    private boolean started = false;
    private List<Track> usedTracks = new ArrayList<>();

    /**
     * Constructeur pour initialiser le contrôleur de jeu.
     * Génère une playlist depuis l'API en fonction du genre sélectionné.
     * @param players liste des joueurs (mode solo ou duel)
     */
    public GameController(List<Player> players) {
        if (players == null || players.isEmpty()) {
            throw new IllegalArgumentException("At least one player required");
        }

        // 1. Charger les paramètres de jeu
        this.settings = SettingsService.loadSettings();
        int numberOfRounds = this.settings.getNumberOfRounds();

        if (numberOfRounds <= 0) {
            throw new IllegalArgumentException("numberOfRounds must be > 0 (loaded from settings)");
        }

        // 2. Générer une playlist depuis l'API en fonction du genre sélectionné
        String selectedGenre = this.settings.getDefaultGenre();
        System.out.println("🎵 Génération d'une playlist pour le genre: " + selectedGenre);
        
        // Générer une playlist avec plus de morceaux que nécessaire (pour éviter les répétitions)
        int playlistSize = Math.max(numberOfRounds * 2, 20);
        this.activePlaylist = playlistService.generatePlaylistFromAPI(selectedGenre, playlistSize);
        
        // Si la génération échoue, essayer de charger une playlist locale
        if (this.activePlaylist == null || this.activePlaylist.getTracks().isEmpty()) {
            System.err.println("⚠️ Échec de génération depuis l'API, tentative de chargement local...");
            String playlistPath = "data/" + selectedGenre.toLowerCase() + "_playlist.json";
            this.activePlaylist = playlistService.loadPlaylist(playlistPath);
        }
        
        // Si même la playlist locale échoue, charger la playlist par défaut
        if (this.activePlaylist == null || this.activePlaylist.getTracks().isEmpty()) {
            System.err.println("⚠️ Échec du chargement local, tentative playlist par défaut...");
            this.activePlaylist = playlistService.loadPlaylist("data/default_playlist.json");
        }
        
        // En dernier recours, utiliser le fallback
        if (this.activePlaylist == null || this.activePlaylist.getTracks().isEmpty()) {
            System.err.println("❌ Aucune playlist disponible. Utilisation du fallback.");
            this.activePlaylist = createFallbackPlaylist();
        }

        System.out.println("✅ Playlist active: " + this.activePlaylist.getName() + 
                         " (" + this.activePlaylist.getTracks().size() + " morceaux)");

        this.players.addAll(players);
        for (int i = 0; i < numberOfRounds; i++) {
            rounds.add(new Round());
        }
    }

    /**
     * Crée une playlist de secours en cas d'échec de chargement.
     */
    private Playlist createFallbackPlaylist() {
        Playlist fallback = new Playlist("Default Fallback");
        int duration = settings.getExtractDuration();
        fallback.addTrack(new Track("The Final Countdown", "Europe", duration));
        fallback.addTrack(new Track("Take on Me", "A-Ha", duration));
        fallback.addTrack(new Track("Bohemian Rhapsody", "Queen", duration));
        fallback.addTrack(new Track("Billie Jean", "Michael Jackson", duration));
        fallback.addTrack(new Track("Hotel California", "Eagles", duration));
        fallback.addTrack(new Track("Sweet Child O' Mine", "Guns N' Roses", duration));
        fallback.addTrack(new Track("Smells Like Teen Spirit", "Nirvana", duration));
        fallback.addTrack(new Track("Wonderwall", "Oasis", duration));
        fallback.addTrack(new Track("Stairway to Heaven", "Led Zeppelin", duration));
        fallback.addTrack(new Track("Imagine", "John Lennon", duration));
        return fallback;
    }

    /**
     * Démarre la partie et lance la 1ère manche.
     */
    public void startGame() {
        if (started) return;
        started = true;
        currentRoundIndex = -1;
        nextRound();
    }
    
    /**
     * Vérifie la réponse du joueur, calcule le score et passe à la manche suivante.
     * @param trackTitle La réponse du titre soumise par le joueur.
     * @param artistName La réponse de l'artiste soumise par le joueur.
     * @param timeElapsed Temps écoulé depuis le début de la manche (en secondes).
     * @param playerIndex L'index du joueur.
     */
    public void checkAnswer(String trackTitle, String artistName, long timeElapsed, int playerIndex) {
        if (!started) return;
        if (currentRoundIndex < 0 || currentRoundIndex >= rounds.size()) return;

        Round currentRound = getCurrentRound();
        if (currentRound == null || currentRound.getTrack() == null) return;

        String correctTitle = currentRound.getTrack().getTitle().toLowerCase().trim();
        String correctArtist = currentRound.getTrack().getArtist().toLowerCase().trim();

        String submittedTitle = trackTitle.toLowerCase().trim();
        String submittedArtist = artistName.toLowerCase().trim();

        int points = 0;
        boolean titleCorrect = submittedTitle.equals(correctTitle);
        boolean artistCorrect = submittedArtist.equals(correctArtist);

        if (titleCorrect && artistCorrect) {
            points = 2;
        } else if (titleCorrect || artistCorrect) {
            points = 1;
        }

        if (settings.isSpeedBonusEnabled() && points > 0) {
            int duration = settings.getExtractDuration();
            if (timeElapsed < (duration / 2.0)) {
                points += 1;
                System.out.println("🔥 Bonus de vitesse activé pour " + players.get(playerIndex).getName() + "!");
            }
        }

        Player currentPlayer = players.get(playerIndex);
        currentPlayer.addScore(points);

        System.out.println(currentPlayer.getName() + " a gagné " + points + " points. Score total: " + currentPlayer.getScore());

        audioService.stop();
        nextRound();
    }

    /**
     * Passe à la manche suivante ou termine la partie si toutes les manches sont jouées.
     */
    public void nextRound() {
        if (!started) {
            throw new IllegalStateException("Game not started");
        }
        
        currentRoundIndex++;

        if (currentRoundIndex < rounds.size()) {
            Round currentRound = getCurrentRound();

            Track newTrack = selectRandomTrack();
            currentRound.setTrack(newTrack);
            
            String query = currentRound.getTrack().getArtist() + " " + currentRound.getTrack().getTitle();
            audioService.loadWithFallback(query);
            audioService.play();

            System.out.println("🎵 Manche " + (currentRoundIndex + 1) + "/" + rounds.size() + 
                             " - Extrait: " + query);

        } else {
            endGame();
        }
    }

    /**
     * Sélectionne aléatoirement un Track dans la playlist active.
     * Évite de sélectionner deux fois la même chanson dans une partie.
     * @return Un Track aléatoire.
     */
    private Track selectRandomTrack() {
        List<Track> tracks = activePlaylist.getTracks();
        if (tracks.isEmpty()) {
            throw new IllegalStateException("La playlist active est vide. Impossible de démarrer une manche.");
        }
        
        // Créer une liste des tracks non encore utilisés
        List<Track> availableTracks = new ArrayList<>();
        for (Track track : tracks) {
            boolean alreadyUsed = false;
            for (Track used : usedTracks) {
                if (used.getTitle().equals(track.getTitle()) && used.getArtist().equals(track.getArtist())) {
                    alreadyUsed = true;
                    break;
                }
            }
            if (!alreadyUsed) {
                availableTracks.add(track);
            }
        }
        
        // Si tous les tracks ont été utilisés, réinitialiser
        if (availableTracks.isEmpty()) {
            System.out.println("♻️ Toutes les chansons ont été jouées, réinitialisation...");
            usedTracks.clear();
            availableTracks.addAll(tracks);
        }
        
        // Sélection aléatoire
        int randomIndex = new Random().nextInt(availableTracks.size());
        Track selectedTrack = availableTracks.get(randomIndex);
        usedTracks.add(selectedTrack);
        
        return selectedTrack;
    }

    /**
     * Termine la partie et sauvegarde les scores.
     */
    private void endGame() {
        audioService.stop();

        System.out.println("🎉 Partie terminée !");
        for (Player player : players) {
            Score score = new Score(player.getName(), player.getScore());
            ScoreService.saveScore(score);
            System.out.println("💾 Score sauvegardé pour " + player.getName() + ": " + player.getScore() + " points");
        }
    }

    public Settings getSettings() {
        return settings;
    }

    public boolean isStarted() {
        return started;
    }

    public int getCurrentRoundIndex() {
        return currentRoundIndex;
    }

    public int getNumberOfRounds() {
        return rounds.size();
    }

    public Round getCurrentRound() {
        if (currentRoundIndex >= 0 && currentRoundIndex < rounds.size()) {
            return rounds.get(currentRoundIndex);
        }
        return null;
    }

    public List<Player> getPlayers() {
        return players;
    }
}