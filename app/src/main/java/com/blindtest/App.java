package com.blindtest;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import com.blindtest.ui.MainMenu;
import com.blindtest.service.AudioService;

/**
 * Classe principale de l'application JavaFX BlindTest.
 * Gère l'interface principale, le contrôle du volume et la navigation entre les vues.
 */
public class App extends Application {

    /**
     * Service audio global pour toute l'application.
     */
    private static AudioService audioService = new AudioService();

    /**
     * Conteneur racine empilant le contenu du jeu et les contrôles UI.
     */
    private static StackPane rootContainer = new StackPane(); 


    /**
     * Conteneur principal pour le contenu du jeu (menu, parties, etc.).
     */
    private static BorderPane gameContent = new BorderPane(); 

    /**
     * Point d'entrée de l'application JavaFX.
     * Configure l'interface, le contrôle du volume et lance le menu principal.
     * @param stage La fenêtre principale de l'application
     */
    @Override
    public void start(Stage stage) {
        // Barre de volume incrustée (discrète en haut à gauche)
        Slider volumeSlider = new Slider(0, 1, 0.5);
        volumeSlider.setPrefWidth(100);
        volumeSlider.setStyle("-fx-control-inner-background: #6C5CE7;");
        volumeSlider.valueProperty().addListener((obs, old, newVal) -> 
            audioService.setGlobalVolume(newVal.doubleValue()));

        Label volIcon = new Label("🔈");
        volIcon.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

        HBox volumeBox = new HBox(10, volIcon, volumeSlider);
        volumeBox.setAlignment(Pos.TOP_LEFT);
        volumeBox.setPadding(new Insets(20));
        volumeBox.setPickOnBounds(false); // Permet de cliquer sur les boutons du menu dessous
        
        StackPane.setAlignment(volumeBox, Pos.TOP_LEFT);

        // On empile le contenu du jeu et la barre de volume par-dessus
        rootContainer.getChildren().addAll(gameContent, volumeBox);

        // Lancement immédiat du menu et de la musique
        MainMenu menu = new MainMenu(audioService);
        setView(menu.getView());

        Scene scene = new Scene(rootContainer, 1000, 750);
        stage.setScene(scene);
        stage.setTitle("BlindTest SDN");
        stage.show();

        audioService.startMenuMusic(); // La musique démarre dès l'ouverture
    }

    /**
     * Change la vue affichée dans le conteneur principal.
     * @param node Le nœud JavaFX à afficher
     */
    public static void setView(javafx.scene.Node node) {
        gameContent.setCenter(node);
    }

    /**
     * Retourne le service audio global.
     * @return L'instance AudioService
     */
    public static AudioService getAudioService() { return audioService; }

    /**
     * Point d'entrée du programme.
     * @param args Les arguments de ligne de commande
     */
    public static void main(String[] args) { launch(); }
}