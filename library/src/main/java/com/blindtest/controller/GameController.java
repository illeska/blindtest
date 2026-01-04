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
import com.blindtest.util.InputValidator;

public class GameController {

    private final AudioService audioService = new AudioService();
    private final Settings settings;
    private Playlist activePlaylist; 
    
    private final List<Round> rounds = new ArrayList<>();
    private final List<Player> players = new ArrayList<>();
    private final List<Track> playedTracks = new ArrayList<>();
    private final Random random = new Random(); 
    
    private int currentPlayerIndex = 0; // Index du joueur dont c'est le tour
    private boolean isDuelMode = false;
    
    private int totalCorrectTitles = 0;
    private int totalCorrectArtists = 0;
    private int totalHintsUsed = 0;

    
    private int currentRoundIndex = -1;
    private boolean started = false;

    /**
     * Crée un contrôleur de jeu avec une liste de joueurs.
     * @param players La liste des joueurs
     * @throws IllegalArgumentException si la liste de joueurs est vide ou nulle
     */
    public GameController(List<Player> players) {
        if (players == null || players.isEmpty()) {
            throw new IllegalArgumentException("At least one player required");
        }

        this.settings = SettingsService.loadSettings();
        int numberOfRounds = this.settings.getNumberOfRounds();
        String genre = this.settings.getDefaultGenre();
        
        // Détection du mode Duel
        this.isDuelMode = (players.size() > 1);

        System.out.println("[GameController] Generation de la playlist pour le genre: " + genre);
        
        this.activePlaylist = DynamicPlaylistGenerator.generatePlaylist(genre, numberOfRounds * (isDuelMode ? 2 : 1));
        
        if (this.activePlaylist == null || this.activePlaylist.getTracks().isEmpty()) {
            System.err.println("ERREUR: Impossible de generer la playlist. Utilisation fallback.");
            this.activePlaylist = createFallbackPlaylist(); 
        } else {
            System.out.println("✓ Playlist chargee avec " + this.activePlaylist.getTracks().size() + " morceaux du genre '" + genre + "'");
        }

        this.players.addAll(players);
        
        // En mode Duel, on a deux fois plus de rounds (un par joueur)
        int totalRounds = isDuelMode ? (numberOfRounds * 2) : numberOfRounds;
        for (int i = 0; i < totalRounds; i++) {
            rounds.add(new Round());
        }
    }


    /**
     * Crée une playlist de secours avec des morceaux par défaut.
     * @return Une playlist de fallback
     */
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


    /**
     * Démarre la partie en initialisant les manches.
     */
    public void startGame() {
        if (started) return;
        started = true;
        currentRoundIndex = -1;
        currentPlayerIndex = 0;
        this.playedTracks.clear();
        this.totalCorrectTitles = 0;
        this.totalCorrectArtists = 0;
        this.totalHintsUsed = 0;
        nextRound();
    }
    
        
    /**
     * Classe interne représentant le résultat d'une vérification de réponse.
     */
    public static class RoundResult {
        public final boolean isTitleCorrect;
        public final boolean isArtistCorrect;
        public final int points;
        public final boolean isRoundOver;

        /**
         * Crée un résultat de manche.
         * @param t Titre correct
         * @param a Artiste correct
         * @param p Points obtenus
         * @param over Manche terminée
         */
        public RoundResult(boolean t, boolean a, int p, boolean over) {
            this.isTitleCorrect = t;
            this.isArtistCorrect = a;
            this.points = p;
            this.isRoundOver = over;
        }
    }

    /**
     * Vérifie la réponse d'un joueur et calcule les points.
     * @param trackTitle Le titre proposé
     * @param artistName L'artiste proposé
     * @param timeElapsed Le temps écoulé depuis le début de la manche
     * @param playerIndex L'index du joueur qui répond
     * @return Un objet RoundResult contenant les résultats de la vérification
     */
    public RoundResult checkAnswer(String trackTitle, String artistName, long timeElapsed, int playerIndex) {
        if (!started) return new RoundResult(false, false, 0, false);
        
        // En mode Duel, vérifier que c'est bien le tour du bon joueur
        if (isDuelMode && playerIndex != currentPlayerIndex) {
            return new RoundResult(false, false, 0, false);
        }
        
        Round currentRound = getCurrentRound();
        Player currentPlayer = players.get(playerIndex);

        // Normalisation des réponses avec InputValidator
        String correctTitle = InputValidator.normalizeAnswer(currentRound.getTrack().getTitle());
        String correctArtist = InputValidator.normalizeAnswer(currentRound.getTrack().getArtist());
        String submittedTitle = InputValidator.normalizeAnswer(trackTitle);
        String submittedArtist = InputValidator.normalizeAnswer(artistName);

        boolean titleCorrect = submittedTitle.equals(correctTitle);
        boolean artistCorrect = submittedArtist.equals(correctArtist);
        
        // Statistiques
        if (titleCorrect) totalCorrectTitles++;
        if (artistCorrect) totalCorrectArtists++;
        
        int points = 0;
        if (titleCorrect && artistCorrect) points = 2;
        else if (titleCorrect || artistCorrect) points = 1;

        if (settings.isSpeedBonusEnabled() && points > 0) {
            if (timeElapsed < (settings.getExtractDuration() / 2.0)) {
                points += 1;
            }
        }

        if (points > 0) {
            audioService.playCorrectSound();
        } else {
            audioService.playWrongSound();
        }

        currentPlayer.addScore(points);

        // En mode Duel, le round est terminé après la réponse du joueur actuel
        boolean isRoundOver = true; // Un joueur = un round

        return new RoundResult(titleCorrect, artistCorrect, points, isRoundOver);
    }


    /**
     * Demande un indice pour la manche en cours.
     * @return L'indice révélé ou null si les indices sont désactivés
     */
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
        
        totalHintsUsed++;
        
        return newHint;
    }

    /**
     * Révèle la prochaine lettre cachée dans un indice.
     * @param currentHint L'indice actuel avec des caractères masqués
     * @param correctValue La valeur correcte complète
     * @return L'indice avec une lettre supplémentaire révélée
     */
    private String revealNextHiddenLetter(String currentHint, String correctValue) {
        StringBuilder sb = new StringBuilder(currentHint);
        int index = sb.indexOf("*");
        if (index != -1) sb.setCharAt(index, correctValue.charAt(index));
        return sb.toString();
    }


    /**
     * Passe à la manche suivante et charge le nouveau morceau.
     * @throws IllegalStateException si la partie n'a pas été démarrée
     */
    public void nextRound() {
        if (!started) throw new IllegalStateException("Game not started");
        
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
            
            // En mode Duel, alterner les joueurs
            if (isDuelMode) {
                currentPlayerIndex = currentRoundIndex % players.size();
                System.out.println("🎵 Tour de " + players.get(currentPlayerIndex).getName() + " : " + newTrack.getArtist() + " - " + newTrack.getTitle());
            } else {
                System.out.println("🎵 Manche " + (currentRoundIndex + 1) + " : " + newTrack.getArtist() + " - " + newTrack.getTitle());
            }
            
            String query = newTrack.getArtist() + " " + newTrack.getTitle();
            audioService.loadWithFallback(query); 
            audioService.play(); 
        } else {
            endGame();
        }
    }

    /**
     * Sélectionne aléatoirement un morceau non encore joué.
     * @return Un morceau aléatoire ou null si aucun disponible
     */
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


    /**
     * Termine la partie et sauvegarde les scores.
     */
    private void endGame() {
        audioService.stop();
        
        // Sauvegarde avec statistiques enrichies
        String mode = isDuelMode ? "Duel" : "Solo";
        String genre = settings.getDefaultGenre();
        int totalTracksPlayed = isDuelMode ? (rounds.size() / 2) : rounds.size();
        
        for (Player player : players) {
            Score score = new Score(
                player.getName(),
                player.getScore(),
                mode,
                genre,
                totalTracksPlayed,
                totalCorrectTitles,
                totalCorrectArtists,
                totalHintsUsed
            );
            ScoreService.saveScore(score);
        }
        
        this.started = false; 
    }
    

    /**
     * Indique si la partie est en mode Duel.
     * @return true si mode Duel
     */
    public boolean isDuelMode() { return isDuelMode; }

    /**
     * Retourne l'index du joueur actuel.
     * @return L'index du joueur dont c'est le tour
     */
    public int getCurrentPlayerIndex() { return currentPlayerIndex; }
    
    /**
     * Retourne le joueur dont c'est actuellement le tour.
     * @return Le joueur actuel
     */
    public Player getCurrentPlayer() { 
        return isDuelMode ? players.get(currentPlayerIndex) : players.get(0); 
    }
    

    public Settings getSettings() { return settings; }
    public boolean isStarted() { return started; }
    public int getCurrentRoundIndex() { return currentRoundIndex; }
    public int getNumberOfRounds() { return isDuelMode ? (rounds.size() / 2) : rounds.size(); }
    public Round getCurrentRound() {
        if (currentRoundIndex >= 0 && currentRoundIndex < rounds.size()) return rounds.get(currentRoundIndex);
        return null;
    }
    public List<Player> getPlayers() { return players; }
}