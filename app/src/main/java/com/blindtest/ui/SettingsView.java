package com.blindtest.ui;

import com.blindtest.App;
import com.blindtest.model.Settings;
import com.blindtest.service.SettingsService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class SettingsView {
    private Stage stage;
    private Settings settings;

    private static final String[] AVAILABLE_GENRES = {
        "Tout Genre", "Pop", "Rock", "Hip-Hop/Rap", "R&B"
    };

    public SettingsView(Stage stage) {
        this.stage = stage;
        this.settings = SettingsService.loadSettings();
    }

    public Scene getScene() {
        VBox root = new VBox(25);
        root.setStyle(MainMenu.BG_GRADIENT);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));

        Label title = new Label("PARAMÈTRES");
        title.setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, 36));
        title.setTextFill(Color.WHITE);

        VBox card = new VBox(20);
        card.setStyle(MainMenu.CARD_STYLE);
        card.setPadding(new Insets(30));
        card.setMaxWidth(550);
        card.setAlignment(Pos.CENTER_LEFT);

        // --- Volume ---
        Label volLabel = styleLabel("Volume Général :");
        Slider volSlider = new Slider(0, 1, settings.getDefaultVolume());
        
        // --- Nouveaux Sliders (Restaurés) ---
        
        // Nombre de manches (5 à 20)
        Label roundsLabel = styleLabel("Nombre de manches : " + settings.getNumberOfRounds());
        Slider roundsSlider = new Slider(5, 20, settings.getNumberOfRounds());
        roundsSlider.setMajorTickUnit(5);
        roundsSlider.setSnapToTicks(true);
        roundsSlider.setShowTickMarks(true);
        roundsSlider.setShowTickLabels(true);
        roundsSlider.valueProperty().addListener((obs, oldVal, newVal) -> 
            roundsLabel.setText("Nombre de manches : " + newVal.intValue()));

        // Durée (10s à 60s)
        Label durationLabel = styleLabel("Durée extrait (sec) : " + settings.getExtractDuration());
        Slider durationSlider = new Slider(10, 60, settings.getExtractDuration());
        durationSlider.setMajorTickUnit(10);
        durationSlider.setSnapToTicks(true);
        durationSlider.setShowTickMarks(true);
        durationSlider.setShowTickLabels(true);
        durationSlider.valueProperty().addListener((obs, oldVal, newVal) -> 
            durationLabel.setText("Durée extrait (sec) : " + newVal.intValue()));

        // --- Options ---
        CheckBox hintsBox = styleCheckBox("Autoriser les indices");
        hintsBox.setSelected(settings.isHintsEnabled());

        CheckBox speedBox = styleCheckBox("Bonus de vitesse");
        speedBox.setSelected(settings.isSpeedBonusEnabled());

        // --- Genre ---
        Label genreLabel = styleLabel("Genre par défaut :");
        ComboBox<String> genreCombo = new ComboBox<>();
        genreCombo.getItems().addAll(AVAILABLE_GENRES);
        genreCombo.setValue(settings.getDefaultGenre() != null ? settings.getDefaultGenre() : "All");
        genreCombo.setMaxWidth(Double.MAX_VALUE);

        card.getChildren().addAll(
            volLabel, volSlider, 
            new Separator(),
            roundsLabel, roundsSlider,
            durationLabel, durationSlider,
            new Separator(),
            hintsBox, speedBox, 
            genreLabel, genreCombo
        );

       // --- Boutons ---
        Button saveBtn = new Button("💾 SAUVEGARDER");
        saveBtn.setStyle("-fx-background-color: #2ed573; -fx-text-fill: white; -fx-background-radius: 30; -fx-padding: 10 30; -fx-font-weight: bold;");
        saveBtn.setOnAction(e -> {
            // Sauvegarde des paramètres
            settings.setDefaultVolume((float) volSlider.getValue());
            settings.setNumberOfRounds((int) roundsSlider.getValue());
            settings.setExtractDuration((int) durationSlider.getValue());
            settings.setHintsEnabled(hintsBox.isSelected());
            settings.setSpeedBonusEnabled(speedBox.isSelected());
            settings.setDefaultGenre(genreCombo.getValue());
            SettingsService.saveSettings(settings);
            
            // Retour au menu principal
            App.getAudioService().playClick();
            new MainMenu(App.getAudioService()).start(stage);
        });

        Button backBtn = new Button("ANNULER");
        backBtn.setStyle("-fx-background-color: #ff7675; -fx-text-fill: white; -fx-background-radius: 30; -fx-padding: 10 30; -fx-font-weight: bold;");
        backBtn.setOnAction(e -> {
            App.getAudioService().playClick();
            new MainMenu(App.getAudioService()).start(stage);
        });

        HBox btnBox = new HBox(20, saveBtn, backBtn);
        btnBox.setAlignment(Pos.CENTER);

        root.getChildren().addAll(title, card, btnBox);
        return new Scene(root, 1000, 750);
    }

    private Label styleLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        l.setTextFill(Color.web("#2d3436"));
        return l;
    }

    private CheckBox styleCheckBox(String text) {
        CheckBox cb = new CheckBox(text);
        cb.setFont(Font.font("Segoe UI", 14));
        cb.setTextFill(Color.web("#2d3436"));
        return cb;
    }
}