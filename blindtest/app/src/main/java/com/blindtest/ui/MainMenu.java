package com.blindtest.ui;

import com.blindtest.SceneManager;
import com.blindtest.controller.GameController;
import com.blindtest.model.Player;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainMenu {

    public void start(Stage primaryStage) {
        SceneManager.switchView(createMenuContent(primaryStage));
    }

    private Parent createMenuContent(Stage primaryStage) {
        Button btnSolo = createButton("Nouveau Jeu (Solo)", e -> showPlayerSetup(primaryStage, 1));
        Button btnDuel = createButton("Duel (2 Joueurs)", e -> showPlayerSetup(primaryStage, 2));
        Button btnLeaderboard = createButton("Classement", e -> showLeaderboard(primaryStage));
        Button btnSettings = createButton("Paramètres", e -> showSettings(primaryStage));
        Button btnQuit = createButton("Quitter", e -> primaryStage.close());

        VBox menuLayout = new VBox(20);
        menuLayout.setAlignment(Pos.CENTER);
        menuLayout.getChildren().addAll(
                new Label("BLINDTEST MUSICAL"),
                btnSolo,
                btnDuel,
                btnLeaderboard,
                btnSettings,
                btnQuit
        );

        return menuLayout;
    }

    private void showPlayerSetup(Stage primaryStage, int playerCount) {
        VBox setupLayout = new VBox(15);
        setupLayout.setAlignment(Pos.CENTER);

        TextField player1Field = new TextField();
        player1Field.setPromptText("Pseudo Joueur 1");
        player1Field.setMaxWidth(200);

        TextField player2Field = new TextField();
        player2Field.setPromptText("Pseudo Joueur 2");
        player2Field.setMaxWidth(200);

        Label title = new Label(playerCount == 1 ? "Mode Solo (1 Joueur)" : "Mode Duel (2 Joueurs)");

        setupLayout.getChildren().add(title);
        setupLayout.getChildren().add(player1Field);

        if (playerCount == 2) {
            setupLayout.getChildren().add(player2Field);
        }

        Button btnStartGame = createButton("Démarrer la partie", e -> {
            String name1 = player1Field.getText().trim();
            if (name1.isEmpty()) { name1 = "Joueur 1"; }

            List<Player> players = new ArrayList<>();
            players.add(new Player(name1));

            if (playerCount == 2) {
                String name2 = player2Field.getText().trim();
                if (name2.isEmpty()) { name2 = "Joueur 2"; }
                players.add(new Player(name2));
            }

            launchGame(primaryStage, players);
        });

        Button btnBack = createButton("Retour au Menu", e -> SceneManager.switchView(createMenuContent(primaryStage)));

        setupLayout.getChildren().addAll(btnStartGame, btnBack);

        SceneManager.switchView(setupLayout);
    }

    private void launchGame(Stage primaryStage, List<Player> players) {
        System.out.println("Lancement de la partie pour : " + players.stream().map(Player::getName).collect(Collectors.joining(", ")));

        GameController gameController = new GameController(players);
        gameController.startGame();

        try {
            GameView gameView = new GameView(gameController);
            SceneManager.switchView(gameView.getRootNode());
            primaryStage.setTitle("BlindTest - Partie en cours");

        } catch (Exception ex) {
            System.err.println("ERREUR: Impossible de passer à la GameView. " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void showLeaderboard(Stage primaryStage) {
        try {
            LeaderboardView lbView = new LeaderboardView(primaryStage);
            SceneManager.switchView(lbView.getScene().getRoot());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSettings(Stage primaryStage) {
        try {
            SettingsView settingsView = new SettingsView(primaryStage);
            SceneManager.switchView(settingsView.getScene().getRoot());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Button createButton(String text, EventHandler<ActionEvent> handler) {
        Button button = new Button(text);
        button.setMinWidth(200);
        button.setOnAction(handler);
        return button;
    }
}