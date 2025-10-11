package com.blindtest;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        Label label = new Label("🎵 Bienvenue dans BlindTest !");
        Scene scene = new Scene(label, 400, 200);
        stage.setTitle("BlindTest App");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
