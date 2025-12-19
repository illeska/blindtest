package com.blindtest.ui;

import com.blindtest.App;
import com.blindtest.controller.GameController;
import com.blindtest.model.Player;
import com.blindtest.model.Round;
import com.blindtest.util.InputValidator;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class GameView {

    private final GameController controller;
    private final StackPane root;
    private VBox mainLayout;
    
    private static final String CARD_STYLE = "-fx-background-color: rgba(255, 255, 255, 0.95); -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);";

    private Label roundLabel, timerLabel, statusLabel, currentPlayerLabel;
    private Label hintTitleLabel, hintArtistLabel;
    private Timeline timeline;
    private int timeSeconds;

    // 🆕 Un seul ensemble de contrôles (visible selon le tour)
    private TextField titleInput, artistInput;
    private Button submitBtn;
    private Label feedbackLabel;
    
    // 🆕 Scores affichés en haut pour les deux joueurs
    private Label p1ScoreLabel, p2ScoreLabel;

    public GameView(GameController controller) {
        this.controller = controller;
        this.root = new StackPane();
        this.root.setStyle(MainMenu.BG_GRADIENT);
        
        initializeUI();
        startRoundUI();
    }

    public Parent getRootNode() { return root; }

    private void initializeUI() {
        mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setMaxWidth(800);

        // --- TOP BAR (Scores des deux joueurs en mode Duel) ---
        HBox topBar = new HBox(30);
        topBar.setAlignment(Pos.CENTER);
        
        if (controller.isDuelMode()) {
            // Scores des deux joueurs affichés en permanence
            VBox p1Box = createScoreBox(controller.getPlayers().get(0));
            p1ScoreLabel = (Label) p1Box.getChildren().get(1);
            
            VBox p2Box = createScoreBox(controller.getPlayers().get(1));
            p2ScoreLabel = (Label) p2Box.getChildren().get(1);
            
            topBar.getChildren().addAll(p1Box, p2Box);
        } else {
            // Mode Solo : un seul score
            VBox p1Box = createScoreBox(controller.getPlayers().get(0));
            p1ScoreLabel = (Label) p1Box.getChildren().get(1);
            topBar.getChildren().add(p1Box);
        }

        // --- INFO BAR (Manche & Timer) ---
        HBox infoBar = new HBox(50);
        infoBar.setAlignment(Pos.CENTER);
        
        VBox roundBox = createInfoBox("MANCHE", "1 / " + controller.getNumberOfRounds());
        roundLabel = (Label) roundBox.getChildren().get(1);
        
        VBox timerBox = createInfoBox("TEMPS", "00:00");
        timerLabel = (Label) timerBox.getChildren().get(1);
        timerLabel.setTextFill(Color.web("#e74c3c"));

        infoBar.getChildren().addAll(roundBox, timerBox);

        // 🆕 LABEL "C'EST LE TOUR DE X" (en mode Duel)
        currentPlayerLabel = new Label("");
        currentPlayerLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        currentPlayerLabel.setTextFill(Color.WHITE);

        // --- INDICES ---
        VBox hintsContainer = new VBox(10);
        hintsContainer.setAlignment(Pos.CENTER);
        hintsContainer.setStyle(CARD_STYLE);
        hintsContainer.setPadding(new Insets(15));
        hintsContainer.setMaxWidth(500);

        hintTitleLabel = new Label("Titre : ?????");
        hintTitleLabel.setFont(Font.font("Verdana", 20));
        hintArtistLabel = new Label("Artiste : ?????");
        hintArtistLabel.setFont(Font.font("Verdana", 20));

        Button hintBtn = new Button("💡 INDICE");
        styleButton(hintBtn, "#f1c40f");
        hintBtn.setOnAction(e -> handleRequestHint());

        hintsContainer.getChildren().addAll(hintTitleLabel, hintArtistLabel, hintBtn);

        // --- ZONE DE JEU (UN SEUL FORMULAIRE) ---
        VBox playArea = createPlayCard();

        // --- STATUS ---
        statusLabel = new Label("");
        statusLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        statusLabel.setTextFill(Color.WHITE);

        mainLayout.getChildren().addAll(topBar, infoBar, currentPlayerLabel, hintsContainer, playArea, statusLabel);
        root.getChildren().add(mainLayout);
    }

    private VBox createScoreBox(Player player) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: rgba(255,255,255,0.3); -fx-background-radius: 10; -fx-padding: 10 20;");
        
        Label name = new Label(player.getName());
        name.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        name.setTextFill(Color.WHITE);
        
        Label score = new Label("Score: " + player.getScore());
        score.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        score.setTextFill(Color.WHITE);
        
        box.getChildren().addAll(name, score);
        return box;
    }

    private VBox createInfoBox(String title, String value) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: rgba(255,255,255,0.3); -fx-background-radius: 10; -fx-padding: 10 20;");
        
        Label t = new Label(title);
        t.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        t.setTextFill(Color.WHITE);
        
        Label v = new Label(value);
        v.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
        v.setTextFill(Color.WHITE);
        
        box.getChildren().addAll(t, v);
        return box;
    }

    private VBox createPlayCard() {
        VBox card = new VBox(12);
        card.setStyle(CARD_STYLE);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER);
        card.setMinWidth(400);

        feedbackLabel = new Label("");
        feedbackLabel.setWrapText(true);
        feedbackLabel.setMinHeight(40);
        feedbackLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));

        titleInput = styleTextField("Titre...");
        artistInput = styleTextField("Artiste...");
        
        submitBtn = new Button("VALIDER");
        styleButton(submitBtn, "#6C5CE7");
        submitBtn.setOnAction(e -> handleSubmit(titleInput.getText(), artistInput.getText()));

        card.getChildren().addAll(feedbackLabel, titleInput, artistInput, submitBtn);
        return card;
    }

    private TextField styleTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-background-color: #f1f2f6; -fx-background-radius: 20; -fx-padding: 8 15;");
        return tf;
    }

    private void styleButton(Button btn, String color) {
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 20; -fx-font-weight: bold; -fx-cursor: hand;");
        btn.setPrefWidth(150);
    }

    private void startRoundUI() {
        if (!controller.isStarted()) {
            new EndGameView((javafx.stage.Stage)root.getScene().getWindow(), controller);
            return;
        }
        
        // Mise à jour du numéro de manche
        int displayRound = controller.isDuelMode() 
            ? (controller.getCurrentRoundIndex() / 2) + 1 
            : controller.getCurrentRoundIndex() + 1;
        roundLabel.setText(displayRound + " / " + controller.getNumberOfRounds());
        
        // 🆕 Affichage du joueur actuel en mode Duel
        if (controller.isDuelMode()) {
            Player currentPlayer = controller.getCurrentPlayer();
            currentPlayerLabel.setText("🎮 C'est le tour de " + currentPlayer.getName() + " !");
        } else {
            currentPlayerLabel.setText("");
        }
        
        updateScores();
        statusLabel.setText("");
        resetInputs();
        updateHints();
        
        if (timeline != null) timeline.stop();
        timeSeconds = controller.getSettings().getExtractDuration();
        timerLabel.setText(formatTime(timeSeconds));
        
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            timeSeconds--;
            timerLabel.setText(formatTime(timeSeconds));
            if (timeSeconds <= 0) handleTimeout();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void handleSubmit(String t, String a) {
        // 🆕 Validation des inputs
        String cleanTitle = InputValidator.sanitize(t);
        String cleanArtist = InputValidator.sanitize(a);
        
        long elapsed = controller.getSettings().getExtractDuration() - timeSeconds;
        int currentPlayerIdx = controller.isDuelMode() ? controller.getCurrentPlayerIndex() : 0;
        
        GameController.RoundResult res = controller.checkAnswer(cleanTitle, cleanArtist, elapsed, currentPlayerIdx);
        
        updateScores();
        
        if (res.points > 0) {
            feedbackLabel.setText("✅ +" + res.points + " pts!");
            feedbackLabel.setTextFill(Color.web("#2ed573"));
            App.getAudioService().playSfxVictory();
        } else {
            feedbackLabel.setText("❌ Raté...");
            feedbackLabel.setTextFill(Color.web("#ff4757"));
            App.getAudioService().playSfxFail();
        }
        
        submitBtn.setDisable(true);
        titleInput.setDisable(true);
        artistInput.setDisable(true);

        if (res.isRoundOver) {
            timeline.stop();
            Round current = controller.getCurrentRound();
            String answer = current.getTrack().getTitle() + " - " + current.getTrack().getArtist();
            
            feedbackLabel.setText(feedbackLabel.getText() + "\nC'était : " + answer);

            PauseTransition pause = new PauseTransition(Duration.seconds(4));
            pause.setOnFinished(e -> {
                controller.nextRound();
                startRoundUI();
            });
            pause.play();
        }
    }
    
    private void handleTimeout() {
        timeline.stop();
        statusLabel.setText("⏰ Temps écoulé !");
        if (!submitBtn.isDisabled()) {
            handleSubmit("", "");
        }
    }

    private void handleRequestHint() {
        String hint = controller.requestHint();
        if (hint != null) {
            updateHints();
            statusLabel.setText("Indice révélé !");
        } else {
            statusLabel.setText("Plus d'indices !");
        }
    }

    private void updateHints() {
        Round r = controller.getCurrentRound();
        if (r != null) {
            hintTitleLabel.setText("Titre : " + r.getTitleHint());
            hintArtistLabel.setText("Artiste : " + r.getArtistHint());
        }
    }

    private void updateScores() {
        p1ScoreLabel.setText("Score: " + controller.getPlayers().get(0).getScore());
        if (controller.isDuelMode() && p2ScoreLabel != null) {
            p2ScoreLabel.setText("Score: " + controller.getPlayers().get(1).getScore());
        }
    }

    private void resetInputs() {
        titleInput.clear();
        artistInput.clear();
        titleInput.setDisable(false);
        artistInput.setDisable(false);
        submitBtn.setDisable(false);
        feedbackLabel.setText("");
    }

    private String formatTime(int s) { 
        return String.format("00:%02d", s); 
    }
}