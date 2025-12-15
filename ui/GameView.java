package com.blindtest.ui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.blindtest.controller.GameController;
import com.blindtest.model.Player;
import com.blindtest.model.Round;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Vue principale du jeu avec feedback en temps réel.
 */
public class GameView {

    private final GameController controller;
    private final BorderPane root;
    
    // Éléments UI dynamiques
    private Label roundLabel;
    private Label timerLabel;
    private Label hintTitleLabel;
    private Label hintArtistLabel;
    private Label statusLabel; 

    // Timer
    private Timeline timeline;
    private int timeSeconds;

    // Zones de joueurs
    private VBox player1Area;
    private VBox player2Area;

    // Champs de saisie Joueur 1
    private TextField p1TitleInput, p1ArtistInput;
    private Button p1SubmitBtn;
    private Label p1ScoreLabel;
    private Label p1FeedbackLabel;
    
    // Champs de saisie Joueur 2
    private TextField p2TitleInput, p2ArtistInput;
    private Button p2SubmitBtn;
    private Label p2ScoreLabel;
    private Label p2FeedbackLabel;

    public GameView(GameController controller) {
        this.controller = controller;
        this.root = new BorderPane();
        this.root.setPadding(new Insets(20));

        initializeUI();
        startRoundUI(); 
    }

    public Parent getRootNode() {
        return root;
    }

    private void initializeUI() {
        // --- TOP : Info Manche & Timer ---
        HBox topBar = new HBox(50);
        topBar.setAlignment(Pos.CENTER);
        
        roundLabel = new Label("Manche 1 / " + controller.getNumberOfRounds());
        roundLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        timerLabel = new Label("00:00");
        timerLabel.setFont(Font.font("Monospace", FontWeight.BOLD, 24));
        timerLabel.setStyle("-fx-text-fill: #e74c3c;"); // Rouge moderne

        topBar.getChildren().addAll(roundLabel, timerLabel);
        root.setTop(topBar);

        // --- CENTER : Indices et Zones de Jeu ---
        VBox centerLayout = new VBox(30);
        centerLayout.setAlignment(Pos.CENTER);

        // Indices
        VBox hintsBox = new VBox(10);
        hintsBox.setAlignment(Pos.CENTER);
        hintTitleLabel = new Label("Titre : ?????");
        hintArtistLabel = new Label("Artiste : ?????");
        hintTitleLabel.setFont(Font.font("Segoe UI", 22));
        hintArtistLabel.setFont(Font.font("Segoe UI", 22));
        
        Button hintBtn = new Button("💡 Demander un indice");
        hintBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        hintBtn.setOnAction(e -> handleRequestHint());
        
        hintsBox.getChildren().addAll(hintTitleLabel, hintArtistLabel, hintBtn);

        // Zones de réponse
        HBox playersContainer = new HBox(40);
        playersContainer.setAlignment(Pos.CENTER);

        player1Area = createPlayerArea(0);
        playersContainer.getChildren().add(player1Area);

        if (controller.getPlayers().size() > 1) {
            player2Area = createPlayerArea(1);
            playersContainer.getChildren().add(player2Area);
        }

        statusLabel = new Label("");
        statusLabel.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold; -fx-font-size: 16px;");

        centerLayout.getChildren().addAll(hintsBox, playersContainer, statusLabel);
        root.setCenter(centerLayout);
    }

    private VBox createPlayerArea(int playerIndex) {
        Player player = controller.getPlayers().get(playerIndex);
        
        VBox area = new VBox(12);
        area.setAlignment(Pos.CENTER);
        area.setPadding(new Insets(20));
        // Style carte blanche avec ombre légère
        area.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        
        Label nameLabel = new Label(player.getName());
        nameLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        
        Label scoreLabel = new Label("Score: " + player.getScore());
        scoreLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        scoreLabel.setStyle("-fx-text-fill: #2ecc71;"); // Vert

        Label feedbackLabel = new Label("");
        feedbackLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        feedbackLabel.setWrapText(true);
        feedbackLabel.setMaxWidth(250);
        feedbackLabel.setAlignment(Pos.CENTER);

        TextField titleInput = new TextField();
        titleInput.setPromptText("Titre de la chanson");
        titleInput.setStyle("-fx-padding: 8; -fx-background-radius: 5;");
        
        TextField artistInput = new TextField();
        artistInput.setPromptText("Nom de l'artiste");
        artistInput.setStyle("-fx-padding: 8; -fx-background-radius: 5;");

        Button submitBtn = new Button("Valider");
        submitBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 5; -fx-cursor: hand;");
        
        if (playerIndex == 0) {
            this.p1TitleInput = titleInput;
            this.p1ArtistInput = artistInput;
            this.p1SubmitBtn = submitBtn;
            this.p1ScoreLabel = scoreLabel;
            this.p1FeedbackLabel = feedbackLabel;
        } else {
            this.p2TitleInput = titleInput;
            this.p2ArtistInput = artistInput;
            this.p2SubmitBtn = submitBtn;
            this.p2ScoreLabel = scoreLabel;
            this.p2FeedbackLabel = feedbackLabel;
        }

        submitBtn.setOnAction(e -> handleSubmit(playerIndex, titleInput.getText(), artistInput.getText()));

        area.getChildren().addAll(nameLabel, scoreLabel, feedbackLabel, titleInput, artistInput, submitBtn);
        return area;
    }

    private void startRoundUI() {
        if (!controller.isStarted()) {
            showEndGame();
            return;
        }

        int currentIndex = controller.getCurrentRoundIndex();
        if (currentIndex >= controller.getNumberOfRounds()) {
            showEndGame();
            return;
        }

        roundLabel.setText("Manche " + (currentIndex + 1) + " / " + controller.getNumberOfRounds());
        updateHintsDisplay();

        resetPlayerInputs(p1TitleInput, p1ArtistInput, p1SubmitBtn, p1FeedbackLabel);
        if (player2Area != null) {
            resetPlayerInputs(p2TitleInput, p2ArtistInput, p2SubmitBtn, p2FeedbackLabel);
        }
        statusLabel.setText("");

        if (timeline != null) timeline.stop();
        timeSeconds = controller.getSettings().getExtractDuration();
        timerLabel.setText(formatTime(timeSeconds));
        
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            timeSeconds--;
            timerLabel.setText(formatTime(timeSeconds));
            if (timeSeconds <= 0) {
                timeline.stop();
                handleTimeout();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void resetPlayerInputs(TextField t1, TextField t2, Button btn, Label feedbackLabel) {
        if (t1 == null) return;
        t1.clear(); t1.setDisable(false);
        t2.clear(); t2.setDisable(false);
        
        btn.setDisable(false);          
        btn.setMouseTransparent(false);
        btn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 20; -fx-background-radius: 5;"); 
        
        btn.setScaleX(1.0); btn.setScaleY(1.0); btn.setTranslateX(0);
        if (feedbackLabel != null) feedbackLabel.setText("");
    }

    private void handleSubmit(int playerIndex, String title, String artist) {
        long timeElapsed = controller.getSettings().getExtractDuration() - timeSeconds;
        
        // Appel Controller
        GameController.RoundResult result = controller.checkAnswer(title, artist, timeElapsed, playerIndex);
        Player player = controller.getPlayers().get(playerIndex);
        
        Label scoreLabel = (playerIndex == 0) ? p1ScoreLabel : p2ScoreLabel;
        Label feedbackLabel = (playerIndex == 0) ? p1FeedbackLabel : p2FeedbackLabel;
        
        if (scoreLabel != null) scoreLabel.setText("Score: " + player.getScore());
        
        if (feedbackLabel != null) {
            if (result.points > 0) {
                if (result.points >= 3) {
                    feedbackLabel.setText("⚡ EXCELLENT ! +" + result.points);
                    feedbackLabel.setStyle("-fx-text-fill: #f1c40f; -fx-font-weight: bold;");
                } else if (result.points == 2) {
                    feedbackLabel.setText("✅ CORRECT ! +" + result.points);
                    feedbackLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
                } else {
                    feedbackLabel.setText("👍 PAS MAL ! +" + result.points);
                    feedbackLabel.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
                }
            } else {
                Round currentRound = controller.getCurrentRound();
                String correct = (currentRound != null && currentRound.getTrack() != null) 
                        ? currentRound.getTrack().getTitle() + " - " + currentRound.getTrack().getArtist() 
                        : "Inconnu";
                feedbackLabel.setText("❌ Raté ! C'était : " + correct);
                feedbackLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            }
        }

        Button targetBtn = (playerIndex == 0) ? p1SubmitBtn : p2SubmitBtn;
        if (targetBtn != null) {
            targetBtn.setMouseTransparent(true);
            animerReponse(targetBtn, result.points > 0);
        }

        if (playerIndex == 0) { p1TitleInput.setDisable(true); p1ArtistInput.setDisable(true); } 
        else { p2TitleInput.setDisable(true); p2ArtistInput.setDisable(true); }

        if (result.isRoundOver) {
            if (timeline != null) timeline.stop();
            statusLabel.setText("Manche terminée ! Suite dans 4s...");
            statusLabel.setStyle("-fx-text-fill: #8e44ad; -fx-font-weight: bold; -fx-font-size: 18px;");
            
            PauseTransition pause = new PauseTransition(Duration.seconds(4));
            pause.setOnFinished(event -> {
                controller.nextRound();
                startRoundUI();
            });
            pause.play();
        } else {
            statusLabel.setText("En attente des autres joueurs...");
        }
    }

    private void handleRequestHint() {
        String hint = controller.requestHint();
        if (hint != null) {
            updateHintsDisplay();
            statusLabel.setText("💡 Indice révélé !");
        } else {
            statusLabel.setText("❌ Plus d'indices !");
        }
    }

    private void updateHintsDisplay() {
        Round current = controller.getCurrentRound();
        if (current != null) {
            hintTitleLabel.setText("Titre : " + current.getTitleHint());
            hintArtistLabel.setText("Artiste : " + current.getArtistHint());
        }
    }

    private void handleTimeout() {
        statusLabel.setText("⏰ Temps écoulé !");
        if (!p1SubmitBtn.isDisabled()) handleSubmit(0, "", "");
        if (p2SubmitBtn != null && !p2SubmitBtn.isDisabled()) handleSubmit(1, "", "");
    }

    // --- NOUVEL ÉCRAN DE FIN MODERNE ---
    private void showEndGame() {
        if (timeline != null) timeline.stop();
        
        // On vide la fenêtre
        root.setTop(null);
        root.setCenter(null);
        
        // Conteneur principal avec dégradé
        VBox mainContainer = new VBox(25);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(40));
        // Fond dégradé bleu nuit vers violet
        mainContainer.setStyle("-fx-background-color: linear-gradient(to bottom right, #2c3e50, #4ca1af);");

        // Titre avec ombre
        Label titleLabel = new Label("🏆 RÉSULTATS FINAUX 🏆");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 36));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setEffect(new DropShadow(10, Color.BLACK));
        
        // Carte des scores (Style Glassmorphism)
        VBox scoreCard = new VBox(15);
        scoreCard.setAlignment(Pos.CENTER);
        scoreCard.setPadding(new Insets(30));
        scoreCard.setMaxWidth(600);
        scoreCard.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 5);");

        // Tri des joueurs
        List<Player> sortedPlayers = new ArrayList<>(controller.getPlayers());
        sortedPlayers.sort(Comparator.comparingInt(Player::getScore).reversed());

        for (int i = 0; i < sortedPlayers.size(); i++) {
            Player p = sortedPlayers.get(i);
            HBox playerRow = new HBox(20);
            playerRow.setAlignment(Pos.CENTER_LEFT);
            playerRow.setPadding(new Insets(10));
            
            // Logique d'affichage des médailles
            String rankSymbol;
            String colorStyle;
            double fontSize = 20;
            
            if (i == 0) { rankSymbol = "🥇"; colorStyle = "-fx-text-fill: #DAA520; -fx-font-weight: bold;"; fontSize=26; } // Or
            else if (i == 1) { rankSymbol = "🥈"; colorStyle = "-fx-text-fill: #7f8c8d; -fx-font-weight: bold;"; fontSize=22; } // Argent
            else if (i == 2) { rankSymbol = "🥉"; colorStyle = "-fx-text-fill: #cd6133; -fx-font-weight: bold;"; fontSize=22; } // Bronze
            else { rankSymbol = (i + 1) + "."; colorStyle = "-fx-text-fill: #34495e;"; fontSize=18; }

            Label rankLabel = new Label(rankSymbol);
            rankLabel.setFont(Font.font("Segoe UI Emoji", fontSize));
            rankLabel.setMinWidth(50);
            rankLabel.setAlignment(Pos.CENTER);

            Label nameLabel = new Label(p.getName());
            nameLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, fontSize));
            nameLabel.setStyle("-fx-text-fill: #2c3e50;");
            HBox.setHgrow(nameLabel, Priority.ALWAYS); // Le nom prend l'espace dispo

            Label scoreLabel = new Label(p.getScore() + " pts");
            scoreLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, fontSize));
            scoreLabel.setStyle(colorStyle);
            
            playerRow.getChildren().addAll(rankLabel, nameLabel, scoreLabel);
            
            // Ligne de séparation discrète
            if (i < sortedPlayers.size() - 1) {
                playerRow.setStyle("-fx-border-color: transparent transparent #ecf0f1 transparent; -fx-border-width: 2;");
            }
            
            scoreCard.getChildren().add(playerRow);
        }

        // Boutons d'action
        HBox buttonsBox = new HBox(20);
        buttonsBox.setAlignment(Pos.CENTER);

        Button menuBtn = createStyledButton("🏠 Menu Principal", "#3498db");
        menuBtn.setOnAction(e -> returnToMenu());

        Button quitBtn = createStyledButton("🚪 Quitter", "#e74c3c");
        quitBtn.setOnAction(e -> System.exit(0));

        buttonsBox.getChildren().addAll(menuBtn, quitBtn);

        mainContainer.getChildren().addAll(titleLabel, scoreCard, buttonsBox);
        root.setCenter(mainContainer);
        
        // Animation d'entrée en fondu
        FadeTransition ft = new FadeTransition(Duration.millis(800), mainContainer);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }
    
    // Helper pour créer de beaux boutons
    private Button createStyledButton(String text, String colorHex) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        btn.setStyle("-fx-background-color: " + colorHex + "; -fx-text-fill: white; -fx-background-radius: 30; -fx-padding: 12 30; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);");
        
        // Effet Hover
        btn.setOnMouseEntered(e -> {
            btn.setStyle("-fx-background-color: derive(" + colorHex + ", 20%); -fx-text-fill: white; -fx-background-radius: 30; -fx-padding: 12 30; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 8, 0, 0, 4);");
            btn.setScaleX(1.05);
            btn.setScaleY(1.05);
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle("-fx-background-color: " + colorHex + "; -fx-text-fill: white; -fx-background-radius: 30; -fx-padding: 12 30; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);");
            btn.setScaleX(1.0);
            btn.setScaleY(1.0);
        });
        
        return btn;
    }
    
    private void returnToMenu() {
         try {
            Stage stage = (Stage) root.getScene().getWindow();
            new MainMenu().start(stage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String formatTime(int seconds) {
        return String.format("00:%02d", seconds);
    }

    private void animerReponse(Button btn, boolean isCorrect) {
        if (isCorrect) {
            btn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
            ScaleTransition st = new ScaleTransition(Duration.millis(200), btn);
            st.setByX(0.1); st.setByY(0.1); 
            st.setAutoReverse(true); st.setCycleCount(2); 
            st.play();
        } else {
            btn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
            TranslateTransition tt = new TranslateTransition(Duration.millis(50), btn);
            tt.setByX(10); 
            tt.setAutoReverse(true); tt.setCycleCount(6); 
            tt.play();
        }
    }
}