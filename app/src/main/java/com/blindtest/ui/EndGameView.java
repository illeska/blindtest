package com.blindtest.ui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.blindtest.App;
import com.blindtest.controller.GameController;
import com.blindtest.model.Player;
import com.blindtest.model.Score;
import com.blindtest.service.ScoreService;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class EndGameView {
    
    private final GameController controller;
    private VBox root;

    /**
     * Constructeur de la vue de fin de partie.
     */
    public EndGameView(GameController controller) {
        this.controller = controller;
        App.getAudioService().startMenuMusic();
        createView();
    }

    /**
     * Retourne la vue racine.
     */
    public VBox getView() {
        return root;
    }

    /**
     * Crée l'interface de fin de partie enrichie.
     */
    private void createView() {
        root = new VBox(30);
        root.setStyle(MainMenu.BG_GRADIENT);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        // Titre avec animation
        Label title = new Label("🏆 PARTIE TERMINÉE 🏆");
        title.setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, 42));
        title.setTextFill(Color.WHITE);
        title.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 15, 0, 0, 5);");
        
        // Classement des joueurs
        VBox podiumCard = createPodiumCard();
        
        // 🆕 Statistiques détaillées
        VBox statsCard = createStatisticsCard();
        
        // 🆕 Badges et réalisations
        HBox badgesBox = createBadgesBox();
        
        // Boutons d'action
        HBox buttons = createActionButtons();

        root.getChildren().addAll(title, podiumCard, statsCard, badgesBox, buttons);

        // Animations d'entrée
        animateEntrance(title, podiumCard, statsCard, badgesBox, buttons);
    }

    /**
     * Crée la carte du podium avec le classement.
     */
    private VBox createPodiumCard() {
        VBox card = new VBox(20);
        card.setStyle(MainMenu.CARD_STYLE);
        card.setPadding(new Insets(35));
        card.setMaxWidth(700);
        card.setAlignment(Pos.CENTER);

        Label subtitle = new Label("🏅 CLASSEMENT FINAL");
        subtitle.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        subtitle.setTextFill(Color.web("#2d3436"));

        // Trier les joueurs
        List<Player> sorted = controller.getPlayers().stream()
                .sorted(Comparator.comparingInt(Player::getScore).reversed())
                .collect(Collectors.toList());

        VBox playersBox = new VBox(15);
        playersBox.setAlignment(Pos.CENTER);

        for (int i = 0; i < sorted.size(); i++) {
            Player p = sorted.get(i);
            saveScore(p);

            HBox row = createPlayerRow(i, p, sorted.size() > 1);
            playersBox.getChildren().add(row);
        }

        card.getChildren().addAll(subtitle, playersBox);
        return card;
    }

    /**
     * Crée une ligne de joueur dans le classement.
     */
    private HBox createPlayerRow(int position, Player player, boolean isDuel) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(15, 20, 15, 20));
        
        // Style selon la position
        String bgColor = position == 0 ? "#fff9e6" : "#f8f9fa";
        String borderColor = position == 0 ? "#f1c40f" : "#dfe6e9";
        
        row.setStyle(
            "-fx-background-color: " + bgColor + "; " +
            "-fx-background-radius: 15; " +
            "-fx-border-color: " + borderColor + "; " +
            "-fx-border-width: 3; " +
            "-fx-border-radius: 15; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 4);"
        );
        row.setMinWidth(600);

        // Médaille
        String medal = (position == 0) ? "🥇" : (position == 1) ? "🥈" : "🥉";
        Label rankLabel = new Label(medal);
        rankLabel.setFont(Font.font("Segoe UI Emoji", 32));
        rankLabel.setMinWidth(50);

        // Nom du joueur
        VBox nameBox = new VBox(5);
        Label name = new Label(player.getName());
        name.setFont(Font.font("Verdana", FontWeight.BOLD, 22));
        name.setTextFill(Color.web("#2d3436"));
        
        // Titre du vainqueur
        if (position == 0) {
            Label winnerLabel = new Label(isDuel ? "🎊 VAINQUEUR 🎊" : "🎊 CHAMPION 🎊");
            winnerLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
            winnerLabel.setTextFill(Color.web("#f1c40f"));
            nameBox.getChildren().addAll(name, winnerLabel);
        } else {
            nameBox.getChildren().add(name);
        }

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Score avec icône
        VBox scoreBox = new VBox(3);
        scoreBox.setAlignment(Pos.CENTER_RIGHT);
        
        Label score = new Label(player.getScore() + " pts");
        score.setFont(Font.font("Verdana", FontWeight.BOLD, 26));
        score.setTextFill(position == 0 ? Color.web("#f1c40f") : Color.web("#6C5CE7"));
        
        scoreBox.getChildren().add(score);

        row.getChildren().addAll(rankLabel, nameBox, spacer, scoreBox);
        return row;
    }

    /**
     * 🆕 Crée la carte des statistiques détaillées.
     */
    private VBox createStatisticsCard() {
        VBox card = new VBox(20);
        card.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.9); " +
            "-fx-background-radius: 20; " +
            "-fx-padding: 30; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.12), 12, 0, 0, 6);"
        );
        card.setMaxWidth(700);
        card.setAlignment(Pos.CENTER);

        Label statsTitle = new Label("📈 STATISTIQUES DE LA PARTIE");
        statsTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        statsTitle.setTextFill(Color.web("#2d3436"));

        // Récupérer les statistiques du contrôleur
        int totalRounds = controller.isDuelMode() 
            ? controller.getNumberOfRounds() * 2 
            : controller.getNumberOfRounds();
        
        // Calculer les stats pour tous les joueurs
        int totalCorrect = 0;
        int totalPossible = totalRounds * 2; // titre + artiste
        
        // Stats globales
        VBox statsBox = new VBox(15);
        statsBox.setAlignment(Pos.CENTER);
        
        // Nombre de manches
        HBox roundsStat = createStatRow("🎵", "Manches jouées", 
            String.valueOf(controller.isDuelMode() ? totalRounds / 2 : totalRounds));
        
        // Mode de jeu
        HBox modeStat = createStatRow("🎮", "Mode de jeu", 
            controller.isDuelMode() ? "Duel (2 joueurs)" : "Solo");
        
        // Genre musical
        HBox genreStat = createStatRow("🎸", "Genre", 
            controller.getSettings().getDefaultGenre());

        statsBox.getChildren().addAll(roundsStat, modeStat, genreStat);

        // 🆕 Barres de progression par joueur
        VBox playersStatsBox = new VBox(15);
        playersStatsBox.setAlignment(Pos.CENTER);
        
        for (Player player : controller.getPlayers()) {
            VBox playerStat = createPlayerStatBar(player, totalRounds);
            playersStatsBox.getChildren().add(playerStat);
        }

        card.getChildren().addAll(statsTitle, statsBox, playersStatsBox);
        return card;
    }

    /**
     * 🆕 Crée une ligne de statistique.
     */
    private HBox createStatRow(String icon, String label, String value) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 15, 8, 15));
        row.setStyle(
            "-fx-background-color: #f8f9fa; " +
            "-fx-background-radius: 10; " +
            "-fx-border-color: #e0e0e0; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 10;"
        );
        row.setMinWidth(600);

        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Segoe UI Emoji", 20));
        iconLabel.setMinWidth(35);

        Label labelText = new Label(label);
        labelText.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
        labelText.setTextFill(Color.web("#636e72"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label valueText = new Label(value);
        valueText.setFont(Font.font("Verdana", FontWeight.BOLD, 16));
        valueText.setTextFill(Color.web("#2d3436"));

        row.getChildren().addAll(iconLabel, labelText, spacer, valueText);
        return row;
    }

    /**
     * 🆕 Crée une barre de progression pour un joueur.
     */
    private VBox createPlayerStatBar(Player player, int totalRounds) {
        VBox statBox = new VBox(8);
        statBox.setAlignment(Pos.CENTER_LEFT);
        statBox.setPadding(new Insets(12));
        statBox.setStyle(
            "-fx-background-color: #f8f9fa; " +
            "-fx-background-radius: 12; " +
            "-fx-border-color: #dfe6e9; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 12;"
        );
        statBox.setMinWidth(600);

        // Nom et score
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label playerName = new Label(player.getName());
        playerName.setFont(Font.font("Verdana", FontWeight.BOLD, 16));
        playerName.setTextFill(Color.web("#2d3436"));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label scoreLabel = new Label(player.getScore() + " points");
        scoreLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 16));
        scoreLabel.setTextFill(Color.web("#6C5CE7"));

        header.getChildren().addAll(playerName, spacer, scoreLabel);

        // Barre de progression
        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(550);
        progressBar.setPrefHeight(20);
        
        // Calculer le pourcentage (score max possible = totalRounds * 3 avec bonus)
        double maxPossible = totalRounds * 3.0;
        double progress = Math.min(1.0, player.getScore() / maxPossible);
        progressBar.setProgress(progress);
        
        // Style de la barre
        String barColor = progress > 0.7 ? "#2ecc71" : progress > 0.4 ? "#f39c12" : "#e74c3c";
        progressBar.setStyle(
            "-fx-accent: " + barColor + "; " +
            "-fx-control-inner-background: #ecf0f1; " +
            "-fx-background-radius: 10; " +
            "-fx-border-radius: 10;"
        );

        // Pourcentage
        Label percentLabel = new Label(String.format("%.0f%% de réussite", progress * 100));
        percentLabel.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));
        percentLabel.setTextFill(Color.web("#636e72"));

        statBox.getChildren().addAll(header, progressBar, percentLabel);
        return statBox;
    }

    /**
     * 🆕 Crée la boîte des badges et réalisations.
     */
    private HBox createBadgesBox() {
        HBox badgesBox = new HBox(20);
        badgesBox.setAlignment(Pos.CENTER);
        badgesBox.setPadding(new Insets(20));

        // Déterminer les badges gagnés
        List<Player> sorted = controller.getPlayers().stream()
                .sorted(Comparator.comparingInt(Player::getScore).reversed())
                .collect(Collectors.toList());

        Player topPlayer = sorted.get(0);
        int topScore = topPlayer.getScore();
        int totalRounds = controller.isDuelMode() 
            ? controller.getNumberOfRounds() * 2 
            : controller.getNumberOfRounds();

        // Badge score parfait
        if (topScore >= totalRounds * 2) {
            badgesBox.getChildren().add(createBadge("🌟", "PARFAIT", "#f1c40f"));
        }

        // Badge rapide
        if (controller.getSettings().isSpeedBonusEnabled() && topScore >= totalRounds * 2.5) {
            badgesBox.getChildren().add(createBadge("⚡", "ÉCLAIR", "#3498db"));
        }

        // Badge champion
        if (controller.isDuelMode() && sorted.size() > 1) {
            int diff = sorted.get(0).getScore() - sorted.get(1).getScore();
            if (diff >= 5) {
                badgesBox.getChildren().add(createBadge("👑", "DOMINATEUR", "#9b59b6"));
            }
        }

        // Badge participation
        badgesBox.getChildren().add(createBadge("🎵", "MÉLOMANE", "#e74c3c"));

        return badgesBox;
    }

    /**
     * 🆕 Crée un badge.
     */
    private VBox createBadge(String icon, String label, String color) {
        VBox badge = new VBox(8);
        badge.setAlignment(Pos.CENTER);
        badge.setPadding(new Insets(15));
        badge.setStyle(
            "-fx-background-color: " + color + "; " +
            "-fx-background-radius: 15; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);"
        );
        badge.setMinWidth(120);

        Circle iconCircle = new Circle(25);
        iconCircle.setFill(Color.WHITE);
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("Segoe UI Emoji", 24));
        
        javafx.scene.layout.StackPane iconStack = new javafx.scene.layout.StackPane(iconCircle, iconLabel);

        Label badgeLabel = new Label(label);
        badgeLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 11));
        badgeLabel.setTextFill(Color.WHITE);

        badge.getChildren().addAll(iconStack, badgeLabel);
        return badge;
    }

    /**
     * Crée les boutons d'action.
     */
    private HBox createActionButtons() {
        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(10));
        
        // Bouton Rejouer
        Button replayBtn = new Button("🔄 REJOUER");
        styleButton(replayBtn, "#2ecc71");
        replayBtn.setOnAction(event -> {
            App.getAudioService().stop();
            
            // Récupérer les mêmes joueurs pour rejouer
            List<Player> newPlayers = new ArrayList<>();
            for (Player p : controller.getPlayers()) {
                newPlayers.add(new Player(p.getName()));
            }
            
            // Créer une nouvelle partie
            GameController newController = new GameController(newPlayers);
            newController.startGame();
            GameView gameView = new GameView(newController);
            App.setView(gameView.getRootNode());
        });
        
        // Bouton Menu Principal
        Button menuBtn = new Button("🏠 MENU PRINCIPAL");
        styleButton(menuBtn, "#6C5CE7");
        menuBtn.setOnAction(event -> {
            App.getAudioService().stop();
            MainMenu menu = new MainMenu(App.getAudioService());
            App.setView(menu.getView());
            App.getAudioService().startMenuMusic();
        });
        
        // Bouton Quitter
        Button quitBtn = new Button("❌ QUITTER");
        styleButton(quitBtn, "#e74c3c");
        quitBtn.setOnAction(e -> System.exit(0));

        buttons.getChildren().addAll(replayBtn, menuBtn, quitBtn);
        return buttons;
    }

    /**
     * Sauvegarde le score d'un joueur.
     */
    private void saveScore(Player p) {
        try {
            ScoreService.saveScore(new Score(p.getName(), p.getScore()));
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }

    /**
     * Applique un style à un bouton.
     */
    private void styleButton(Button btn, String color) {
        btn.setStyle(
            "-fx-background-color: " + color + "; " +
            "-fx-text-fill: white; " +
            "-fx-background-radius: 30; " +
            "-fx-padding: 15 35; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 16px; " +
            "-fx-cursor: hand; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 8, 0, 0, 4);"
        );
        
        btn.setOnMouseEntered(e -> {
            btn.setStyle(
                "-fx-background-color: " + color + "; " +
                "-fx-text-fill: white; " +
                "-fx-background-radius: 30; " +
                "-fx-padding: 15 35; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 16px; " +
                "-fx-cursor: hand; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 15, 0, 0, 6);"
            );
            btn.setScaleX(1.05);
            btn.setScaleY(1.05);
        });
        
        btn.setOnMouseExited(e -> {
            styleButton(btn, color);
            btn.setScaleX(1.0);
            btn.setScaleY(1.0);
        });
    }

    /**
     * Anime l'entrée des éléments.
     */
    private void animateEntrance(javafx.scene.Node... nodes) {
        int delay = 0;
        for (javafx.scene.Node node : nodes) {
            TranslateTransition tt = new TranslateTransition(Duration.millis(1000), node);
            tt.setFromY(80);
            tt.setToY(0);
            
            FadeTransition ft = new FadeTransition(Duration.millis(1000), node);
            ft.setFromValue(0);
            ft.setToValue(1);
            
            ScaleTransition st = new ScaleTransition(Duration.millis(1000), node);
            st.setFromX(0.9);
            st.setFromY(0.9);
            st.setToX(1.0);
            st.setToY(1.0);
            
            ParallelTransition pt = new ParallelTransition(tt, ft, st);
            pt.setDelay(Duration.millis(delay));
            pt.play();
            
            delay += 150;
        }
    }
}