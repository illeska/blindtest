package com.blindtest.ui;

import com.blindtest.model.Score;
import com.blindtest.service.ExportService;
import com.blindtest.service.ScoreService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * Vue enrichie du leaderboard avec filtres, statistiques et export.
 * 🆕 Sprint 4 : Ajout de filtres par mode/genre, statistiques et boutons d'export.
 */
public class LeaderboardView {
    private Scene scene;
    private Stage stage;
    
    // Composants UI
    private VBox leaderboardBox;
    private ComboBox<String> modeFilter;
    private ComboBox<String> genreFilter;
    private Label statsLabel;

    public LeaderboardView(Stage stage) {
        this.stage = stage;
        initializeScene();
    }

    private void initializeScene() {
        // Titre
        Label titleLabel = new Label("🏆 Classement");
        titleLabel.setFont(new Font("Arial", 24));

        // === SECTION FILTRES ===
        HBox filterBox = createFilterBox();

        // === SECTION STATISTIQUES ===
        statsLabel = new Label();
        statsLabel.setFont(new Font("Arial", 12));
        statsLabel.setStyle("-fx-text-fill: #555555;");
        updateStatistics(null, null);

        // === SECTION LEADERBOARD ===
        leaderboardBox = new VBox(10);
        leaderboardBox.setAlignment(Pos.CENTER);
        leaderboardBox.setPadding(new Insets(20));
        
        // Chargement initial du leaderboard
        updateLeaderboard(null, null);

        // === SECTION BOUTONS D'ACTION ===
        HBox actionButtonsBox = createActionButtons();

        // Bouton retour
        Button backButton = new Button("Retour");
        backButton.setStyle("-fx-min-width: 150px; -fx-padding: 10px;");
        backButton.setOnAction(e -> {
            MainMenu mainMenu = new MainMenu();
            stage.setScene(mainMenu.getScene(stage));
        });

        // Layout principal
        VBox mainLayout = new VBox(15);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(20));
        mainLayout.getChildren().addAll(
                titleLabel, 
                filterBox, 
                statsLabel, 
                leaderboardBox, 
                actionButtonsBox, 
                backButton
        );

        scene = new Scene(mainLayout, 700, 700);
    }

    /**
     * Crée la section de filtres (mode et genre).
     */
    private HBox createFilterBox() {
        HBox filterBox = new HBox(15);
        filterBox.setAlignment(Pos.CENTER);
        filterBox.setPadding(new Insets(10));
        filterBox.setStyle("-fx-background-color: #e8f4f8; -fx-background-radius: 10;");

        Label filterLabel = new Label("Filtres :");
        filterLabel.setFont(new Font("Arial", 14));
        filterLabel.setStyle("-fx-font-weight: bold;");

        // Filtre par mode
        modeFilter = new ComboBox<>();
        modeFilter.getItems().addAll("Tous", "Solo", "Duel");
        modeFilter.setValue("Tous");
        modeFilter.setPromptText("Mode");
        modeFilter.setOnAction(e -> applyFilters());

        // Filtre par genre
        genreFilter = new ComboBox<>();
        genreFilter.getItems().addAll("Tous", "Pop", "Rock", "Hip-Hop", "Jazz", "Electro", "Mixed");
        genreFilter.setValue("Tous");
        genreFilter.setPromptText("Genre");
        genreFilter.setOnAction(e -> applyFilters());

        Button resetButton = new Button("Réinitialiser");
        resetButton.setStyle("-fx-padding: 5px 15px;");
        resetButton.setOnAction(e -> {
            modeFilter.setValue("Tous");
            genreFilter.setValue("Tous");
            applyFilters();
        });

        filterBox.getChildren().addAll(filterLabel, modeFilter, genreFilter, resetButton);
        return filterBox;
    }

    /**
     * Crée la section des boutons d'action (export).
     */
    private HBox createActionButtons() {
        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER);
        actionBox.setPadding(new Insets(10));

        Button exportCSVButton = new Button("📊 Export CSV");
        exportCSVButton.setStyle("-fx-padding: 8px 15px;");
        exportCSVButton.setOnAction(e -> exportLeaderboard("csv"));

        Button exportJSONButton = new Button("📄 Export JSON");
        exportJSONButton.setStyle("-fx-padding: 8px 15px;");
        exportJSONButton.setOnAction(e -> exportLeaderboard("json"));

        Button statsReportButton = new Button("📈 Rapport Stats");
        statsReportButton.setStyle("-fx-padding: 8px 15px;");
        statsReportButton.setOnAction(e -> exportStatisticsReport());

        actionBox.getChildren().addAll(exportCSVButton, exportJSONButton, statsReportButton);
        return actionBox;
    }

    /**
     * Applique les filtres sélectionnés et met à jour l'affichage.
     */
    private void applyFilters() {
        String selectedMode = modeFilter.getValue();
        String selectedGenre = genreFilter.getValue();

        String mode = selectedMode.equals("Tous") ? null : selectedMode;
        String genre = selectedGenre.equals("Tous") ? null : selectedGenre;

        updateLeaderboard(mode, genre);
        updateStatistics(mode, genre);
    }

    /**
     * Met à jour l'affichage du leaderboard avec les filtres appliqués.
     */
    private void updateLeaderboard(String mode, String genre) {
        leaderboardBox.getChildren().clear();

        // Chargement des scores selon les filtres
        List<Score> leaderboard;
        if (mode != null && genre != null) {
            leaderboard = ScoreService.getLeaderboardByModeAndGenre(mode, genre, 10);
        } else if (mode != null) {
            leaderboard = ScoreService.getLeaderboardByMode(mode, 10);
        } else if (genre != null) {
            leaderboard = ScoreService.getLeaderboardByGenre(genre, 10);
        } else {
            leaderboard = ScoreService.getLeaderboard(10);
        }

        if (leaderboard.isEmpty()) {
            Label emptyLabel = new Label("Aucun score enregistré pour ce filtre.");
            emptyLabel.setFont(new Font("Arial", 14));
            leaderboardBox.getChildren().add(emptyLabel);
            return;
        }

        // En-tête
        HBox headerBox = createHeaderRow();
        leaderboardBox.getChildren().add(headerBox);

        // Lignes de scores
        int rank = 1;
        for (Score entry : leaderboard) {
            HBox entryBox = createScoreRow(rank, entry);
            leaderboardBox.getChildren().add(entryBox);
            rank++;
        }
    }

    /**
     * Crée la ligne d'en-tête du tableau.
     */
    private HBox createHeaderRow() {
        HBox headerBox = new HBox(15);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(10));
        headerBox.setStyle("-fx-background-color: #2196F3; -fx-background-radius: 5;");

        Label rankHeader = createHeaderLabel("Rang", 50);
        Label pseudoHeader = createHeaderLabel("Pseudo", 120);
        Label scoreHeader = createHeaderLabel("Score", 70);
        Label modeHeader = createHeaderLabel("Mode", 70);
        Label genreHeader = createHeaderLabel("Genre", 80);
        Label successHeader = createHeaderLabel("Réussite", 80);

        headerBox.getChildren().addAll(rankHeader, pseudoHeader, scoreHeader, modeHeader, genreHeader, successHeader);
        return headerBox;
    }

    /**
     * Crée un label d'en-tête avec style uniforme.
     */
    private Label createHeaderLabel(String text, int width) {
        Label label = new Label(text);
        label.setFont(new Font("Arial", 13));
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
        label.setMinWidth(width);
        label.setAlignment(Pos.CENTER);
        return label;
    }

    /**
     * Crée une ligne de score pour le tableau.
     */
    private HBox createScoreRow(int rank, Score entry) {
        HBox entryBox = new HBox(15);
        entryBox.setAlignment(Pos.CENTER);
        entryBox.setPadding(new Insets(8));
        
        // Style alterné pour les lignes
        String bgColor = (rank % 2 == 0) ? "#f5f5f5" : "#ffffff";
        entryBox.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 5;");

        Label rankLabel = createCellLabel(String.valueOf(rank), 50);
        Label pseudoLabel = createCellLabel(entry.getPseudo(), 120);
        Label scoreLabel = createCellLabel(String.valueOf(entry.getScore()), 70);
        Label modeLabel = createCellLabel(entry.getMode() != null ? entry.getMode() : "-", 70);
        Label genreLabel = createCellLabel(entry.getGenre() != null ? entry.getGenre() : "-", 80);
        Label successLabel = createCellLabel(String.format("%.1f%%", entry.getSuccessRate()), 80);

        entryBox.getChildren().addAll(rankLabel, pseudoLabel, scoreLabel, modeLabel, genreLabel, successLabel);
        
        // Effet de survol
        entryBox.setOnMouseEntered(e -> entryBox.setStyle("-fx-background-color: #e3f2fd; -fx-background-radius: 5;"));
        entryBox.setOnMouseExited(e -> entryBox.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 5;"));
        
        return entryBox;
    }

    /**
     * Crée un label de cellule avec style uniforme.
     */
    private Label createCellLabel(String text, int width) {
        Label label = new Label(text);
        label.setFont(new Font("Arial", 12));
        label.setMinWidth(width);
        label.setAlignment(Pos.CENTER);
        return label;
    }

    /**
     * Met à jour l'affichage des statistiques.
     */
    private void updateStatistics(String mode, String genre) {
        ScoreService.ScoreStatistics stats;
        
        if (mode != null) {
            stats = ScoreService.getStatisticsByMode(mode);
        } else {
            stats = ScoreService.getGlobalStatistics();
        }

        if (stats.getTotalGames() == 0) {
            statsLabel.setText("📊 Aucune statistique disponible.");
        } else {
            String statsText = String.format(
                    "📊 Statistiques : %d parties | Score moyen : %.1f | Meilleur : %d | Taux réussite : %.1f%%",
                    stats.getTotalGames(),
                    stats.getAverageScore(),
                    stats.getMaxScore(),
                    stats.getAvgSuccessRate()
            );
            statsLabel.setText(statsText);
        }
    }

    /**
     * Exporte le leaderboard actuel dans le format spécifié.
     */
    private void exportLeaderboard(String format) {
        try {
            String mode = modeFilter.getValue();
            String modePrefix = mode.equals("Tous") ? "all" : mode.toLowerCase();
            
            String filename = ExportService.generateExportFilename("leaderboard_" + modePrefix, format);
            
            if (format.equals("csv")) {
                if (mode.equals("Tous")) {
                    ExportService.exportLeaderboardToCSV(filename);
                } else {
                    ExportService.exportLeaderboardByModeToCSV(mode, filename);
                }
            } else if (format.equals("json")) {
                if (mode.equals("Tous")) {
                    ExportService.exportLeaderboardToJSON(filename);
                } else {
                    ExportService.exportLeaderboardByModeToJSON(mode, filename);
                }
            }
            
            showAlert(Alert.AlertType.INFORMATION, "Export réussi", 
                      "Le leaderboard a été exporté vers :\n" + filename);
            
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur d'export", 
                      "Impossible d'exporter le leaderboard :\n" + e.getMessage());
        }
    }

    /**
     * Génère et exporte un rapport de statistiques.
     */
    private void exportStatisticsReport() {
        try {
            String mode = modeFilter.getValue();
            ScoreService.ScoreStatistics stats;
            
            if (mode.equals("Tous")) {
                stats = ScoreService.getGlobalStatistics();
            } else {
                stats = ScoreService.getStatisticsByMode(mode);
            }
            
            String filename = ExportService.generateExportFilename("stats_report", "txt");
            ExportService.exportStatisticsReport(stats, filename);
            
            showAlert(Alert.AlertType.INFORMATION, "Rapport généré", 
                      "Le rapport de statistiques a été généré :\n" + filename);
            
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de génération", 
                      "Impossible de générer le rapport :\n" + e.getMessage());
        }
    }

    /**
     * Affiche une alerte à l'utilisateur.
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Scene getScene() {
        return scene;
    }
}