package com.blindtest.ui;

import com.blindtest.controller.GameController;
import com.blindtest.model.Player;
import com.blindtest.model.Round;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
 * Gère l'affichage des indices, du timer et des zones de réponse (Solo ou Duel).
 */
public class GameView {

    private final GameController controller;
    private final BorderPane root;
    
    // Éléments UI dynamiques
    private Label roundLabel;
    private Label timerLabel;
    private Label hintTitleLabel;
    private Label hintArtistLabel;
    private Label statusLabel; // Pour afficher "En attente du Joueur 2..."

    // Timer
    private Timeline timeline;
    private int timeSeconds;

    // Zones de joueurs
    private VBox player1Area;
    private VBox player2Area; // Null en mode solo

    // Champs de saisie (gardés en mémoire pour les désactiver après réponse)
    private TextField p1TitleInput, p1ArtistInput;
    private Button p1SubmitBtn;
    
    private TextField p2TitleInput, p2ArtistInput;
    private Button p2SubmitBtn;

    public GameView(GameController controller) {
        this.controller = controller;
        this.root = new BorderPane();
        this.root.setPadding(new Insets(20));

        initializeUI();
        startRoundUI(); // Lance le timer et l'affichage pour la 1ère manche
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

    /**
     * Crée la zone de saisie pour un joueur spécifique.
     */
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
        
        // Stockage des références pour contrôle ultérieur
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

    /**
     * Logique appelée au démarrage d'une nouvelle manche.
     */
    private void startRoundUI() {
        // 1. Mise à jour des infos de base
        int currentIndex = controller.getCurrentRoundIndex();
        
        // Vérification de fin de partie (sécurité)
        if (currentIndex >= controller.getNumberOfRounds()) {
            showEndGame();
            return;
        }

        roundLabel.setText("Manche " + (currentIndex + 1) + " / " + controller.getNumberOfRounds());
        
        // 2. Réinitialisation des indices (masqués)
        updateHintsDisplay();

        // 3. Réinitialisation des champs et boutons
        resetPlayerInputs(p1TitleInput, p1ArtistInput, p1SubmitBtn);
        if (player2Area != null) {
            resetPlayerInputs(p2TitleInput, p2ArtistInput, p2SubmitBtn);
        }
        statusLabel.setText("");

        // 4. Lancement du Timer
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
        t1.clear(); t1.setDisable(false);
        t2.clear(); t2.setDisable(false);
        btn.setDisable(false);
    }

    private void handleSubmit(int playerIndex, String title, String artist) {
        // 1. Calcul du temps écoulé pour le bonus
        long timeElapsed = controller.getSettings().getExtractDuration() - timeSeconds;
        
        // 2. Appel au contrôleur
        int oldRoundIndex = controller.getCurrentRoundIndex();
        controller.checkAnswer(title, artist, timeElapsed, playerIndex);
        
        // 3. Désactiver les champs du joueur qui vient de répondre
        if (playerIndex == 0) {
            p1TitleInput.setDisable(true); p1ArtistInput.setDisable(true); p1SubmitBtn.setDisable(true);
        } else {
            p2TitleInput.setDisable(true); p2ArtistInput.setDisable(true); p2SubmitBtn.setDisable(true);
        }

        // 4. Vérifier si on a changé de manche (ce qui veut dire que tous le monde a répondu)
        if (controller.getCurrentRoundIndex() > oldRoundIndex) {
            // Manche suivante !
            startRoundUI();
        } else {
            // On attend l'autre joueur
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
        
        // Force la soumission pour ceux qui n'ont pas répondu (si non désactivé)
        if (!p1SubmitBtn.isDisabled()) handleSubmit(0, "", "");
        if (p2SubmitBtn != null && !p2SubmitBtn.isDisabled()) handleSubmit(1, "", "");
    }

    private void refreshScores() {
        // Mettre à jour l'affichage des scores dans les box des joueurs
        // (Nécessite de recréer ou d'accéder aux labels, ici simplifié pour l'exemple)
        // Dans une version idéale, PlayerArea serait une classe séparée avec une méthode updateScore()
        System.out.println("Rafraîchissement des scores UI..."); 
    }

    private void showEndGame() {
        if (timeline != null) timeline.stop();
        root.setCenter(new Label("PARTIE TERMINÉE ! \nConsultez le classement."));
        // Ici vous pourriez ajouter un bouton pour retourner au menu
    }

    private String formatTime(int seconds) {
        return String.format("00:%02d", seconds);
    }
}