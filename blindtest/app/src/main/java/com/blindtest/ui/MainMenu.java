package com.blindtest.ui;

import com.blindtest.controller.GameController;
import com.blindtest.model.Player;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Menu principal de l'application BlindTest.
 * Gère l'initialisation de l'UI et le lancement des différents modes de jeu (Solo/Duel).
 */
public class MainMenu {

    // Cette méthode est appelée par App.java pour démarrer le menu
    public void start(Stage primaryStage) {
        primaryStage.setTitle("BlindTest - Menu Principal");
        primaryStage.setScene(createMenuScene(primaryStage));
        primaryStage.show();
    }

    // --- CORRECTION : Méthode publique pour permettre aux autres vues de revenir au menu ---
    // Cette méthode résout l'erreur "cannot find symbol method getScene(Stage)"
    public Scene getScene(Stage primaryStage) {
        return createMenuScene(primaryStage);
    }

    // Création de la scène du menu principal (factorisée pour être réutilisée)
    private Scene createMenuScene(Stage primaryStage) {
        // --- Éléments du Menu Principal ---
        
        // Boutons qui lancent l'écran de saisie des pseudos (1 ou 2 joueurs)
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

        return new Scene(menuLayout, 400, 500);
    }
    
    // --- Écran de Saisie des Joueurs (Setup) ---

    /**
     * Affiche l'écran de saisie des noms de joueurs pour le mode sélectionné.
     */
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
        
        // Affiche la zone de saisie du joueur 2 seulement en mode Duel
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

            // 🔥 Lancement de la logique de jeu et de l'écran principal
            launchGame(primaryStage, players);
        });

        Button btnBack = createButton("Retour au Menu", e -> start(primaryStage));

        setupLayout.getChildren().addAll(btnStartGame, btnBack);
        
        Scene setupScene = new Scene(setupLayout, 400, 500);
        primaryStage.setScene(setupScene);
    }

    // --- Logique de Lancement du Jeu ---

    /**
     * Crée le GameController, lance la partie et bascule vers GameView.
     */
    private void launchGame(Stage primaryStage, List<Player> players) {
        System.out.println("Lancement de la partie pour : " + players.stream().map(Player::getName).collect(Collectors.joining(", ")));
        
        // 1. Initialiser le contrôleur
        GameController gameController = new GameController(players); 

        // 2. Démarrer la logique du jeu (lance le premier extrait audio)
        gameController.startGame(); 

        // 3. Basculer vers l'écran de jeu
        try {
            // Création de la vue du jeu (nécessite GameView.java)
            GameView gameView = new GameView(gameController);
            Scene gameScene = new Scene(gameView.getRootNode(), 800, 600); 

            primaryStage.setScene(gameScene);
            primaryStage.setTitle("BlindTest - Partie en cours");
            
        } catch (Exception ex) {
            System.err.println("ERREUR: Impossible de passer à la GameView. Est-elle bien implémentée ? " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void showLeaderboard(Stage primaryStage) {
        try {
            LeaderboardView lbView = new LeaderboardView(primaryStage);
            primaryStage.setScene(lbView.getScene());
            primaryStage.show();
                    
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSettings(Stage primaryStage) {
        try {
            
            SettingsView settingsView = new SettingsView(primaryStage);
            primaryStage.setScene(settingsView.getScene());
            primaryStage.show();

            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // --- Utilitaire ---
    private Button createButton(String text, EventHandler<ActionEvent> handler) {
        Button button = new Button(text);
        button.setMinWidth(200);
        button.setOnAction(handler);
        return button;
    }
}