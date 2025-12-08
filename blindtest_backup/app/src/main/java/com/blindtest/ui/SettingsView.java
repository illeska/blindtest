package com.blindtest.ui;

import com.blindtest.model.Settings;
import com.blindtest.service.SettingsService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class SettingsView {
    private Scene scene;
    private Stage stage;
    private Settings settings;

    // Liste des genres musicaux disponibles
    private static final String[] AVAILABLE_GENRES = {
        "Pop",
        "Rock",
        "Hip-Hop/Rap",
        "R&B",
        "Électro/EDM",
        "Jazz",
        "Blues",
        "Country",
        "Reggae",
        "Classique",
        "Metal",
        "Folk",
        "Soul",
        "Funk",
        "Disco",
        "House",
        "Techno",
        "Indie",
        "Punk",
        "Alternative"
    };

    public SettingsView(Stage stage) {
        this.stage = stage;
        this.settings = SettingsService.loadSettings();
        initializeScene();
    }

    private void initializeScene() {
        Label titleLabel = new Label("⚙️ Paramètres");
        titleLabel.setFont(new Font("Arial", 24));

        VBox settingsBox = new VBox(15);
        settingsBox.setAlignment(Pos.CENTER_LEFT);
        settingsBox.setPadding(new Insets(20));
        settingsBox.setMaxWidth(400);

        // Nombre de manches
        HBox roundsBox = new HBox(10);
        roundsBox.setAlignment(Pos.CENTER_LEFT);
        Label roundsLabel = new Label("Nombre de manches:");
        roundsLabel.setMinWidth(200);
        Spinner<Integer> roundsSpinner = new Spinner<>(1, 20, settings.getNumberOfRounds());
        roundsSpinner.setEditable(true);
        roundsBox.getChildren().addAll(roundsLabel, roundsSpinner);

        // Durée extrait
        HBox durationBox = new HBox(10);
        durationBox.setAlignment(Pos.CENTER_LEFT);
        Label durationLabel = new Label("Durée extrait (secondes):");
        durationLabel.setMinWidth(200);
        Spinner<Integer> durationSpinner = new Spinner<>(5, 60, settings.getExtractDuration());
        durationSpinner.setEditable(true);
        durationBox.getChildren().addAll(durationLabel, durationSpinner);

        // Volume
        HBox volumeBox = new HBox(10);
        volumeBox.setAlignment(Pos.CENTER_LEFT);
        Label volumeLabel = new Label("Volume par défaut:");
        volumeLabel.setMinWidth(200);
        Slider volumeSlider = new Slider(0, 1, settings.getDefaultVolume());
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setMajorTickUnit(0.25);
        volumeSlider.setMinorTickCount(0);
        volumeSlider.setBlockIncrement(0.1);
        volumeSlider.setPrefWidth(150);
        Label volumeValueLabel = new Label(String.format("%.0f%%", settings.getDefaultVolume() * 100));
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            volumeValueLabel.setText(String.format("%.0f%%", newVal.doubleValue() * 100));
        });
        volumeBox.getChildren().addAll(volumeLabel, volumeSlider, volumeValueLabel);

        // Checkboxes
        CheckBox hintsCheckBox = new CheckBox("Activer les indices");
        hintsCheckBox.setSelected(settings.isHintsEnabled());

        CheckBox speedBonusCheckBox = new CheckBox("Bonus de rapidité");
        speedBonusCheckBox.setSelected(settings.isSpeedBonusEnabled());

        // Genre par défaut avec ComboBox (menu déroulant)
        HBox genreBox = new HBox(10);
        genreBox.setAlignment(Pos.CENTER_LEFT);
        Label genreLabel = new Label("Genre par défaut:");
        genreLabel.setMinWidth(200);
        
        ComboBox<String> genreComboBox = new ComboBox<>();
        genreComboBox.getItems().addAll(AVAILABLE_GENRES);
        genreComboBox.setPrefWidth(150);
        
        // Sélectionner le genre actuel s'il existe dans la liste
        String currentGenre = settings.getDefaultGenre();
        boolean found = false;
        for (String genre : AVAILABLE_GENRES) {
            if (genre.equalsIgnoreCase(currentGenre)) {
                genreComboBox.setValue(genre);
                found = true;
                break;
            }
        }
        // Si le genre actuel n'est pas dans la liste, sélectionner "Pop" par défaut
        if (!found) {
            genreComboBox.setValue("Pop");
        }
        
        genreBox.getChildren().addAll(genreLabel, genreComboBox);

        settingsBox.getChildren().addAll(
            roundsBox, 
            durationBox, 
            volumeBox, 
            hintsCheckBox, 
            speedBonusCheckBox,
            genreBox
        );

        // Boutons
        HBox buttonsBox = new HBox(15);
        buttonsBox.setAlignment(Pos.CENTER);

        Button saveButton = new Button("Enregistrer");
        saveButton.setStyle("-fx-min-width: 150px; -fx-padding: 10px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        saveButton.setOnAction(e -> {
            settings.setNumberOfRounds(roundsSpinner.getValue());
            settings.setExtractDuration(durationSpinner.getValue());
            settings.setDefaultVolume(volumeSlider.getValue());
            settings.setHintsEnabled(hintsCheckBox.isSelected());
            settings.setSpeedBonusEnabled(speedBonusCheckBox.isSelected());
            
            // Récupérer le genre sélectionné dans le ComboBox
            String selectedGenre = genreComboBox.getValue();
            if (selectedGenre != null && !selectedGenre.isEmpty()) {
                settings.setDefaultGenre(selectedGenre.toLowerCase());
            }

            SettingsService.saveSettings(settings);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Paramètres sauvegardés");
            alert.setHeaderText(null);
            alert.setContentText("Les paramètres ont été enregistrés avec succès.");
            alert.showAndWait();
        });

        Button cancelButton = new Button("Retour");
        cancelButton.setStyle("-fx-min-width: 150px; -fx-padding: 10px;");
        cancelButton.setOnAction(e -> {
            MainMenu mainMenu = new MainMenu();
            stage.setScene(mainMenu.getScene(stage));
        });

        buttonsBox.getChildren().addAll(saveButton, cancelButton);

        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.setPadding(new Insets(20));
        mainLayout.getChildren().addAll(titleLabel, settingsBox, buttonsBox);

        scene = new Scene(mainLayout, 500, 500);
    }

    public Scene getScene() {
        return scene;
    }
}