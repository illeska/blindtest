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
    private Label statsLabel;

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

        Button exportCsvBtn = new Button("📄 CSV");
        exportCsvBtn.setStyle("-fx-background-color: #0984e3; -fx-text-fill: white; -fx-font-weight: bold;");
        exportCsvBtn.setOnAction(e -> exportData("csv"));

        Button exportJsonBtn = new Button("📦 JSON");
        exportJsonBtn.setStyle("-fx-background-color: #6C5CE7; -fx-text-fill: white; -fx-font-weight: bold;");
        exportJsonBtn.setOnAction(e -> exportData("json"));

        toolbar.getChildren().addAll(
            new Label("Filtre:"), modeFilter, 
            new Region(), 
            exportCsvBtn, exportJsonBtn
        );
        HBox.setHgrow(toolbar.getChildren().get(2), Priority.ALWAYS);

        // 🆕 STATISTIQUES
        statsLabel = new Label("");
        statsLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        statsLabel.setTextFill(Color.WHITE);
        statsLabel.setWrapText(true);
        statsLabel.setMaxWidth(800);

        // --- TABLEAU DES SCORES ---
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

        TableColumn<Score, String> genreCol = new TableColumn<>("Genre");
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));
        genreCol.setStyle("-fx-alignment: CENTER;");

        // 🆕 COLONNES STATISTIQUES
        TableColumn<Score, String> successRateCol = new TableColumn<>("Réussite");
        successRateCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.format("%.1f%%", cellData.getValue().getSuccessRate()))
        );
        successRateCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Score, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDate().toLocalDate().toString())
        );
        dateCol.setStyle("-fx-alignment: CENTER; -fx-text-fill: gray;");

        tableView.getColumns().addAll(pseudoCol, scoreCol, modeCol, genreCol, successRateCol, dateCol);
        
        refreshScores();

        Button backBtn = new Button("RETOUR MENU");
        backBtn.setStyle("-fx-background-color: white; -fx-text-fill: #ff7675; -fx-background-radius: 30; -fx-padding: 10 30; -fx-font-weight: bold; -fx-cursor: hand;");
        backBtn.setOnAction(e -> new MainMenu().startWithoutIntro(stage));

        root.getChildren().addAll(title, toolbar, statsLabel, tableView, backBtn);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        return new Scene(root, 1100, 750);
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
        
        // 🆕 Mise à jour des statistiques
        updateStatistics(mode);
    }

    private void updateStatistics(String mode) {
        ScoreService.ScoreStatistics stats;
        
        if (mode.equals("Tous")) {
            stats = ScoreService.getGlobalStatistics();
        } else {
            stats = ScoreService.getStatisticsByMode(mode);
        }
        
        if (stats.getTotalGames() > 0) {
            statsLabel.setText(String.format(
                "📊 Statistiques (%s) : %d parties | Score moyen: %.1f | Meilleur: %d | Taux réussite: %.1f%% | Titres: %.1f%% | Artistes: %.1f%%",
                mode,
                stats.getTotalGames(),
                stats.getAverageScore(),
                stats.getMaxScore(),
                stats.getAvgSuccessRate(),
                stats.getAvgTitleSuccessRate(),
                stats.getAvgArtistSuccessRate()
            ));
        } else {
            statsLabel.setText("📊 Aucune donnée disponible pour ce filtre");
        }
    }

    private void exportData(String format) {
        try {
            String filename = ExportService.generateExportFilename("leaderboard", format);
            
            if (format.equals("csv")) {
                ExportService.exportLeaderboardToCSV(filename);
            } else {
                ExportService.exportLeaderboardToJSON(filename);
            }
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION, 
                "Export réussi !\nFichier : exports/" + filename);
            alert.setTitle("Export réussi");
            alert.show();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, 
                "Erreur lors de l'export : " + e.getMessage());
            alert.setTitle("Erreur");
            alert.show();
        }
    }
}