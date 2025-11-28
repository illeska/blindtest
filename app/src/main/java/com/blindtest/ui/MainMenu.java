package com.blindtest.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
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

        Button playButton = new Button("Jouer");
        Button quitButton = new Button("Quitter");

        // Actions des boutons
        playButton.setOnAction(e -> {
            GameView gameView = new GameView(stage);
            gameView.startGame();
            stage.setScene(gameView.getScene());
        });
        quitButton.setOnAction(e -> stage.close());

        VBox layout = new VBox(20);
        layout.getChildren().addAll(playButton, quitButton);

        scene = new Scene(layout, 300, 200);
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
