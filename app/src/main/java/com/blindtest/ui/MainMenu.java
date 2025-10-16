package com.blindtest.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainMenu extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("BlindTest 🎵");

        Button playButton = new Button("Jouer");
        Button quitButton = new Button("Quitter");

        // Actions des boutons
        playButton.setOnAction(e -> System.out.println("Démarrage du jeu..."));
        quitButton.setOnAction(e -> primaryStage.close());

        VBox layout = new VBox(20);
        layout.getChildren().addAll(playButton, quitButton);

        Scene scene = new Scene(layout, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
