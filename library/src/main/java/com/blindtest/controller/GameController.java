package com.blindtest.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.blindtest.model.Player;
import com.blindtest.model.Playlist;
import com.blindtest.model.Round;
import com.blindtest.model.Score;
import com.blindtest.model.Settings;
import com.blindtest.model.Track;
import com.blindtest.service.AudioService;
import com.blindtest.service.DynamicPlaylistGenerator;
import com.blindtest.service.ScoreService;
import com.blindtest.service.SettingsService;

public class GameController {

    private final AudioService audioService = new AudioService();
    private final Settings settings;
    private Playlist activePlaylist; 
    
    private final List<Round> rounds = new ArrayList<>();
    private final List<Player> players = new ArrayList<>();
    private final List<Track> playedTracks = new ArrayList<>(); 
    private final Random random = new Random(); 
    
    private final Set<Player> answeredPlayers = new HashSet<>(); 
    
    private int currentRoundIndex = -1;
    private boolean started = false;

    public GameController(List<Player> players) {
        if (players == null || players.isEmpty()) {
            throw new IllegalArgumentException("At least one player required");
        }

        this.settings = SettingsService.loadSettings();
        int numberOfRounds = this.settings.getNumberOfRounds();
        String genre = this.settings.getDefaultGenre();

        System.out.println("[GameController] Generation de la playlist pour le genre: " + genre);
        
        // Génération dynamique de la playlist basée sur les settings
        this.activePlaylist = DynamicPlaylistGenerator.generatePlaylist(genre, numberOfRounds);
        
        if (this.activePlaylist == null || this.activePlaylist.getTracks().isEmpty()) {
            System.err.println("ERREUR: Impossible de generer la playlist. Utilisation fallback.");
            this.activePlaylist = createFallbackPlaylist(); 
        } else {
            System.out.println("✓ Playlist chargee avec " + this.activePlaylist.getTracks().size() + " morceaux du genre '" + genre + "'");
        }

        this.players.addAll(players);
        for (int i = 0; i < numberOfRounds; i++) {
            rounds.add(new Round());
        }
    }

    private Playlist createFallbackPlaylist() {
        Playlist fallback = new Playlist("Default Fallback");
        int duration = settings.getExtractDuration();
        fallback.addTrack(new Track("The Final Countdown", "Europe", duration));
        fallback.addTrack(new Track("Take on Me", "A-Ha", duration));
        fallback.addTrack(new Track("Billie Jean", "Michael Jackson", duration));
        fallback.addTrack(new Track("Bohemian Rhapsody", "Queen", duration));
        fallback.addTrack(new Track("Sweet Child O' Mine", "Guns N' Roses", duration));
        return fallback;
    }

    public void startGame() {
        if (started) return;
        started = true;
        currentRoundIndex = -1;
        this.playedTracks.clear();
        nextRound();
    }
    
    // Ajoutez cette classe interne simple tout en bas de GameController.java ou dans un fichier à part
public static class RoundResult {
    public final boolean isTitleCorrect;
    public final boolean isArtistCorrect;
    public final int points;
    public final boolean isRoundOver;

    public RoundResult(boolean t, boolean a, int p, boolean over) {
        this.isTitleCorrect = t;
        this.isArtistCorrect = a;
        this.points = p;
        this.isRoundOver = over;
    }
}

    // Remplacez la méthode checkAnswer existante par celle-ci
    public RoundResult checkAnswer(String trackTitle, String artistName, long timeElapsed, int playerIndex) {
        if (!started) return new RoundResult(false, false, 0, false);
        
        Round currentRound = getCurrentRound();
        Player currentPlayer = players.get(playerIndex);
        
        // Protection si le joueur a déjà répondu
        if (answeredPlayers.contains(currentPlayer)) {
            return new RoundResult(false, false, 0, false);
        }

        String correctTitle = currentRound.getTrack().getTitle().toLowerCase().trim();
        String correctArtist = currentRound.getTrack().getArtist().toLowerCase().trim();
        String submittedTitle = trackTitle != null ? trackTitle.toLowerCase().trim() : "";
        String submittedArtist = artistName != null ? artistName.toLowerCase().trim() : "";

        boolean titleCorrect = submittedTitle.equals(correctTitle);
        boolean artistCorrect = submittedArtist.equals(correctArtist);
        
        int points = 0;
        if (titleCorrect && artistCorrect) points = 2;
        else if (titleCorrect || artistCorrect) points = 1;

        // Bonus de vitesse
        if (settings.isSpeedBonusEnabled() && points > 0) {
            if (timeElapsed < (settings.getExtractDuration() / 2.0)) {
                points += 1;
            }
        }

        // Effets sonores (On garde les bruitages courts)
        if (points > 0) {
            audioService.playCorrectSound();
        } else {
            audioService.playWrongSound();
        }

        currentPlayer.addScore(points);
        answeredPlayers.add(currentPlayer);

        boolean isRoundOver = answeredPlayers.size() == players.size();

        // IMPORTANT : On a supprimé audioService.stop() et nextRound() ici !
        // On laisse la musique tourner.

        return new RoundResult(titleCorrect, artistCorrect, points, isRoundOver);
    }

    public String requestHint() {
        if (!settings.isHintsEnabled()) return null;
        
        Round currentRound = getCurrentRound();
        if (currentRound == null || currentRound.getTrack() == null) return null;

        boolean titleHidden = currentRound.getTitleHint().contains("*");
        boolean artistHidden = currentRound.getArtistHint().contains("*");
        
        if (!titleHidden && !artistHidden) return null;

        String hintType;
        if (titleHidden && artistHidden) hintType = random.nextBoolean() ? "title" : "artist";
        else if (titleHidden) hintType = "title";
        else hintType = "artist";

        Track correctTrack = currentRound.getTrack();
        String currentHint = "title".equals(hintType) ? currentRound.getTitleHint() : currentRound.getArtistHint();
        String correctValue = "title".equals(hintType) ? correctTrack.getTitle() : correctTrack.getArtist();

        String newHint = revealNextHiddenLetter(currentHint, correctValue);

        if ("title".equals(hintType)) currentRound.setTitleHint(newHint);
        else currentRound.setArtistHint(newHint);
        
        return newHint;
    }

    private String revealNextHiddenLetter(String currentHint, String correctValue) {
        StringBuilder sb = new StringBuilder(currentHint);
        int index = sb.indexOf("*");
        if (index != -1) sb.setCharAt(index, correctValue.charAt(index));
        return sb.toString();
    }

    public void nextRound() {
        if (!started) throw new IllegalStateException("Game not started");
        
        answeredPlayers.clear(); 
        currentRoundIndex++;

        if (currentRoundIndex < rounds.size()) {
            Round currentRound = getCurrentRound();
            Track newTrack = selectRandomTrack();
            
            if (newTrack == null) {
                endGame();
                return;
            }
            
            currentRound.setTrack(newTrack); 
            this.playedTracks.add(newTrack);
            
            System.out.println("🎵 Manche " + (currentRoundIndex + 1) + " : " + newTrack.getArtist() + " - " + newTrack.getTitle());
            
            String query = newTrack.getArtist() + " " + newTrack.getTitle();
            audioService.loadWithFallback(query); 
            audioService.play(); 
        } else {
            endGame();
        }
    }

    private Track selectRandomTrack() {
        List<Track> allTracks = activePlaylist.getTracks();
        List<Track> availableTracks = allTracks.stream()
            .filter(track -> !playedTracks.contains(track)) 
            .collect(Collectors.toList());

        if (availableTracks.isEmpty()) {
            if (allTracks.size() < rounds.size()) {
                this.playedTracks.clear(); 
                availableTracks = allTracks;
            } else {
                return null; 
            }
        }
        return availableTracks.get(random.nextInt(availableTracks.size()));
    }

    private void endGame() {
        audioService.stop(); 
        for (Player player : players) {
            ScoreService.saveScore(new Score(player.getName(), player.getScore())); 
        }
        this.started = false; 
    }
    
    public Settings getSettings() { return settings; }
    public boolean isStarted() { return started; }
    public int getCurrentRoundIndex() { return currentRoundIndex; }
    public int getNumberOfRounds() { return rounds.size(); }
    public Round getCurrentRound() {
        if (currentRoundIndex >= 0 && currentRoundIndex < rounds.size()) return rounds.get(currentRoundIndex);
        return null;
    }
    public List<Player> getPlayers() { return players; }
}