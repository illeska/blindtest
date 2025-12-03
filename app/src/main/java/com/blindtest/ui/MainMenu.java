package com.blindtest.ui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MainMenu extends Application {

    private Scene scene;

    public Scene getScene(Stage stage) {
        if (scene == null) {
            initializeScene(stage);
        }
        return scene;
    }

    private void initializeScene(Stage stage) {
        stage.setTitle("BlindTest 🎵");

        // Titre
        Label titleLabel = new Label("BlindTest");
        titleLabel.setFont(new Font("Arial", 24));

        // Création des boutons
        Button playSoloButton = new Button("Mode Solo");
        Button playDuelButton = new Button("Mode Duel");
        Button leaderboardButton = new Button("Classement");
        Button settingsButton = new Button("Paramètres");
        Button quitButton = new Button("Quitter");

        // Styles CSS basiques pour unifier la taille (optionnel mais recommandé)
        String buttonStyle = "-fx-min-width: 150px; -fx-padding: 10px;";
        playSoloButton.setStyle(buttonStyle);
        playDuelButton.setStyle(buttonStyle);
        leaderboardButton.setStyle(buttonStyle);
        settingsButton.setStyle(buttonStyle);
        quitButton.setStyle(buttonStyle);

        // Actions des boutons
        playSoloButton.setOnAction(e -> {
            GameView gameView = new GameView(stage);
            gameView.startGame();
            stage.setScene(gameView.getScene());
        });

        playDuelButton.setOnAction(e -> {
            System.out.println("Lancement Mode Duel (À implémenter par Achraf)");
            // TODO: Connecter à la vue Duel quand elle sera prête
        });

        leaderboardButton.setOnAction(e -> {
             System.out.println("Ouverture Classement (À implémenter par Léo)");
             // TODO: Connecter à LeaderboardView
        });

        settingsButton.setOnAction(e -> {
             System.out.println("Ouverture Paramètres (À implémenter par Léo)");
             // TODO: Connecter à SettingsView
        });

        quitButton.setOnAction(e -> stage.close());

        // Layout vertical centré
        VBox layout = new VBox(15); // Espacement de 15px
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(titleLabel, playSoloButton, playDuelButton, leaderboardButton, settingsButton, quitButton);

        scene = new Scene(layout, 400, 500);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setScene(getScene(primaryStage));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}