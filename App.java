package com.blindtest;

import javafx.application.Application;
import javafx.stage.Stage;
import com.blindtest.ui.MainMenu;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        MainMenu menu = new MainMenu();
        menu.start(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}
