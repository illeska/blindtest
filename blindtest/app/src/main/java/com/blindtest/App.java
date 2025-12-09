package com.blindtest;

import javafx.application.Application;
import javafx.stage.Stage;
import com.blindtest.ui.MainMenu;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
    
        StackPane mainLayout = new StackPane();
    
        SceneManager.setRootPane(mainLayout);

        Scene scene = new Scene(mainLayout, 800, 600); 
        primaryStage.setTitle("BlindTest - Illeska");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
