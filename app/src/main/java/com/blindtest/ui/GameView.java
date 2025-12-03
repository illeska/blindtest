package com.blindtest.ui;

import com.blindtest.controller.GameController;
import com.blindtest.model.Player;
import com.blindtest.model.Round;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.Arrays;
import java.util.List;

/**
 * Vue pour le jeu BlindTest : affiche le timer, champs de saisie, bouton soumettre et feedback.
 */
public class GameView {

    private final GameController gameController;
    private final Stage stage;
    private Scene scene;
    
    // Éléments UI
    private Label timerLabel;
    private Label feedbackLabel; // Nouveau label pour le retour utilisateur
    private TextField titleField;
    private TextField artistField;
    private Button submitButton;
    
    private Timeline timer;
    private long startTime;
    private int playerIndex = 0; // Mode solo par défaut

    public GameView(Stage stage) {
        this.stage = stage;
        // Créer un joueur par défaut pour mode solo
        List<Player> players = Arrays.asList(new Player("Joueur 1"));
        this.gameController = new GameController(players);
        initializeUI();
    }

    private void initializeUI() {
        timerLabel = new Label("Prêt ?");
        timerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Feedback Label (caché au début)
        feedbackLabel = new Label("");
        feedbackLabel.setStyle("-fx-font-size: 14px;");

        titleField = new TextField();
        titleField.setPromptText("Titre de la chanson");
        
        artistField = new TextField();
        artistField.setPromptText("Artiste");
        
        submitButton = new Button("Soumettre");
        submitButton.setOnAction(e -> submitAnswer());
        submitButton.setDefaultButton(true); // Permet de valider avec "Entrée"

        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-padding: 20px;");
        layout.getChildren().addAll(timerLabel, feedbackLabel, titleField, artistField, submitButton);

        scene = new Scene(layout, 400, 350);
    }

    public void startGame() {
        gameController.startGame();
        updateUIForRound();
    }

    private void startTimer() {
        int duration = gameController.getSettings().getExtractDuration();
        timerLabel.setText("Temps restant: " + duration + "s");
        startTime = System.currentTimeMillis();

        if (timer != null) timer.stop();
        
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            long elapsed = (System.currentTimeMillis() - startTime) / 1000;
            long remaining = duration - elapsed;
            timerLabel.setText("Temps restant: " + remaining + "s");
            
            // Changement de couleur quand il reste peu de temps
            if (remaining <= 5) {
                timerLabel.setStyle("-fx-text-fill: red; -fx-font-size: 18px; -fx-font-weight: bold;");
            } else {
                timerLabel.setStyle("-fx-text-fill: black; -fx-font-size: 18px; -fx-font-weight: bold;");
            }

            if (remaining <= 0) {
                timer.stop();
                submitAnswer(); // Auto-soumettre quand timer expire
            }
        }));
        timer.setCycleCount(duration + 1); // +1 pour afficher 0s
        timer.play();
    }

    private void submitAnswer() {
        if (timer != null) timer.stop();
        
        String title = titleField.getText();
        String artist = artistField.getText();
        long timeElapsed = (System.currentTimeMillis() - startTime) / 1000;
        
        // 1. Capturer le score avant
        Player currentPlayer = gameController.getPlayers().get(playerIndex);
        int scoreBefore = currentPlayer.getScore();

        // 2. Vérifier la réponse
        gameController.checkAnswer(title, artist, timeElapsed, playerIndex);

        // 3. Capturer le score après pour calculer le gain
        int scoreAfter = currentPlayer.getScore();
        int pointsGained = scoreAfter - scoreBefore;

        // 4. Mettre à jour le feedback
        updateFeedback(pointsGained);

        // 5. Suite du jeu
        if (gameController.getCurrentRoundIndex() >= gameController.getNumberOfRounds()) {
            showEndGame();
        } else {
            // Petit délai pour laisser l'utilisateur voir le feedback avant de passer au suivant ? 
            // Pour l'instant on passe direct, mais on garde le feedback affiché
            updateUIForRound();
        }
    }

    private void updateFeedback(int points) {
        if (points > 0) {
            feedbackLabel.setText("Bravo ! +" + points + " points");
            feedbackLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold; -fx-font-size: 14px;");
        } else {
            feedbackLabel.setText("Raté ! C'était : " + 
                gameController.getCurrentRound().getTrack().getTitle() + " - " + 
                gameController.getCurrentRound().getTrack().getArtist());
            feedbackLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 14px;");
        }
    }

    private void updateUIForRound() {
        Round round = gameController.getCurrentRound();
        if (round != null && round.getTrack() != null) {
            titleField.clear();
            artistField.clear();
            titleField.requestFocus(); // Focus sur le champ titre
            startTimer();
        }
    }

    private void showEndGame() {
        Label endLabel = new Label("Partie terminée !");
        endLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        Label scoreLabel = new Label("Score final : " + gameController.getPlayers().get(0).getScore());
        
        Button backButton = new Button("Retour au menu");
        backButton.setOnAction(e -> stage.setScene(new MainMenu().getScene(stage)));

        VBox endLayout = new VBox(20);
        endLayout.setAlignment(Pos.CENTER);
        endLayout.getChildren().addAll(endLabel, scoreLabel, backButton);
        
        scene = new Scene(endLayout, 400, 300);
        stage.setScene(scene);
    }

    public Scene getScene() {
        return scene;
    }
}