package com.blindtest.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.blindtest.App;
import com.blindtest.controller.GameController;
import com.blindtest.model.Player;
import com.blindtest.service.AudioService;
import com.blindtest.util.InputValidator;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainMenu {

    private Stage primaryStage;
    private StackPane root;
    private VBox contentBox;
    private AudioService audioService;
    
    public static final String BG_GRADIENT = "-fx-background-color: linear-gradient(to bottom right, #a18cd1, #fbc2eb);";
    public static final String CARD_STYLE = "-fx-background-color: rgba(255, 255, 255, 0.95); -fx-background-radius: 25; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 15, 0, 0, 8);";
    public static final String TITLE_FONT = "Verdana";
    public static final String TEXT_FONT = "Segoe UI";

    public MainMenu(AudioService audioService) {
        this.audioService = audioService;
    }

    public StackPane getView() {
    if (root == null) {
        createMenuScene(true); // Crée le menu si pas encore fait
    }
    return root;
    }


    public void start(Stage stage) {
        this.primaryStage = stage;
        // On utilise le système de vue pour ne pas écraser la barre de volume
        App.setView(this.getView()); 
    }
    
    public void startWithoutIntro(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("BlindTest SDN");
        primaryStage.setScene(createMenuScene(false));
        primaryStage.show();
    }

    private Scene createMenuScene(boolean showIntro) {
        root = new StackPane();
        root.setStyle(BG_GRADIENT);

        Pane backgroundAnimation = createBackgroundAnimation();
        
        contentBox = new VBox(25);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setMaxWidth(600);
        contentBox.setPadding(new Insets(40));
        
        if (showIntro) {
            showSplashScreen();
        } else {
            showMainMenu();
        }

        root.getChildren().addAll(backgroundAnimation, contentBox);
        return new Scene(root, 1000, 750);
    }


    private void showSplashScreen() {
        contentBox.getChildren().clear();

        TextFlow titleFlow = createRainbowTitle("BLINDTEST", 64);
        TextFlow subTitleFlow = createRainbowTitle("SDN", 64);
        VBox titleBox = new VBox(5, titleFlow, subTitleFlow);
        titleBox.setAlignment(Pos.CENTER);
        
        TranslateTransition floatTitle = new TranslateTransition(Duration.seconds(2), titleBox);
        floatTitle.setByY(-15);
        floatTitle.setAutoReverse(true);
        floatTitle.setCycleCount(Animation.INDEFINITE);
        floatTitle.play();

        Button enterBtn = createStyledButton("ENTRER DANS LE JEU", "#6C5CE7", 280);
        enterBtn.setOnAction(e -> showMainMenu());

        Button quitBtn = createStyledButton("QUITTER", "#ff7675", 280);
        quitBtn.setOnAction(e -> System.exit(0));

        VBox btnBox = new VBox(20, enterBtn, quitBtn);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPadding(new Insets(50, 0, 0, 0));

        animateEntrance(titleBox, btnBox);
        contentBox.getChildren().addAll(titleBox, btnBox);
    }

    private void showMainMenu() {
        contentBox.getChildren().clear();
        
        TextFlow miniTitle = createRainbowTitle("BLINDTEST SDN", 32);

        Label subTitle = new Label("Menu Principal");
        subTitle.setFont(Font.font(TEXT_FONT, FontWeight.BOLD, 20));
        subTitle.setTextFill(Color.WHITE);

        VBox card = new VBox(20);
        card.setStyle(CARD_STYLE);
        card.setPadding(new Insets(40));
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(400);

        Button playBtn = createStyledButton("🎮 JOUER", "#0984e3", 250);
        playBtn.setOnAction(e -> showModeSelection());

        Button leaderBtn = createStyledButton("🏆 CLASSEMENT", "#00cec9", 250);
        leaderBtn.setOnAction(e -> {
        App.getAudioService().playClick(); // Joue le son
        LeaderboardView lb = new LeaderboardView(primaryStage);
        App.setView(lb.getScene().getRoot()); // Utilise App.setView
        });

        Button settingsBtn = createStyledButton("⚙️ PARAMÈTRES", "#fdcb6e", 250);
        settingsBtn.setOnAction(e -> {
        App.getAudioService().playClick(); // Joue le son
        SettingsView sv = new SettingsView(primaryStage);
        App.setView(sv.getScene().getRoot()); // Utilise App.setView
        });
        
        Button backBtn = createStyledButton("⬅ ÉCRAN TITRE", "#b2bec3", 250);
        backBtn.setOnAction(e -> {
        App.getAudioService().playClick(); // Joue le son
        showSplashScreen();
        });


        card.getChildren().addAll(playBtn, leaderBtn, settingsBtn, backBtn);

        animateEntrance(miniTitle, subTitle, card);
        contentBox.getChildren().addAll(miniTitle, subTitle, card);
    }

    private void showModeSelection() {
        contentBox.getChildren().clear();

        Label title = new Label("CHOISIS TON MODE");
        title.setFont(Font.font(TITLE_FONT, FontWeight.BOLD, 32));
        title.setTextFill(Color.WHITE);
        title.setEffect(new DropShadow(2, Color.BLACK));

        VBox card = new VBox(20);
        card.setStyle(CARD_STYLE);
        card.setPadding(new Insets(40));
        card.setAlignment(Pos.CENTER);

        Button soloBtn = createStyledButton("👤 SOLO", "#6C5CE7", 250);
        soloBtn.setOnAction(e -> showNameInput(1));

        Button duoBtn = createStyledButton("👥 DUO (1 vs 1)", "#e17055", 250);
        duoBtn.setOnAction(e -> showNameInput(2));

        Button backBtn = createStyledButton("RETOUR", "#b2bec3", 250);
        backBtn.setOnAction(e -> showMainMenu());

        card.getChildren().addAll(soloBtn, duoBtn, backBtn);
        
        animateEntrance(title, card);
        contentBox.getChildren().addAll(title, card);
    }

    private void showNameInput(int playerCount) {
        contentBox.getChildren().clear();

        Label title = new Label(playerCount == 1 ? "QUI ES-TU ?" : "QUI SONT LES JOUEURS ?");
        title.setFont(Font.font(TITLE_FONT, FontWeight.BOLD, 28));
        title.setTextFill(Color.WHITE);

        VBox inputsBox = new VBox(20);
        inputsBox.setAlignment(Pos.CENTER);
        inputsBox.setStyle(CARD_STYLE);
        inputsBox.setPadding(new Insets(40));

        TextField p1Input = createStyledTextField("Pseudo Joueur 1");
        TextField p2Input = createStyledTextField("Pseudo Joueur 2");

        // 🆕 Label d'aide
        Label helpLabel = new Label("2-20 caractères, lettres/chiffres uniquement");
        helpLabel.setFont(Font.font(TEXT_FONT, 12));
        helpLabel.setTextFill(Color.GRAY);

        inputsBox.getChildren().addAll(helpLabel, p1Input);
        if (playerCount == 2) {
            inputsBox.getChildren().add(p2Input);
        }

        Button startBtn = createStyledButton("🚀 C'EST PARTI !", "#2ed573", 250);
        startBtn.setOnAction(e -> {
            // 🆕 VALIDATION DES PSEUDOS
            String name1 = InputValidator.sanitize(p1Input.getText());
            String name2 = playerCount == 2 ? InputValidator.sanitize(p2Input.getText()) : "";

            // Vérification Joueur 1
            if (!InputValidator.isValidPseudo(name1)) {
                showError("Pseudo invalide", 
                    "Le pseudo du Joueur 1 doit contenir entre 2 et 20 caractères (lettres/chiffres uniquement).");
                return;
            }

            // Vérification Joueur 2 (si mode Duel)
            if (playerCount == 2) {
                if (!InputValidator.isValidPseudo(name2)) {
                    showError("Pseudo invalide", 
                        "Le pseudo du Joueur 2 doit contenir entre 2 et 20 caractères (lettres/chiffres uniquement).");
                    return;
                }
                
                // Vérifier que les pseudos sont différents
                if (name1.equalsIgnoreCase(name2)) {
                    showError("Pseudos identiques", 
                        "Les deux joueurs doivent avoir des pseudos différents !");
                    return;
                }
            }

            // Tout est OK, lancer le jeu
            List<Player> players = new ArrayList<>();
            players.add(new Player(name1));
            if (playerCount == 2) {
                players.add(new Player(name2));
            }
            launchGame(players);
        });

        Button backBtn = createStyledButton("Retour", "#b2bec3", 250);
        backBtn.setOnAction(e -> showModeSelection());

        inputsBox.getChildren().addAll(startBtn, backBtn);

        animateEntrance(title, inputsBox);
        contentBox.getChildren().addAll(title, inputsBox);
    }

    // 🆕 Méthode pour afficher les erreurs
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    private void launchGame(List<Player> players) {
        try {
            App.getAudioService().stopMenuMusic(); // Arrête l'ambiance pour le jeu
            GameController controller = new GameController(players);
            controller.startGame();
            GameView gameView = new GameView(controller);
            App.setView(gameView.getRootNode()); // Utilise le nouveau système de vue
        } catch (Exception e) {
            e.printStackTrace();
            }
        }

    private void showLeaderboard() {
        LeaderboardView lb = new LeaderboardView(primaryStage);
        primaryStage.setScene(lb.getScene());
    }

    private void showSettings() {
        SettingsView sv = new SettingsView(primaryStage);
        primaryStage.setScene(sv.getScene());
    }

    private TextFlow createRainbowTitle(String text, double fontSize) {
        TextFlow flow = new TextFlow();
        Color[] colors = {
            Color.web("#ff7675"), Color.web("#fdcb6e"), Color.web("#55efc4"), 
            Color.web("#74b9ff"), Color.web("#a29bfe"), Color.web("#fd79a8")
        };
        int colorIndex = 0;
        for (char c : text.toCharArray()) {
            Text t = new Text(String.valueOf(c));
            t.setFont(Font.font("Verdana", FontWeight.EXTRA_BOLD, fontSize));
            t.setFill(colors[colorIndex % colors.length]);
            t.setEffect(new DropShadow(3, Color.BLACK));
            flow.getChildren().add(t);
            colorIndex++;
        }
        flow.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        return flow;
    }

    private Button createStyledButton(String text, String colorHex, double width) {
        Button btn = new Button(text);
        App.getAudioService().playClick();
        btn.setFont(Font.font(TEXT_FONT, FontWeight.BOLD, 16));
        btn.setPrefWidth(width);
        btn.setPrefHeight(50);
        
        String baseStyle = "-fx-background-color: white; -fx-text-fill: " + colorHex + "; " +
                           "-fx-background-radius: 30; -fx-cursor: hand; -fx-border-color: " + colorHex + "; -fx-border-width: 2; -fx-border-radius: 30;";
        String hoverStyle = "-fx-background-color: " + colorHex + "; -fx-text-fill: white; " +
                            "-fx-background-radius: 30; -fx-cursor: hand; -fx-border-color: white; -fx-border-width: 2; -fx-border-radius: 30; " +
                            "-fx-effect: dropshadow(three-pass-box, " + colorHex + ", 10, 0, 0, 0);";
        btn.setStyle(baseStyle);
        btn.setOnMouseEntered(e -> { btn.setStyle(hoverStyle); btn.setScaleX(1.05); btn.setScaleY(1.05); });
        btn.setOnMouseExited(e -> { btn.setStyle(baseStyle); btn.setScaleX(1.0); btn.setScaleY(1.0); });
        btn.setOnMouseClicked(e -> App.getAudioService().playClick());
        return btn;
    }

    private TextField createStyledTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setFont(Font.font(TEXT_FONT, 16));
        tf.setPrefWidth(280);
        tf.setStyle("-fx-background-color: #dfe6e9; -fx-background-radius: 25; -fx-padding: 12 20; -fx-text-fill: #2d3436;");
        return tf;
    }

    private void animateEntrance(javafx.scene.Node... nodes) {
        int delay = 0;
        for (javafx.scene.Node node : nodes) {
            TranslateTransition tt = new TranslateTransition(Duration.millis(800), node);
            tt.setFromY(100); tt.setToY(0);
            FadeTransition ft = new FadeTransition(Duration.millis(800), node);
            ft.setFromValue(0); ft.setToValue(1);
            ParallelTransition pt = new ParallelTransition(tt, ft);
            pt.setDelay(Duration.millis(delay));
            pt.play();
            delay += 100;
        }
    }

    private Pane createBackgroundAnimation() {
        Pane pane = new Pane();
        Random rand = new Random();
        for (int i = 0; i < 20; i++) {
            Circle c = new Circle(rand.nextInt(60) + 20);
            c.setFill(Color.WHITE);
            c.setOpacity(0.05 + rand.nextDouble() * 0.15);
            c.setTranslateX(rand.nextInt(1000));
            c.setTranslateY(rand.nextInt(750));
            c.setEffect(new javafx.scene.effect.GaussianBlur(20));
            TranslateTransition tt = new TranslateTransition(Duration.seconds(15 + rand.nextInt(25)), c);
            tt.setByX(rand.nextInt(300) - 150); tt.setByY(rand.nextInt(300) - 150);
            tt.setAutoReverse(true); tt.setCycleCount(Animation.INDEFINITE); tt.play();
            pane.getChildren().add(c);
        }
        return pane;
    }
}