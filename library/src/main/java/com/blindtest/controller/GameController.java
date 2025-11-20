package com.blindtest.controller;

import com.blindtest.model.Player;
import com.blindtest.model.Round;
import com.blindtest.model.Score;
import com.blindtest.model.Settings;
import com.blindtest.model.Track;
import com.blindtest.service.AudioService;
import com.blindtest.service.Playlist; // Classe Playlist dans le package service
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
    private Playlist activePlaylist; // Playlist actuellement utilisée
    
    private final List<Round> rounds = new ArrayList<>();
    private final List<Player> players = new ArrayList<>();
    
    private int currentRoundIndex = -1;
    private boolean started = false;

    /**
     * Constructeur pour initialiser le contrôleur de jeu.
     * Charge les paramètres et la playlist par défaut.
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

        // 2. Charger la playlist par défaut
        // NOTE: Assurez-vous que le fichier "data/default_playlist.json" existe !
        this.activePlaylist = playlistService.loadPlaylist("data/default_playlist.json"); 
        
        if (this.activePlaylist == null || this.activePlaylist.getTracks().isEmpty()) {
            System.err.println("ERREUR: La playlist par défaut n'a pas pu être chargée ou est vide. Utilisation d'une playlist de secours.");
            this.activePlaylist = createFallbackPlaylist(); 
        }

        this.players.addAll(players);
        for (int i = 0; i < numberOfRounds; i++) {
            // Un Round est créé vide, le Track sera affecté dans nextRound()
            rounds.add(new Round());
        }
    }

    /**
     * Crée une playlist de secours en cas d'échec de chargement.
     */
    private Playlist createFallbackPlaylist() {
        Playlist fallback = new Playlist("Default Fallback");
        int duration = settings.getExtractDuration(); // Utiliser la durée des settings
        fallback.addTrack(new Track("The Final Countdown", "Europe", duration));
        fallback.addTrack(new Track("Take on Me", "A-Ha", duration));
        return fallback;
    }

    /**
     * Démarre la partie et lance la 1ère manche.
     */
    public void startGame() {
        if (started) return;
        started = true;
        currentRoundIndex = -1; // nextRound() lancera la manche 0
        nextRound();
    }
    
    /**
     * Vérifie la réponse du joueur, calcule le score et passe à la manche suivante.
     * Cette méthode doit être appelée par l'UI lorsque le joueur soumet sa réponse ou que le timer s'arrête.
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

        // 1. Définir la bonne réponse
        String correctTitle = currentRound.getTrack().getTitle().toLowerCase().trim();
        String correctArtist = currentRound.getTrack().getArtist().toLowerCase().trim();

        // 2. Préparer les réponses soumises
        String submittedTitle = trackTitle.toLowerCase().trim();
        String submittedArtist = artistName.toLowerCase().trim();

        int points = 0;
        boolean titleCorrect = submittedTitle.equals(correctTitle);
        boolean artistCorrect = submittedArtist.equals(correctArtist);

        // 3. Logique de base du scoring
        if (titleCorrect && artistCorrect) {
            points = 2; // +2 points pour titre et artiste corrects
        } else if (titleCorrect || artistCorrect) {
            points = 1; // +1 point si un seul est correct
        }

        // 4. Logique du bonus de vitesse (si activé)
        if (settings.isSpeedBonusEnabled() && points > 0) { 
            // Bonus si la réponse est soumise dans la première moitié du temps imparti.
            int duration = settings.getExtractDuration(); // Durée en secondes
            if (timeElapsed < (duration / 2.0)) {
                points += 1; // +1 point bonus si la réponse est rapide
                System.out.println("🔥 Bonus de vitesse activé pour " + players.get(playerIndex).getName() + "!");
            }
        }

        // 5. Mise à jour du score du joueur
        Player currentPlayer = players.get(playerIndex);
        currentPlayer.addScore(points); 

        System.out.println(currentPlayer.getName() + " a gagné " + points + " points. Score total: " + currentPlayer.getScore());

        // 6. Arrêt de l'extrait audio et passage à la manche suivante
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

            // 1. Sélection aléatoire réelle du Track
            Track newTrack = selectRandomTrack();
            currentRound.setTrack(newTrack); 
            
            // 2. Lancement de l'audio (Intégration AudioService)
            String query = currentRound.getTrack().getArtist() + " " + currentRound.getTrack().getTitle();
            audioService.loadWithFallback(query); 
            audioService.play(); 
            
            // TODO: L'UI doit démarrer son Timer ici (tâche Achraf)

            System.out.println("Manche " + (currentRoundIndex + 1) + " démarrée. Extrait: " + query);

        } else {
            // Fin de partie : sauvegarder les scores
            endGame();
        }
    }

    /**
     * Sélectionne aléatoirement un Track dans la playlist active.
     * @return Un Track aléatoire.
     */
    private Track selectRandomTrack() {
        List<Track> tracks = activePlaylist.getTracks();
        if (tracks.isEmpty()) {
            throw new IllegalStateException("La playlist active est vide. Impossible de démarrer une manche.");
        }
        
        // Sélection aléatoire simple
        int randomIndex = new Random().nextInt(tracks.size());
        return tracks.get(randomIndex);
    }


    /**
     * Termine la partie et sauvegarde les scores.
     */
    private void endGame() {

        // Arrêt de l'audio
        audioService.stop(); 

        System.out.println("Partie terminée.");
        for (Player player : players) {
            Score score = new Score(player.getName(), player.getScore()); //
            ScoreService.saveScore(score); //
            System.out.println("Score sauvegardé pour " + player.getName() + ": " + player.getScore());
        }
    }

    /**
     * Retourne la configuration actuelle du jeu.
     * @return Les settings du jeu.
     */
    public Settings getSettings() {
        return settings;
    }

    /**
     * Vérifie si la partie a démarré.
     * @return true si démarrée, false sinon
     */
    public boolean isStarted() { return started; }

    /**
     * Retourne l'index de la manche actuelle.
     * @return L'index de la manche actuelle
     */
    public int getCurrentRoundIndex() { return currentRoundIndex; }

    /**
     * Retourne le nombre total de manches.
     * @return Le nombre de manches
     */
    public int getNumberOfRounds() { return rounds.size(); }

    /**
     * Retourne la manche actuelle.
     * @return La manche actuelle ou null si aucune
     */
    public Round getCurrentRound() {
        if (currentRoundIndex >= 0 && currentRoundIndex < rounds.size()) {
            return rounds.get(currentRoundIndex);
        }
        return null;
    }

    /**
     * Retourne la liste des joueurs.
     * @return La liste des joueurs
     */
    public List<Player> getPlayers() { return players; }
}