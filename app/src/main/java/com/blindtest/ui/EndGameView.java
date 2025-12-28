package com.blindtest.ui;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.blindtest.App;
import com.blindtest.controller.GameController;
import com.blindtest.model.Player;
import com.blindtest.model.Score;
import com.blindtest.service.ScoreService;
import com.blindtest.ui.MainMenu;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;


public class EndGameView {
    
    private final Stage stage;
    private final GameController controller;

    public EndGameView(Stage stage, GameController controller) {
        this.stage = stage;
        this.controller = controller;
        App.getAudioService().startMenuMusic();
        createScene();
    }

    private void createScene() {
        VBox root = new VBox(30);
        root.setStyle(MainMenu.BG_GRADIENT);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        Label title = new Label("🏆 RÉSULTATS 🏆");
        title.setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, 40));
        title.setTextFill(Color.WHITE);
        
        VBox card = new VBox(15);
        card.setStyle(MainMenu.CARD_STYLE);
        card.setPadding(new Insets(30));
        card.setMaxWidth(600);
        card.setAlignment(Pos.CENTER);

        // Sauvegarde et Affichage
        List<Player> sorted = controller.getPlayers().stream()
                .sorted(Comparator.comparingInt(Player::getScore).reversed())
                .collect(Collectors.toList());

        for (int i = 0; i < sorted.size(); i++) {
            Player p = sorted.get(i);
            saveScore(p); // Sauvegarde automatique

            HBox row = new HBox(20);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(10));
            row.setStyle("-fx-border-color: transparent transparent #dfe6e9 transparent;");

            String medal = (i==0) ? "🥇" : (i==1) ? "🥈" : "🥉";
            if (i > 2) medal = String.valueOf(i+1);

            Label rank = new Label(medal);
            rank.setFont(Font.font("Segoe UI Emoji", 24));
            
            Label name = new Label(p.getName());
            name.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
            name.setTextFill(Color.web("#2d3436"));
            
            Label score = new Label(p.getScore() + " pts");
            score.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
            score.setTextFill(Color.web(i==0 ? "#e1b12c" : "#636e72"));

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            row.getChildren().addAll(rank, name, spacer, score);
            card.getChildren().add(row);
        }

        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);
        
        Button menuBtn = new Button("MENU PRINCIPAL");
        styleButton(menuBtn, "#6C5CE7");

        menuBtn.setOnAction(event -> {
            App.getAudioService().stop();

            MainMenu menu = new MainMenu(App.getAudioService());
            menu.startWithoutIntro(stage);

            App.getAudioService().startMenuMusic();
        });

        
        Button quitBtn = new Button("QUITTER");
        styleButton(quitBtn, "#ff7675");
        quitBtn.setOnAction(e -> System.exit(0));

        buttons.getChildren().addAll(menuBtn, quitBtn);
        root.getChildren().addAll(title, card, buttons);

        // Animation
        FadeTransition ft = new FadeTransition(Duration.millis(1000), root);
        ft.setFromValue(0); ft.setToValue(1); ft.play();

        stage.setScene(new Scene(root, 900, 700));
    }

    private void saveScore(Player p) {
        try {
            ScoreService.saveScore(new Score(p.getName(), p.getScore()));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void styleButton(Button btn, String color) {
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-background-radius: 30; -fx-padding: 12 25; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand;");
    }
}