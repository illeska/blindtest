package com.blindtest.ui;

import com.blindtest.controller.GameController;
import com.blindtest.model.Player;
import com.blindtest.model.Round;

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
    
    // Style constant
    private static final String CARD_STYLE = "-fx-background-color: rgba(255, 255, 255, 0.95); -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);";

    // UI Elements
    private Label roundLabel, timerLabel, statusLabel;
    private Label hintTitleLabel, hintArtistLabel;
    private Timeline timeline;
    private int timeSeconds;

    // Player controls
    private TextField p1TitleInput, p1ArtistInput;
    private Button p1SubmitBtn;
    private Label p1ScoreLabel, p1FeedbackLabel;
    
    private TextField p2TitleInput, p2ArtistInput;
    private Button p2SubmitBtn;
    private Label p2ScoreLabel, p2FeedbackLabel;

    public GameView(GameController controller) {
        this.controller = controller;
        this.root = new StackPane();
        // Même fond que le menu
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

        // --- TOP BAR (Timer & Info) ---
        HBox topBar = new HBox(50);
        topBar.setAlignment(Pos.CENTER);
        
        VBox roundBox = createInfoBox("MANCHE", controller.getCurrentRoundIndex() + 1 + " / " + controller.getNumberOfRounds());
        roundLabel = (Label) roundBox.getChildren().get(1); // Hack rapide pour ref
        
        VBox timerBox = createInfoBox("TEMPS", "00:00");
        timerLabel = (Label) timerBox.getChildren().get(1);
        timerLabel.setTextFill(Color.web("#e74c3c"));

        topBar.getChildren().addAll(roundBox, timerBox);

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

        // --- PLAYERS ---
        HBox playersContainer = new HBox(30);
        playersContainer.setAlignment(Pos.CENTER);
        
        VBox p1Area = createPlayerCard(0);
        playersContainer.getChildren().add(p1Area);

        if (controller.getPlayers().size() > 1) {
            VBox p2Area = createPlayerCard(1);
            playersContainer.getChildren().add(p2Area);
        }

        // --- STATUS ---
        statusLabel = new Label("");
        statusLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        statusLabel.setTextFill(Color.WHITE);

        mainLayout.getChildren().addAll(topBar, hintsContainer, playersContainer, statusLabel);
        root.getChildren().add(mainLayout);
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

    private VBox createPlayerCard(int index) {
        Player player = controller.getPlayers().get(index);
        VBox card = new VBox(12);
        card.setStyle(CARD_STYLE);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER);
        card.setMinWidth(300);

        Label name = new Label(player.getName());
        name.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
        
        Label score = new Label("Score: " + player.getScore());
        score.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        score.setTextFill(Color.web("#6C5CE7"));

        Label feedback = new Label("");
        feedback.setWrapText(true);
        feedback.setMinHeight(40);

        TextField tInput = styleTextField("Titre...");
        TextField aInput = styleTextField("Artiste...");
        Button btn = new Button("VALIDER");
        styleButton(btn, "#6C5CE7");

        if (index == 0) {
            p1TitleInput = tInput; p1ArtistInput = aInput; p1SubmitBtn = btn;
            p1ScoreLabel = score; p1FeedbackLabel = feedback;
        } else {
            p2TitleInput = tInput; p2ArtistInput = aInput; p2SubmitBtn = btn;
            p2ScoreLabel = score; p2FeedbackLabel = feedback;
        }

        btn.setOnAction(e -> handleSubmit(index, tInput.getText(), aInput.getText()));

        card.getChildren().addAll(name, score, feedback, tInput, aInput, btn);
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
        
        roundLabel.setText((controller.getCurrentRoundIndex() + 1) + " / " + controller.getNumberOfRounds());
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

    private void handleSubmit(int index, String t, String a) {
        long elapsed = controller.getSettings().getExtractDuration() - timeSeconds;
        GameController.RoundResult res = controller.checkAnswer(t, a, elapsed, index);
        
        Label feedback = (index == 0) ? p1FeedbackLabel : p2FeedbackLabel;
        Label score = (index == 0) ? p1ScoreLabel : p2ScoreLabel;
        Button btn = (index == 0) ? p1SubmitBtn : p2SubmitBtn;
        
        score.setText("Score: " + controller.getPlayers().get(index).getScore());
        
        if (res.points > 0) {
            feedback.setText("✅ +" + res.points + " pts!");
            feedback.setTextFill(Color.web("#2ed573"));
        } else {
            feedback.setText("❌ Raté...");
            feedback.setTextFill(Color.web("#ff4757"));
        }
        
        btn.setDisable(true);
        if (index == 0) { p1TitleInput.setDisable(true); p1ArtistInput.setDisable(true); }
        else { p2TitleInput.setDisable(true); p2ArtistInput.setDisable(true); }

        if (res.isRoundOver) {
            timeline.stop();
            statusLabel.setText("Terminé ! La suite arrive...");
            Round current = controller.getCurrentRound();
            String answer = current.getTrack().getTitle() + " - " + current.getTrack().getArtist();
            
            // Afficher la réponse à tous
            p1FeedbackLabel.setText(p1FeedbackLabel.getText() + "\nC'était : " + answer);
            if(p2FeedbackLabel != null) p2FeedbackLabel.setText(p2FeedbackLabel.getText() + "\nC'était : " + answer);

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
        if (!p1SubmitBtn.isDisabled()) handleSubmit(0, "", "");
        if (p2SubmitBtn != null && !p2SubmitBtn.isDisabled()) handleSubmit(1, "", "");
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

    private void resetInputs() {
        p1TitleInput.clear(); p1ArtistInput.clear(); p1TitleInput.setDisable(false); p1ArtistInput.setDisable(false); p1SubmitBtn.setDisable(false); p1FeedbackLabel.setText("");
        if (p2SubmitBtn != null) {
            p2TitleInput.clear(); p2ArtistInput.clear(); p2TitleInput.setDisable(false); p2ArtistInput.setDisable(false); p2SubmitBtn.setDisable(false); p2FeedbackLabel.setText("");
        }
    }

    private String formatTime(int s) { return String.format("00:%02d", s); }
}