package com.blindtest.ui;

import java.util.List;

import com.blindtest.App;
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
    private ComboBox<String> genreFilter;
    private Label statsLabel;

    /**
     * Constructeur de la vue du classement.
     * 
     * @param stage Le stage principal de l'application
     */
    public LeaderboardView(Stage stage) {
        this.stage = stage;
    }

    /**
     * Crée et retourne la scène du classement avec les filtres, le tableau des scores et les statistiques.
     * Configure la barre d'outils avec les filtres de mode et genre, et les boutons d'export.
     * 
     * @return La scène JavaFX contenant l'interface du classement
     */
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
        toolbar.setMaxWidth(900);

        // Filtre MODE
        modeFilter = new ComboBox<>();
        modeFilter.getItems().addAll("Tous", "Solo", "Duel");
        modeFilter.setValue("Tous");
        modeFilter.setOnAction(e -> refreshScores());

        // Filtre GENRE
        genreFilter = new ComboBox<>();
        genreFilter.getItems().addAll("Tous", "Pop", "Rock", "Hip-Hop/Rap", "R&B", "Tout Genre");
        genreFilter.setValue("Tous");
        genreFilter.setOnAction(e -> refreshScores());

        Button exportCsvBtn = new Button("📄 CSV");
        exportCsvBtn.setStyle("-fx-background-color: #0984e3; -fx-text-fill: white; -fx-font-weight: bold;");
        exportCsvBtn.setOnAction(e -> exportData("csv"));

        Button exportJsonBtn = new Button("📦 JSON");
        exportJsonBtn.setStyle("-fx-background-color: #6C5CE7; -fx-text-fill: white; -fx-font-weight: bold;");
        exportJsonBtn.setOnAction(e -> exportData("json"));

        toolbar.getChildren().addAll(
            new Label("Mode:"), modeFilter,
            new Label("Genre:"), genreFilter, 
            new Region(), 
            exportCsvBtn, exportJsonBtn
        );
        HBox.setHgrow(toolbar.getChildren().get(4), Priority.ALWAYS); // Ajusté l'index du spacer

        // STATISTIQUES
        statsLabel = new Label("");
        statsLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        statsLabel.setTextFill(Color.WHITE);
        statsLabel.setWrapText(true);
        statsLabel.setMaxWidth(900);

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
        backBtn.setOnAction(e -> {
            MainMenu menu = new MainMenu(App.getAudioService());
            App.setView(menu.getView());
        });

        root.getChildren().addAll(title, toolbar, statsLabel, tableView, backBtn);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        return new Scene(root, 1100, 750);
    }


    /**
     * Rafraîchit l'affichage des scores en fonction des filtres sélectionnés (mode et genre).
     * Met à jour le tableau et les statistiques affichées.
     */
    private void refreshScores() {
        String mode = modeFilter.getValue();
        String genre = genreFilter.getValue();
        List<Score> scores;
        
        // Filtrage selon les deux critères
        if (mode.equals("Tous") && genre.equals("Tous")) {
            // Aucun filtre
            scores = ScoreService.getLeaderboard(100);
        } else if (!mode.equals("Tous") && genre.equals("Tous")) {
            // Filtre mode uniquement
            scores = ScoreService.getLeaderboardByMode(mode, 100);
        } else if (mode.equals("Tous") && !genre.equals("Tous")) {
            // Filtre genre uniquement
            scores = ScoreService.getLeaderboardByGenre(genre, 100);
        } else {
            // Les deux filtres
            scores = ScoreService.getLeaderboardByModeAndGenre(mode, genre, 100);
        }

        ObservableList<Score> data = FXCollections.observableArrayList(scores);
        tableView.setItems(data);
        
        // Mise à jour des statistiques
        updateStatistics(mode, genre);
    }

    /**
     * Met à jour l'affichage des statistiques en fonction des filtres appliqués.
     * Calcule et affiche les statistiques globales ou filtrées (nombre de parties, scores moyens, taux de réussite, etc.).
     * 
     * @param mode Le mode de jeu sélectionné dans le filtre
     * @param genre Le genre musical sélectionné dans le filtre
     */
    private void updateStatistics(String mode, String genre) {
        ScoreService.ScoreStatistics stats;
        
        String filterDesc;
        if (mode.equals("Tous") && genre.equals("Tous")) {
            stats = ScoreService.getGlobalStatistics();
            filterDesc = "Tous";
        } else if (!mode.equals("Tous") && genre.equals("Tous")) {
            stats = ScoreService.getStatisticsByMode(mode);
            filterDesc = mode;
        } else if (mode.equals("Tous") && !genre.equals("Tous")) {
            // Stats par genre seul (on filtre manuellement)
            List<Score> filteredScores = ScoreService.getLeaderboardByGenre(genre, 1000);
            stats = calculateStatsFromScores(filteredScores);
            filterDesc = genre;
        } else {
            // Mode + Genre
            List<Score> filteredScores = ScoreService.getLeaderboardByModeAndGenre(mode, genre, 1000);
            stats = calculateStatsFromScores(filteredScores);
            filterDesc = mode + " - " + genre;
        }
        
        if (stats.getTotalGames() > 0) {
            statsLabel.setText(String.format(
                "📊 Statistiques (%s) : %d parties | Score moyen: %.1f | Meilleur: %d | Taux réussite: %.1f%% | Titres: %.1f%% | Artistes: %.1f%%",
                filterDesc,
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

    /**
     * Calcule manuellement les statistiques à partir d'une liste de scores.
     * Utilisé lorsque les statistiques ne sont pas disponibles directement depuis le service.
     * 
     * @param scores La liste des scores à analyser
     * @return Un objet ScoreStatistics contenant les statistiques calculées
     */
    private ScoreService.ScoreStatistics calculateStatsFromScores(List<Score> scores) {
        if (scores.isEmpty()) {
            return new ScoreService.ScoreStatistics(0, 0, 0, 0.0, 0.0, 0.0, 0.0);
        }

        int totalGames = scores.size();
        int totalScore = scores.stream().mapToInt(Score::getScore).sum();
        int maxScore = scores.stream().mapToInt(Score::getScore).max().orElse(0);
        int minScore = scores.stream().mapToInt(Score::getScore).min().orElse(0);
        double averageScore = (double) totalScore / totalGames;

        double avgSuccessRate = scores.stream().mapToDouble(Score::getSuccessRate).average().orElse(0.0);
        double avgTitleSuccessRate = scores.stream().mapToDouble(Score::getTitleSuccessRate).average().orElse(0.0);
        double avgArtistSuccessRate = scores.stream().mapToDouble(Score::getArtistSuccessRate).average().orElse(0.0);

        return new ScoreService.ScoreStatistics(
            totalGames, maxScore, minScore, averageScore,
            avgSuccessRate, avgTitleSuccessRate, avgArtistSuccessRate
        );
    }

    /**
     * Exporte les données du classement au format spécifié (CSV ou JSON).
     * Applique les filtres actifs avant l'export et génère un nom de fichier approprié.
     * Affiche une alerte de confirmation ou d'erreur selon le résultat.
     * 
     * @param format Le format d'export souhaité ("csv" ou "json")
     */
    private void exportData(String format) {
        try {
            String mode = modeFilter.getValue();
            String genre = genreFilter.getValue();
            
            String filename;
            if (!mode.equals("Tous") && !genre.equals("Tous")) {
                filename = ExportService.generateExportFilename("leaderboard_" + mode + "_" + genre, format);
            } else if (!mode.equals("Tous")) {
                filename = ExportService.generateExportFilename("leaderboard_" + mode, format);
            } else if (!genre.equals("Tous")) {
                filename = ExportService.generateExportFilename("leaderboard_" + genre, format);
            } else {
                filename = ExportService.generateExportFilename("leaderboard", format);
            }
            
            // Export selon les filtres actifs
            List<Score> scoresToExport;
            if (mode.equals("Tous") && genre.equals("Tous")) {
                scoresToExport = ScoreService.getLeaderboard(100);
            } else if (!mode.equals("Tous") && genre.equals("Tous")) {
                scoresToExport = ScoreService.getLeaderboardByMode(mode, 100);
            } else if (mode.equals("Tous") && !genre.equals("Tous")) {
                scoresToExport = ScoreService.getLeaderboardByGenre(genre, 100);
            } else {
                scoresToExport = ScoreService.getLeaderboardByModeAndGenre(mode, genre, 100);
            }
            
            if (format.equals("csv")) {
                ExportService.exportToCSV(scoresToExport, filename);
            } else {
                ExportService.exportToJSON(scoresToExport, filename);
            }
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION, 
                "Export réussi !\nFichier : " + filename);
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