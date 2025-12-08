package com.blindtest.ui;

import com.blindtest.model.Score;
import com.blindtest.service.ScoreService;  // ← Utiliser ScoreService au lieu de ScoreManager
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.List;

public class LeaderboardView {
    private Scene scene;
    private Stage stage;

    public LeaderboardView(Stage stage) {
        this.stage = stage;
        initializeScene();
    }

    private void initializeScene() {
        Label titleLabel = new Label("🏆 Classement");
        titleLabel.setFont(new Font("Arial", 24));

        VBox leaderboardBox = new VBox(10);
        leaderboardBox.setAlignment(Pos.CENTER);
        leaderboardBox.setPadding(new Insets(20));

        List<Score> leaderboard = ScoreService.getLeaderboard();  // ← Utiliser ScoreService

        if (leaderboard.isEmpty()) {
            Label emptyLabel = new Label("Aucun score enregistré pour le moment.");
            emptyLabel.setFont(new Font("Arial", 14));
            leaderboardBox.getChildren().add(emptyLabel);
        } else {
            // En-tête
            HBox headerBox = new HBox(20);
            headerBox.setAlignment(Pos.CENTER);
            headerBox.setPadding(new Insets(10));
            headerBox.setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 5;");

            Label rankHeader = new Label("Rang");
            rankHeader.setFont(new Font("Arial", 14));
            rankHeader.setStyle("-fx-font-weight: bold;");
            rankHeader.setMinWidth(50);

            Label pseudoHeader = new Label("Pseudo");
            pseudoHeader.setFont(new Font("Arial", 14));
            pseudoHeader.setStyle("-fx-font-weight: bold;");
            pseudoHeader.setMinWidth(150);

            Label scoreHeader = new Label("Score");
            scoreHeader.setFont(new Font("Arial", 14));
            scoreHeader.setStyle("-fx-font-weight: bold;");
            scoreHeader.setMinWidth(80);

            headerBox.getChildren().addAll(rankHeader, pseudoHeader, scoreHeader);
            leaderboardBox.getChildren().add(headerBox);

            // Lignes de scores
            int rank = 1;
            for (Score entry : leaderboard) {
                HBox entryBox = new HBox(20);
                entryBox.setAlignment(Pos.CENTER);
                entryBox.setPadding(new Insets(10));
                entryBox.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 5;");

                Label rankLabel = new Label(String.valueOf(rank));
                rankLabel.setFont(new Font("Arial", 14));
                rankLabel.setMinWidth(50);

                Label pseudoLabel = new Label(entry.getPseudo());
                pseudoLabel.setFont(new Font("Arial", 14));
                pseudoLabel.setMinWidth(150);

                Label scoreLabel = new Label(String.valueOf(entry.getScore()));
                scoreLabel.setFont(new Font("Arial", 14));
                scoreLabel.setMinWidth(80);

                entryBox.getChildren().addAll(rankLabel, pseudoLabel, scoreLabel);
                leaderboardBox.getChildren().add(entryBox);

                rank++;
            }
        }

        Button backButton = new Button("Retour");
        backButton.setStyle("-fx-min-width: 150px; -fx-padding: 10px;");
        backButton.setOnAction(e -> {
            MainMenu mainMenu = new MainMenu();
            stage.setScene(mainMenu.getScene(stage));
        });

        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(20));
        mainLayout.getChildren().addAll(titleLabel, leaderboardBox, backButton);

        scene = new Scene(mainLayout, 500, 600);
    }

    public Scene getScene() {
        return scene;
    }
}