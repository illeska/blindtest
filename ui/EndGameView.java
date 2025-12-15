package com.blindtest.ui;

import com.blindtest.controller.GameController;
import com.blindtest.model.Player;
import com.blindtest.model.Score;
import com.blindtest.service.ScoreService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.List;

/**
 * Écran de fin de partie avec résultats détaillés et options de navigation.
 * Affiche le gagnant, les scores finaux et permet de revenir au menu ou voir le classement.
 */
public class EndGameView {
    
    private final Stage stage;
    private final GameController controller;
    private final Scene scene;

    public EndGameView(Stage stage, GameController controller) {
        this.stage = stage;
        this.controller = controller;
        this.scene = createScene();
    }

    private Scene createScene() {
        VBox mainLayout = new VBox(25);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(40));
        mainLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #667eea 0%, #764ba2 100%);");

        // Titre
        Label titleLabel = new Label("🎉 PARTIE TERMINÉE ! 🎉");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titleLabel.setStyle("-fx-text-fill: white;");

        // Résultats
        VBox resultsBox = createResultsBox();

        // Statistiques rapides
        VBox statsBox = createQuickStatsBox();

        // Boutons d'action
        VBox buttonsBox = createButtonsBox();

        mainLayout.getChildren().addAll(titleLabel, resultsBox, statsBox, buttonsBox);

        return new Scene(mainLayout, 600, 700);
    }

    /**
     * Crée la section des résultats avec les scores finaux.
     */
    private VBox createResultsBox() {
        VBox resultsBox = new VBox(15);
        resultsBox.setAlignment(Pos.CENTER);
        resultsBox.setPadding(new Insets(20));
        resultsBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-background-radius: 15;");

        List<Player> players = controller.getPlayers();
        
        if (players.size() == 1) {
            // Mode Solo
            Player player = players.get(0);
            
            Label winnerLabel = new Label("👤 " + player.getName());
            winnerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
            
            Label scoreLabel = new Label("Score Final : " + player.getScore() + " points");
            scoreLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
            scoreLabel.setStyle("-fx-text-fill: #4CAF50;");

            resultsBox.getChildren().addAll(winnerLabel, scoreLabel);
            
            // Sauvegarde du score
            saveScore(player);
            
        } else {
            // Mode Duel
            Player player1 = players.get(0);
            Player player2 = players.get(1);
            
            Player winner = player1.getScore() >= player2.getScore() ? player1 : player2;
            Player loser = winner == player1 ? player2 : player1;
            
            Label winnerTitleLabel = new Label("🏆 VAINQUEUR");
            winnerTitleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            winnerTitleLabel.setStyle("-fx-text-fill: #FFD700;");
            
            Label winnerLabel = new Label(winner.getName());
            winnerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
            
            Label winnerScoreLabel = new Label(winner.getScore() + " points");
            winnerScoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
            winnerScoreLabel.setStyle("-fx-text-fill: #4CAF50;");
            
            Label vsLabel = new Label("VS");
            vsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
            vsLabel.setStyle("-fx-text-fill: #888;");
            
            Label loserLabel = new Label(loser.getName() + " : " + loser.getScore() + " points");
            loserLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
            loserLabel.setStyle("-fx-text-fill: #666;");

            resultsBox.getChildren().addAll(
                winnerTitleLabel, 
                winnerLabel, 
                winnerScoreLabel, 
                vsLabel, 
                loserLabel
            );
            
            // Sauvegarde des deux scores
            saveScore(player1);
            saveScore(player2);
        }

        return resultsBox;
    }

    /**
     * Crée la section des statistiques rapides de la partie.
     */
    private VBox createQuickStatsBox() {
        VBox statsBox = new VBox(10);
        statsBox.setAlignment(Pos.CENTER);
        statsBox.setPadding(new Insets(15));
        statsBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.85); -fx-background-radius: 10;");

        Label statsTitle = new Label("📊 Statistiques de la partie");
        statsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        int totalRounds = controller.getNumberOfRounds();
        String mode = controller.getPlayers().size() > 1 ? "Duel" : "Solo";
        String genre = controller.getSettings().getDefaultGenre() != null ? 
                      controller.getSettings().getDefaultGenre() : "Mixed";

        Label roundsLabel = new Label("Manches jouées : " + totalRounds);
        roundsLabel.setFont(Font.font("Arial", 14));
        
        Label modeLabel = new Label("Mode : " + mode);
        modeLabel.setFont(Font.font("Arial", 14));
        
        Label genreLabel = new Label("Genre : " + genre);
        genreLabel.setFont(Font.font("Arial", 14));

        statsBox.getChildren().addAll(statsTitle, roundsLabel, modeLabel, genreLabel);

        return statsBox;
    }

    /**
     * Crée la section des boutons d'action.
     */
    private VBox createButtonsBox() {
        VBox buttonsBox = new VBox(15);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(10));

        // Bouton Voir le Classement
        Button leaderboardButton = new Button("🏆 Voir le Classement");
        leaderboardButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        leaderboardButton.setStyle(
            "-fx-background-color: #4CAF50; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 15px 40px; " +
            "-fx-background-radius: 10; " +
            "-fx-cursor: hand;"
        );
        leaderboardButton.setOnMouseEntered(e -> 
            leaderboardButton.setStyle(
                "-fx-background-color: #45a049; " +
                "-fx-text-fill: white; " +
                "-fx-padding: 15px 40px; " +
                "-fx-background-radius: 10; " +
                "-fx-cursor: hand;"
            )
        );
        leaderboardButton.setOnMouseExited(e -> 
            leaderboardButton.setStyle(
                "-fx-background-color: #4CAF50; " +
                "-fx-text-fill: white; " +
                "-fx-padding: 15px 40px; " +
                "-fx-background-radius: 10; " +
                "-fx-cursor: hand;"
            )
        );
        leaderboardButton.setOnAction(e -> showLeaderboard());

        // Bouton Rejouer
        Button replayButton = new Button("🔄 Rejouer");
        replayButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        replayButton.setStyle(
            "-fx-background-color: #2196F3; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 15px 40px; " +
            "-fx-background-radius: 10; " +
            "-fx-cursor: hand;"
        );
        replayButton.setOnMouseEntered(e -> 
            replayButton.setStyle(
                "-fx-background-color: #1976D2; " +
                "-fx-text-fill: white; " +
                "-fx-padding: 15px 40px; " +
                "-fx-background-radius: 10; " +
                "-fx-cursor: hand;"
            )
        );
        replayButton.setOnMouseExited(e -> 
            replayButton.setStyle(
                "-fx-background-color: #2196F3; " +
                "-fx-text-fill: white; " +
                "-fx-padding: 15px 40px; " +
                "-fx-background-radius: 10; " +
                "-fx-cursor: hand;"
            )
        );
        replayButton.setOnAction(e -> replay());

        // Bouton Menu Principal
        Button menuButton = new Button("🏠 Menu Principal");
        menuButton.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        menuButton.setStyle(
            "-fx-background-color: #757575; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 15px 40px; " +
            "-fx-background-radius: 10; " +
            "-fx-cursor: hand;"
        );
        menuButton.setOnMouseEntered(e -> 
            menuButton.setStyle(
                "-fx-background-color: #616161; " +
                "-fx-text-fill: white; " +
                "-fx-padding: 15px 40px; " +
                "-fx-background-radius: 10; " +
                "-fx-cursor: hand;"
            )
        );
        menuButton.setOnMouseExited(e -> 
            menuButton.setStyle(
                "-fx-background-color: #757575; " +
                "-fx-text-fill: white; " +
                "-fx-padding: 15px 40px; " +
                "-fx-background-radius: 10; " +
                "-fx-cursor: hand;"
            )
        );
        menuButton.setOnAction(e -> backToMenu());

        buttonsBox.getChildren().addAll(leaderboardButton, replayButton, menuButton);

        return buttonsBox;
    }

    /**
     * Sauvegarde le score d'un joueur dans le système de persistance.
     */
    private void saveScore(Player player) {
        try {
            String mode = controller.getPlayers().size() > 1 ? "Duel" : "Solo";
            String genre = controller.getSettings().getDefaultGenre() != null ? 
                          controller.getSettings().getDefaultGenre() : "Mixed";
            
            // Calcul des statistiques de la partie
            int totalTracks = controller.getNumberOfRounds();
            // Note : Ces valeurs seraient idéalement récupérées du GameController
            // Pour l'instant, on utilise des valeurs estimées
            int correctTitles = player.getScore() / 100; // Estimation basée sur le scoring
            int correctArtists = player.getScore() / 100;
            int hintsUsed = 0; // À tracker dans GameController
            
            Score score = new Score(
                player.getName(),
                player.getScore(),
                mode,
                genre,
                totalTracks,
                correctTitles,
                correctArtists,
                hintsUsed
            );
            
            ScoreService.saveScore(score);
            System.out.println("[EndGameView] Score sauvegardé pour " + player.getName());
            
        } catch (Exception e) {
            System.err.println("[EndGameView] Erreur lors de la sauvegarde du score : " + e.getMessage());
        }
    }

    /**
     * Affiche le classement.
     */
    private void showLeaderboard() {
        LeaderboardView leaderboardView = new LeaderboardView(stage);
        stage.setScene(leaderboardView.getScene());
    }

    /**
     * Relance une nouvelle partie avec les mêmes paramètres.
     */
    private void replay() {
        // Réinitialise le contrôleur et relance une partie
        // Note : Nécessite une méthode reset() dans GameController
        // Pour l'instant, on retourne au menu
        backToMenu();
    }

    /**
     * Retourne au menu principal.
     */
    private void backToMenu() {
        MainMenu mainMenu = new MainMenu();
        stage.setScene(mainMenu.getScene(stage));
    }

    public Scene getScene() {
        return scene;
    }
}