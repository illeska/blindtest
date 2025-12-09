package blindtest; // ⚠️ Vérifie que c'est bien le nom de ton package !

import javafx.animation.FadeTransition;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class SceneManager {
    // Ce panneau gardera toutes tes vues empilées
    private static StackPane rootPane;

    // On appelle ça une seule fois au début du programme
    public static void setRootPane(StackPane pane) {
        rootPane = pane;
    }

    // Méthode magique pour changer de vue avec un fondu
    public static void switchView(Parent nouvelleVue) {
        if (rootPane == null) {
            System.err.println("Erreur : SceneManager n'est pas initialisé dans App.java !");
            return;
        }

        // Si c'est la toute première vue, on l'affiche direct sans animation
        if (rootPane.getChildren().isEmpty()) {
            rootPane.getChildren().add(nouvelleVue);
            return;
        }

        // Sinon, on prépare la transition
        Parent vueActuelle = rootPane.getChildren().get(0);

        // 1. La nouvelle vue commence invisible (transparente)
        nouvelleVue.setOpacity(0);
        rootPane.getChildren().add(nouvelleVue);

        // 2. On fait apparaître la nouvelle (Fade In)
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), nouvelleVue);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        // 3. On fait disparaître l'ancienne (Fade Out)
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), vueActuelle);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        // 4. Quand l'ancienne est invisible, on la supprime de la mémoire
        fadeOut.setOnFinished(e -> rootPane.getChildren().remove(vueActuelle));

        // Action !
        fadeIn.play();
        fadeOut.play();
    }
}