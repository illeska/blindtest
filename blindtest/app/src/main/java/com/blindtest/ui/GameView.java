package com.blindtest.ui;

import com.blindtest.controller.GameController;
import com.blindtest.model.Player;
import com.blindtest.model.Round;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
 * Vue principale du jeu (Écran de partie).
 * CORRIGÉ : Animations de réponse visibles (plus de blocage gris).
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
    private VBox player2Area; // Null en mode solo

    // Champs de saisie
    private TextField p1TitleInput, p1ArtistInput;
    private Button p1SubmitBtn;
    
    private TextField p2TitleInput, p2ArtistInput;
    private Button p2SubmitBtn;

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
        timerLabel.setStyle("-fx-text-fill: red;");

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
        hintTitleLabel.setFont(Font.font("Arial", 20));
        hintArtistLabel.setFont(Font.font("Arial", 20));
        
        Button hintBtn = new Button("💡 Demander un indice");
        hintBtn.setOnAction(e -> handleRequestHint());
        
        hintsBox.getChildren().addAll(hintTitleLabel, hintArtistLabel, hintBtn);

        // Zones de réponse (Solo ou Duel)
        HBox playersContainer = new HBox(40);
        playersContainer.setAlignment(Pos.CENTER);

        // Joueur 1 (Toujours présent)
        player1Area = createPlayerArea(0);
        playersContainer.getChildren().add(player1Area);

        // Joueur 2 (Seulement si Duel)
        if (controller.getPlayers().size() > 1) {
            player2Area = createPlayerArea(1);
            playersContainer.getChildren().add(player2Area);
        }

        statusLabel = new Label("");
        statusLabel.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");

        centerLayout.getChildren().addAll(hintsBox, playersContainer, statusLabel);
        root.setCenter(centerLayout);
    }

    private VBox createPlayerArea(int playerIndex) {
        Player player = controller.getPlayers().get(playerIndex);
        
        VBox area = new VBox(10);
        area.setAlignment(Pos.CENTER);
        area.setStyle("-fx-border-color: #ccc; -fx-border-radius: 5; -fx-padding: 15; -fx-background-color: #f9f9f9;");
        
        Label nameLabel = new Label(player.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        
        Label scoreLabel = new Label("Score: " + player.getScore());

        TextField titleInput = new TextField();
        titleInput.setPromptText("Titre de la chanson");
        
        TextField artistInput = new TextField();
        artistInput.setPromptText("Nom de l'artiste");

        Button submitBtn = new Button("Valider");
        submitBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        
        if (playerIndex == 0) {
            this.p1TitleInput = titleInput;
            this.p1ArtistInput = artistInput;
            this.p1SubmitBtn = submitBtn;
        } else {
            this.p2TitleInput = titleInput;
            this.p2ArtistInput = artistInput;
            this.p2SubmitBtn = submitBtn;
        }

        submitBtn.setOnAction(e -> handleSubmit(playerIndex, titleInput.getText(), artistInput.getText()));

        area.getChildren().addAll(nameLabel, scoreLabel, titleInput, artistInput, submitBtn);
        return area;
    }

    private void startRoundUI() {
        int currentIndex = controller.getCurrentRoundIndex();
        
        if (currentIndex >= controller.getNumberOfRounds()) {
            showEndGame();
            return;
        }

        roundLabel.setText("Manche " + (currentIndex + 1) + " / " + controller.getNumberOfRounds());
        updateHintsDisplay();

        // Réinitialisation propre (important pour effacer la couleur rouge/verte)
        resetPlayerInputs(p1TitleInput, p1ArtistInput, p1SubmitBtn);
        if (player2Area != null) {
            resetPlayerInputs(p2TitleInput, p2ArtistInput, p2SubmitBtn);
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

    private void resetPlayerInputs(TextField t1, TextField t2, Button btn) {
        if (t1 == null) return;
        
        // Champs textes
        t1.clear(); t1.setDisable(false);
        t2.clear(); t2.setDisable(false);
        
        // Bouton : On réactive tout
        btn.setDisable(false);          
        btn.setMouseTransparent(false); // On rend le clic possible à nouveau
        
        // On remet la couleur verte par défaut
        btn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;"); 
        
        // On remet la taille normale (reset animation)
        btn.setScaleX(1.0);
        btn.setScaleY(1.0);
        btn.setTranslateX(0);
    }

    private void handleSubmit(int playerIndex, String title, String artist) {
        // Debug
        System.out.println("Validation Joueur " + (playerIndex + 1));

        Player player = controller.getPlayers().get(playerIndex);
        int scoreBefore = player.getScore();

        long timeElapsed = controller.getSettings().getExtractDuration() - timeSeconds;
        int oldRoundIndex = controller.getCurrentRoundIndex();
        
        controller.checkAnswer(title, artist, timeElapsed, playerIndex);
        
        int scoreAfter = player.getScore();
        boolean isCorrect = scoreAfter > scoreBefore;
        
        System.out.println("Correct : " + isCorrect);

        // --- FIX : On utilise setMouseTransparent au lieu de setDisable ---
        Button targetBtn = (playerIndex == 0) ? p1SubmitBtn : p2SubmitBtn;
        if (targetBtn != null) {
            targetBtn.setMouseTransparent(true); // Bloque le clic mais garde la couleur !
            animerReponse(targetBtn, isCorrect);
        }

        // On désactive seulement les textes pour l'instant
        if (playerIndex == 0) {
            p1TitleInput.setDisable(true); p1ArtistInput.setDisable(true); 
        } else {
            p2TitleInput.setDisable(true); p2ArtistInput.setDisable(true); 
        }

        if (controller.getCurrentRoundIndex() > oldRoundIndex) {
            statusLabel.setText("Manche terminée !");
            
            // Pause de 2 secondes pour voir l'animation
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(event -> startRoundUI());
            pause.play();
            
        } else {
            statusLabel.setText("En attente des autres joueurs...");
        }
        
        refreshScores();
    }

    private void handleRequestHint() {
        String hint = controller.requestHint();
        if (hint != null) {
            updateHintsDisplay();
        } else {
            statusLabel.setText("Plus d'indices disponibles !");
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
        statusLabel.setText("Temps écoulé !");
        if (!p1SubmitBtn.isDisabled()) handleSubmit(0, "", "");
        if (p2SubmitBtn != null && !p2SubmitBtn.isDisabled()) handleSubmit(1, "", "");
    }

    private void refreshScores() {
        System.out.println("Scores mis à jour."); 
    }

    private void showEndGame() {
        if (timeline != null) timeline.stop();
        root.setCenter(new Label("PARTIE TERMINÉE ! \nConsultez le classement."));
    }

    private String formatTime(int seconds) {
        return String.format("00:%02d", seconds);
    }

    private void animerReponse(Button btn, boolean isCorrect) {
        if (isCorrect) {
            // VERT + POP
            btn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-color: #27ae60; -fx-border-width: 2px;");
            
            ScaleTransition st = new ScaleTransition(Duration.millis(200), btn);
            st.setByX(0.1); st.setByY(0.1); 
            st.setAutoReverse(true); st.setCycleCount(2); 
            st.play();
        } else {
            // ROUGE + SHAKE
            btn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-border-color: #c0392b; -fx-border-width: 2px;");
            
            TranslateTransition tt = new TranslateTransition(Duration.millis(50), btn);
            tt.setByX(10); 
            tt.setAutoReverse(true); tt.setCycleCount(6); 
            tt.play();
        }
    }
}