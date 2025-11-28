package com.blindtest.ui;

import com.blindtest.controller.GameController;
import com.blindtest.model.Player;
import com.blindtest.model.Round;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
 * Vue pour le jeu BlindTest : affiche le timer, champs de saisie pour titre et artiste, bouton soumettre.
 * Intègre avec GameController pour logique de jeu.
 */
public class GameView {

    private final GameController gameController;
    private final Stage stage;
    private Scene scene;
    private Label timerLabel;
    private TextField titleField;
    private TextField artistField;
    private Button submitButton;
    private Timeline timer;
    private long startTime;
    private int playerIndex = 0; // Pour mode solo, index 0

    public GameView(Stage stage) {
        this.stage = stage;
        // Créer un joueur par défaut pour mode solo
        List<Player> players = Arrays.asList(new Player("Joueur"));
        this.gameController = new GameController(players);
        initializeUI();
    }

    private void initializeUI() {
        timerLabel = new Label("Temps restant: " + gameController.getSettings().getExtractDuration() + "s");
        titleField = new TextField();
        titleField.setPromptText("Titre de la chanson");
        artistField = new TextField();
        artistField.setPromptText("Artiste");
        submitButton = new Button("Soumettre");

        submitButton.setOnAction(e -> submitAnswer());

        VBox layout = new VBox(10);
        layout.getChildren().addAll(timerLabel, titleField, artistField, submitButton);

        scene = new Scene(layout, 400, 300);
    }

    public void startGame() {
        gameController.startGame();
        startTimer();
        updateUIForRound();
    }

    private void startTimer() {
        int duration = gameController.getSettings().getExtractDuration();
        timerLabel.setText("Temps restant: " + duration + "s");
        startTime = System.currentTimeMillis();

        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            long elapsed = (System.currentTimeMillis() - startTime) / 1000;
            long remaining = duration - elapsed;
            timerLabel.setText("Temps restant: " + remaining + "s");
            if (remaining <= 0) {
                timer.stop();
                submitAnswer(); // Auto-soumettre quand timer expire
            }
        }));
        timer.setCycleCount(duration);
        timer.play();
    }

    private void submitAnswer() {
        if (timer != null) timer.stop();
        String title = titleField.getText();
        String artist = artistField.getText();
        long timeElapsed = (System.currentTimeMillis() - startTime) / 1000;
        gameController.checkAnswer(title, artist, timeElapsed, playerIndex);

        // Vérifier si partie terminée
        if (gameController.getCurrentRoundIndex() >= gameController.getNumberOfRounds()) {
            showEndGame();
        } else {
            updateUIForRound();
        }
    }

    private void updateUIForRound() {
        Round round = gameController.getCurrentRound();
        if (round != null && round.getTrack() != null) {
            titleField.clear();
            artistField.clear();
            startTimer();
        }
    }

    private void showEndGame() {
        Label endLabel = new Label("Partie terminée! Scores sauvegardés.");
        Button backButton = new Button("Retour au menu");
        backButton.setOnAction(e -> stage.setScene(new MainMenu().getScene(stage))); // Besoin d'ajuster MainMenu pour retourner Scene

        VBox endLayout = new VBox(10);
        endLayout.getChildren().addAll(endLabel, backButton);
        scene = new Scene(endLayout, 400, 300);
        stage.setScene(scene);
    }

    public Scene getScene() {
        return scene;
    }
}
