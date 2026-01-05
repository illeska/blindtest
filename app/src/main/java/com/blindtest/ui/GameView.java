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
import javafx.scene.control.ProgressBar;
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
    private Label bonusLabel;
    private ProgressBar timerBar;
    private Timeline timeline;
    private int timeSeconds;
    private int maxTime;

    private TextField titleInput, artistInput;
    private Button submitBtn;
    private Label feedbackLabel;
    
    private Label p1ScoreLabel, p2ScoreLabel;

    /**
     * Constructeur de la vue du jeu.
     */
    public GameView(GameController controller) {
        this.controller = controller;
        this.root = new StackPane();
        this.root.setStyle(MainMenu.BG_GRADIENT);
        
        initializeUI();
        startRoundUI();
    }

    /**
     * Retourne le nœud racine de la vue.
     */
    public Parent getRootNode() { 
        return root; 
    }

    /**
     * Initialise tous les composants de l'interface utilisateur.
     */
    private void initializeUI() {
        mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setMaxWidth(800);

        // TOP BAR - Scores
        HBox topBar = new HBox(30);
        topBar.setAlignment(Pos.CENTER);
        
        if (controller.isDuelMode()) {
            VBox p1Box = createScoreBox(controller.getPlayers().get(0));
            p1ScoreLabel = (Label) p1Box.getChildren().get(1);
            
            VBox p2Box = createScoreBox(controller.getPlayers().get(1));
            p2ScoreLabel = (Label) p2Box.getChildren().get(1);
            
            topBar.getChildren().addAll(p1Box, p2Box);
        } else {
            VBox p1Box = createScoreBox(controller.getPlayers().get(0));
            p1ScoreLabel = (Label) p1Box.getChildren().get(1);
            topBar.getChildren().add(p1Box);
        }

        // INFO BAR - Manche et Timer
        HBox infoBar = new HBox(50);
        infoBar.setAlignment(Pos.CENTER);
        
        VBox roundBox = createInfoBox("MANCHE", "1 / " + controller.getNumberOfRounds());
        roundLabel = (Label) roundBox.getChildren().get(1);
        
        VBox timerBox = createInfoBox("TEMPS", "00:00");
        timerLabel = (Label) timerBox.getChildren().get(1);
        timerLabel.setTextFill(Color.web("#e74c3c"));

        infoBar.getChildren().addAll(roundBox, timerBox);

        // 🆕 JAUGE DE RAPIDITÉ
        VBox speedGauge = createSpeedGauge();

        // Label du joueur actuel (mode Duel)
        currentPlayerLabel = new Label("");
        currentPlayerLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        currentPlayerLabel.setTextFill(Color.WHITE);

        // Conteneur des indices
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

        // Zone de jeu
        VBox playArea = createPlayCard();

        // Label de statut
        statusLabel = new Label("");
        statusLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        statusLabel.setTextFill(Color.WHITE);

        // 🆕 AJOUT DE LA JAUGE DANS LE LAYOUT
        mainLayout.getChildren().addAll(
            topBar, 
            infoBar, 
            speedGauge,  // <- LA JAUGE EST ICI
            currentPlayerLabel, 
            hintsContainer, 
            playArea, 
            statusLabel
        );
        
        root.getChildren().add(mainLayout);
    }

    /**
     * 🆕 Crée la jauge de rapidité avec barre de progression.
     */
    private VBox createSpeedGauge() {
        VBox gaugeBox = new VBox(12);
        gaugeBox.setAlignment(Pos.CENTER);
        gaugeBox.setStyle(
            "-fx-background-color: rgba(255,255,255,0.25); " +
            "-fx-background-radius: 15; " +
            "-fx-padding: 20; " +
            "-fx-border-color: rgba(255,255,255,0.4); " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 15;"
        );
        gaugeBox.setMaxWidth(650);
        gaugeBox.setPrefHeight(140);

        // Titre de la jauge
        Label gaugeTitle = new Label("⚡ JAUGE DE RAPIDITÉ");
        gaugeTitle.setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, 16));
        gaugeTitle.setTextFill(Color.WHITE);
        gaugeTitle.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);");

        // Barre de progression avec STYLE FORCÉ
        timerBar = new ProgressBar(1.0);
        timerBar.setPrefWidth(600);
        timerBar.setPrefHeight(35);
        timerBar.setMinHeight(35);
        timerBar.setMaxHeight(35);
        timerBar.setStyle(
            "-fx-accent: #2ecc71; " +
            "-fx-control-inner-background: #34495e; " +
            "-fx-background-color: #34495e; " +
            "-fx-border-color: white; " +
            "-fx-border-width: 2; " +
            "-fx-background-radius: 10; " +
            "-fx-border-radius: 10; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 8, 0, 0, 3);"
        );

        // Label du bonus
        bonusLabel = new Label("⚡ BONUS ACTIF : +1 pt !");
        bonusLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
        bonusLabel.setTextFill(Color.web("#2ecc71"));
        bonusLabel.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 5, 0, 0, 2);");

        gaugeBox.getChildren().addAll(gaugeTitle, timerBar, bonusLabel);
        return gaugeBox;
    }

    /**
     * Crée une boîte d'affichage du score.
     */
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

    /**
     * Crée une boîte d'information.
     */
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

    /**
     * Crée la carte de jeu avec formulaire.
     */
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

    /**
     * Style un champ de texte.
     */
    private TextField styleTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-background-color: #f1f2f6; -fx-background-radius: 20; -fx-padding: 8 15;");
        return tf;
    }

    /**
     * Style un bouton.
     */
    private void styleButton(Button btn, String color) {
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 20; -fx-font-weight: bold; -fx-cursor: hand;");
        btn.setPrefWidth(150);
    }

    /**
     * Démarre l'interface pour une nouvelle manche.
     */
    private void startRoundUI() {
        if (!controller.isStarted()) {
            EndGameView endView = new EndGameView(controller);
            App.setView(endView.getView());
            return;
        }
        
        // Mise à jour manche
        int displayRound = controller.isDuelMode() 
            ? (controller.getCurrentRoundIndex() / 2) + 1 
            : controller.getCurrentRoundIndex() + 1;
        roundLabel.setText(displayRound + " / " + controller.getNumberOfRounds());
        
        // Joueur actuel en mode Duel
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
        
        // 🆕 Initialisation du timer et de la jauge
        maxTime = controller.getSettings().getExtractDuration();
        timeSeconds = maxTime;
        timerLabel.setText(formatTime(timeSeconds));
        timerBar.setProgress(1.0);
        // Style initial de la barre avec fond visible
        timerBar.setStyle(
            "-fx-accent: #2ecc71; " +
            "-fx-control-inner-background: #34495e; " +
            "-fx-background-color: #34495e; " +
            "-fx-border-color: white; " +
            "-fx-border-width: 2; " +
            "-fx-background-radius: 10; " +
            "-fx-border-radius: 10; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 8, 0, 0, 3);"
        );
        updateBonusLabel();
        
        // Timeline avec mise à jour de la barre
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            timeSeconds--;
            timerLabel.setText(formatTime(timeSeconds));
            
            // 🆕 Mise à jour de la barre et du bonus
            updateTimerBar();
            updateBonusLabel();
            
            if (timeSeconds <= 0) handleTimeout();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * 🆕 Met à jour la barre de progression et sa couleur.
     */
    private void updateTimerBar() {
        double progress = (double) timeSeconds / maxTime;
        timerBar.setProgress(progress);
        
        // Changement de couleur selon le temps restant
        String baseStyle = "-fx-control-inner-background: #34495e; " +
                          "-fx-background-color: #34495e; " +
                          "-fx-border-color: white; " +
                          "-fx-border-width: 2; " +
                          "-fx-background-radius: 10; " +
                          "-fx-border-radius: 10; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 8, 0, 0, 3);";
        
        if (progress > 0.6) {
            timerBar.setStyle(baseStyle + "-fx-accent: #2ecc71;"); // Vert
        } else if (progress > 0.3) {
            timerBar.setStyle(baseStyle + "-fx-accent: #f39c12;"); // Orange
        } else {
            timerBar.setStyle(baseStyle + "-fx-accent: #e74c3c;"); // Rouge
        }
    }

    /**
     * 🆕 Met à jour le label du bonus selon le temps.
     */
    private void updateBonusLabel() {
        if (!controller.getSettings().isSpeedBonusEnabled()) {
            bonusLabel.setText("Bonus désactivé");
            bonusLabel.setTextFill(Color.GRAY);
            return;
        }
        
        long elapsed = maxTime - timeSeconds;
        boolean hasBonus = elapsed < (maxTime / 2.0);
        
        if (hasBonus) {
            bonusLabel.setText("⚡ BONUS ACTIF : +1 pt !");
            bonusLabel.setTextFill(Color.web("#2ecc71"));
        } else {
            bonusLabel.setText("Pas de bonus de rapidité");
            bonusLabel.setTextFill(Color.web("#95a5a6"));
        }
    }

    /**
     * Gère la soumission d'une réponse.
     */
    private void handleSubmit(String t, String a) {
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

            // 🆕 Petite pause avant la transition (1.5 secondes)
            PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
            pause.setOnFinished(e -> {
                // 🆕 Afficher l'écran de transition au lieu de passer directement à la manche suivante
                showTransitionScreen(res.points, answer, res.isTitleCorrect || res.isArtistCorrect);
            });
            pause.play();
        }
    }

    /**
     * Gère l'expiration du temps.
     */
    private void handleTimeout() {
        timeline.stop();
        statusLabel.setText("⏰ Temps écoulé !");
        if (!submitBtn.isDisabled()) {
            handleSubmit("", "");
        }
    }

    /**
     * Gère la demande d'indice.
     */
    private void handleRequestHint() {
        String hint = controller.requestHint();
        if (hint != null) {
            updateHints();
            statusLabel.setText("Indice révélé !");
        } else {
            statusLabel.setText("Plus d'indices !");
        }
    }

    /**
     * Met à jour l'affichage des indices.
     */
    private void updateHints() {
        Round r = controller.getCurrentRound();
        if (r != null) {
            hintTitleLabel.setText("Titre : " + r.getTitleHint());
            hintArtistLabel.setText("Artiste : " + r.getArtistHint());
        }
    }

    /**
     * Met à jour l'affichage des scores.
     */
    private void updateScores() {
        p1ScoreLabel.setText("Score: " + controller.getPlayers().get(0).getScore());
        if (controller.isDuelMode() && p2ScoreLabel != null) {
            p2ScoreLabel.setText("Score: " + controller.getPlayers().get(1).getScore());
        }
    }

    /**
     * Réinitialise les champs de saisie.
     */
    private void resetInputs() {
        titleInput.clear();
        artistInput.clear();
        titleInput.setDisable(false);
        artistInput.setDisable(false);
        submitBtn.setDisable(false);
        feedbackLabel.setText("");
    }

    /**
     * Formate le temps en MM:SS.
     */
    private String formatTime(int s) { 
        return String.format("00:%02d", s); 
    }

    /**
     * 🆕 Affiche l'écran de transition entre les manches.
     */
    private void showTransitionScreen(int pointsEarned, String correctAnswer, boolean wasCorrect) {
        int currentRoundIndex = controller.getCurrentRoundIndex();
        
        RoundTransitionView transitionView = new RoundTransitionView(
            controller,
            currentRoundIndex,
            pointsEarned,
            correctAnswer,
            wasCorrect,
            () -> {
                // Callback : passer à la manche suivante après la transition
                // Retirer l'overlay de transition
                if (root.getChildren().size() > 1) {
                    root.getChildren().remove(1); // Retirer l'overlay
                }
                
                controller.nextRound();
                startRoundUI();
            }
        );
        
        // Ajouter la transition comme overlay au-dessus de la vue actuelle
        root.getChildren().add(transitionView.getView());
    }
}