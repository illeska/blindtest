package com.blindtest.ui;

import java.util.List;

import com.blindtest.model.Score;
import com.blindtest.service.ExportService;
import com.blindtest.service.ScoreService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LeaderboardView {
    private Stage stage;
    private TableView<Score> tableView;
    private ComboBox<String> modeFilter;

    public LeaderboardView(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {
        VBox root = new VBox(20);
        root.setStyle(MainMenu.BG_GRADIENT);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(30));

        Label title = new Label("🏆 HALL OF FAME");
        title.setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, 36));
        title.setTextFill(Color.WHITE);

        // --- BARRE D'OUTILS ---
        HBox toolbar = new HBox(15);
        toolbar.setAlignment(Pos.CENTER);
        toolbar.setStyle("-fx-background-color: rgba(255,255,255,0.8); -fx-background-radius: 15; -fx-padding: 10;");
        toolbar.setMaxWidth(800);

        modeFilter = new ComboBox<>();
        modeFilter.getItems().addAll("Tous", "Solo", "Duel");
        modeFilter.setValue("Tous");
        modeFilter.setOnAction(e -> refreshScores());

        Button exportBtn = new Button("📄 Export CSV");
        exportBtn.setStyle("-fx-background-color: #0984e3; -fx-text-fill: white; -fx-font-weight: bold;");
        exportBtn.setOnAction(e -> exportData());

        toolbar.getChildren().addAll(new Label("Filtre:"), modeFilter, new Region(), exportBtn);
        HBox.setHgrow(toolbar.getChildren().get(2), Priority.ALWAYS);

        // --- TABLEAU DES SCORES (Refait proprement) ---
        tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setStyle("-fx-background-color: rgba(255,255,255,0.9); -fx-background-radius: 10;");

        TableColumn<Score, String> pseudoCol = new TableColumn<>("Joueur");
        pseudoCol.setCellValueFactory(new PropertyValueFactory<>("pseudo"));
        pseudoCol.setStyle("-fx-alignment: CENTER-LEFT; -fx-font-weight: bold;");

        TableColumn<Score, Integer> scoreCol = new TableColumn<>("Score");
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        scoreCol.setStyle("-fx-alignment: CENTER; -fx-text-fill: #6C5CE7; -fx-font-weight: bold; -fx-font-size: 14px;");

        TableColumn<Score, String> modeCol = new TableColumn<>("Mode");
        modeCol.setCellValueFactory(new PropertyValueFactory<>("mode"));
        modeCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Score, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate().toLocalDate().toString()));
        dateCol.setStyle("-fx-alignment: CENTER; -fx-text-fill: gray;");

        tableView.getColumns().addAll(pseudoCol, scoreCol, modeCol, dateCol);
        
        refreshScores(); // Charger les données

        // Bouton Retour avec la nouvelle méthode startWithoutIntro
        Button backBtn = new Button("RETOUR MENU");
        backBtn.setStyle("-fx-background-color: white; -fx-text-fill: #ff7675; -fx-background-radius: 30; -fx-padding: 10 30; -fx-font-weight: bold; -fx-cursor: hand;");
        backBtn.setOnAction(e -> new MainMenu().startWithoutIntro(stage));

        root.getChildren().addAll(title, toolbar, tableView, backBtn);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        return new Scene(root, 1000, 750);
    }

    private void refreshScores() {
        String mode = modeFilter.getValue();
        List<Score> scores;
        
        if (mode.equals("Tous")) {
            scores = ScoreService.getLeaderboard(100);
        } else {
            scores = ScoreService.getLeaderboardByMode(mode, 100);
        }

        ObservableList<Score> data = FXCollections.observableArrayList(scores);
        tableView.setItems(data);
    }

    private void exportData() {
        try {
            String filename = ExportService.generateExportFilename("leaderboard", "csv");
            ExportService.exportLeaderboardToCSV(filename);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Export réussi : " + filename);
            alert.show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur export : " + e.getMessage()).show();
        }
    }
}