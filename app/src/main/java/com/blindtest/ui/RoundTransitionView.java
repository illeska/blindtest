package com.blindtest.ui;

import com.blindtest.controller.GameController;
import com.blindtest.model.Player;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Vue de transition affichée entre chaque manche.
 * Affiche le récapitulatif de la manche terminée et le classement actuel.
 */
public class RoundTransitionView {
    
    private final GameController controller;
    private final int roundJustFinished;
    private final int pointsEarned;
    private final String correctAnswer;
    private final boolean wasCorrect;
    private final Runnable onTransitionComplete;
    
    private StackPane root;
    private int countdown = 3;

    /**
     * Constructeur de la vue de transition.
     * 
     * @param controller Le contrôleur de jeu
     * @param roundJustFinished Le numéro de la manche qui vient de se terminer
     * @param pointsEarned Les points gagnés sur cette manche
     * @param correctAnswer La réponse correcte
     * @param wasCorrect Si le joueur a trouvé la bonne réponse
     * @param onTransitionComplete Callback à exécuter après la transition
     */
    public RoundTransitionView(GameController controller, int roundJustFinished, 
                               int pointsEarned, String correctAnswer, boolean wasCorrect,
                               Runnable onTransitionComplete) {
        this.controller = controller;
        this.roundJustFinished = roundJustFinished;
        this.pointsEarned = pointsEarned;
        this.correctAnswer = correctAnswer;
        this.wasCorrect = wasCorrect;
        this.onTransitionComplete = onTransitionComplete;
        
        createView();
    }

    /**
     * Retourne la vue racine de la transition.
     */
    public StackPane getView() {
        return root;
    }

    /**
     * Crée l'interface de transition avec animations.
     */
    private void createView() {
        root = new StackPane();
        root.setStyle(MainMenu.BG_GRADIENT);

        // Fond semi-transparent animé
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        
        // Conteneur principal
        VBox mainContainer = new VBox(25);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(40));
        mainContainer.setMaxWidth(700);

        // --- TITRE DE LA MANCHE ---
        Label roundTitle = createRoundTitle();
        
        // --- CARTE DE RÉSULTAT ---
        VBox resultCard = createResultCard();
        
        // --- CLASSEMENT ---
        VBox rankingCard = createRankingCard();
        
        // --- COMPTE À REBOURS ---
        VBox countdownCard = createCountdownCard();

        mainContainer.getChildren().addAll(roundTitle, resultCard, rankingCard, countdownCard);
        
        root.getChildren().addAll(overlay, mainContainer);
        
        // Démarrer les animations
        animateEntrance(roundTitle, resultCard, rankingCard, countdownCard);
        startCountdown();
    }

    /**
     * Crée le titre de la manche terminée.
     */
    private Label createRoundTitle() {
        int displayRound = controller.isDuelMode() 
            ? (roundJustFinished / 2) + 1 
            : roundJustFinished + 1;
        
        Label title = new Label("🎵 MANCHE " + displayRound + " TERMINÉE 🎵");
        title.setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, 36));
        title.setTextFill(Color.WHITE);
        title.setStyle(
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 15, 0, 0, 5); " +
            "-fx-padding: 10;"
        );
        
        return title;
    }

    /**
     * Crée la carte affichant le résultat de la manche.
     */
    private VBox createResultCard() {
        VBox card = new VBox(20);
        card.setAlignment(Pos.CENTER);
        card.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.95); " +
            "-fx-background-radius: 25; " +
            "-fx-padding: 30; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 20, 0, 0, 10);"
        );
        card.setMaxWidth(600);

        // Icône et message de résultat
        String resultIcon = wasCorrect ? "✅" : "❌";
        String resultText = wasCorrect ? "BONNE RÉPONSE !" : "DOMMAGE...";
        Color resultColor = wasCorrect ? Color.web("#2ecc71") : Color.web("#e74c3c");
        
        Label resultLabel = new Label(resultIcon + " " + resultText);
        resultLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 28));
        resultLabel.setTextFill(resultColor);

        // Points gagnés
        Label pointsLabel = new Label("+" + pointsEarned + " points");
        pointsLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 32));
        pointsLabel.setTextFill(Color.web("#6C5CE7"));
        
        // Réponse correcte
        Label answerLabel = new Label("C'était : " + correctAnswer);
        answerLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 18));
        answerLabel.setTextFill(Color.web("#2d3436"));
        answerLabel.setWrapText(true);
        answerLabel.setMaxWidth(550);
        answerLabel.setAlignment(Pos.CENTER);
        
        // Séparateur
        Region separator = new Region();
        separator.setStyle("-fx-background-color: #dfe6e9;");
        separator.setPrefHeight(2);
        separator.setMaxWidth(500);

        card.getChildren().addAll(resultLabel, pointsLabel, separator, answerLabel);
        
        return card;
    }

    /**
     * Crée la carte affichant le classement actuel.
     */
    private VBox createRankingCard() {
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.9); " +
            "-fx-background-radius: 20; " +
            "-fx-padding: 25; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 15, 0, 0, 8);"
        );
        card.setMaxWidth(600);

        Label rankingTitle = new Label("📊 CLASSEMENT ACTUEL");
        rankingTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        rankingTitle.setTextFill(Color.web("#2d3436"));

        // Trier les joueurs par score
        List<Player> sortedPlayers = controller.getPlayers().stream()
            .sorted(Comparator.comparingInt(Player::getScore).reversed())
            .collect(Collectors.toList());

        VBox playersBox = new VBox(10);
        playersBox.setAlignment(Pos.CENTER);

        int rank = 1;
        for (Player player : sortedPlayers) {
            HBox playerRow = createPlayerRow(rank, player);
            playersBox.getChildren().add(playerRow);
            rank++;
        }

        card.getChildren().addAll(rankingTitle, playersBox);
        
        return card;
    }

    /**
     * Crée une ligne de joueur pour le classement.
     */
    private HBox createPlayerRow(int rank, Player player) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 15, 10, 15));
        row.setStyle(
            "-fx-background-color: " + (rank == 1 ? "#fff9e6" : "#f8f9fa") + "; " +
            "-fx-background-radius: 10; " +
            "-fx-border-color: " + (rank == 1 ? "#f1c40f" : "#e0e0e0") + "; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 10;"
        );
        row.setMinWidth(500);

        // Médaille ou position
        String medal = (rank == 1) ? "🥇" : (rank == 2) ? "🥈" : String.valueOf(rank);
        Label rankLabel = new Label(medal);
        rankLabel.setFont(Font.font("Segoe UI Emoji", 24));
        rankLabel.setMinWidth(40);

        // Nom du joueur
        Label nameLabel = new Label(player.getName());
        nameLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
        nameLabel.setTextFill(Color.web("#2d3436"));

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Score
        Label scoreLabel = new Label(player.getScore() + " pts");
        scoreLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        scoreLabel.setTextFill(rank == 1 ? Color.web("#f1c40f") : Color.web("#6C5CE7"));

        row.getChildren().addAll(rankLabel, nameLabel, spacer, scoreLabel);
        
        return row;
    }

    /**
     * Crée la carte du compte à rebours.
     */
    private VBox createCountdownCard() {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));

        Label countdownText = new Label("Prochaine manche dans...");
        countdownText.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        countdownText.setTextFill(Color.WHITE);
        
        // Cercle du compte à rebours
        Circle countdownCircle = new Circle(40);
        countdownCircle.setFill(Color.web("#6C5CE7"));
        countdownCircle.setStroke(Color.WHITE);
        countdownCircle.setStrokeWidth(3);
        
        Label countdownLabel = new Label(String.valueOf(countdown));
        countdownLabel.setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, 48));
        countdownLabel.setTextFill(Color.WHITE);
        
        StackPane countdownStack = new StackPane(countdownCircle, countdownLabel);
        
        card.getChildren().addAll(countdownText, countdownStack);
        card.setId("countdownCard"); // Pour la récupérer dans le timeline
        
        return card;
    }

    /**
     * Anime l'entrée des éléments avec des transitions fluides.
     */
    private void animateEntrance(javafx.scene.Node... nodes) {
        int delay = 0;
        for (javafx.scene.Node node : nodes) {
            // Translation de bas en haut
            TranslateTransition tt = new TranslateTransition(Duration.millis(800), node);
            tt.setFromY(100);
            tt.setToY(0);
            
            // Fondu
            FadeTransition ft = new FadeTransition(Duration.millis(800), node);
            ft.setFromValue(0);
            ft.setToValue(1);
            
            // Scale (zoom)
            ScaleTransition st = new ScaleTransition(Duration.millis(800), node);
            st.setFromX(0.8);
            st.setFromY(0.8);
            st.setToX(1.0);
            st.setToY(1.0);
            
            ParallelTransition pt = new ParallelTransition(tt, ft, st);
            pt.setDelay(Duration.millis(delay));
            pt.play();
            
            delay += 150;
        }
    }

    /**
     * Démarre le compte à rebours de 3 secondes.
     */
    private void startCountdown() {
        Timeline countdownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            countdown--;
            
            // Mettre à jour le label du compte à rebours
            VBox countdownCard = (VBox) root.lookup("#countdownCard");
            if (countdownCard != null) {
                StackPane stack = (StackPane) countdownCard.getChildren().get(1);
                Label countdownLabel = (Label) stack.getChildren().get(1);
                countdownLabel.setText(String.valueOf(countdown));
                
                // Animation du chiffre
                ScaleTransition st = new ScaleTransition(Duration.millis(300), countdownLabel);
                st.setFromX(1.5);
                st.setFromY(1.5);
                st.setToX(1.0);
                st.setToY(1.0);
                st.play();
            }
            
            if (countdown <= 0) {
                // 🆕 Faire disparaître la transition avec fadeOut
                FadeTransition fadeOut = new FadeTransition(Duration.millis(400), root);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(e -> {
                    // Exécuter le callback qui va retirer l'overlay et passer à la manche suivante
                    if (onTransitionComplete != null) {
                        onTransitionComplete.run();
                    }
                });
                fadeOut.play();
            }
        }));
        
        countdownTimeline.setCycleCount(3);
        countdownTimeline.play();
    }
}