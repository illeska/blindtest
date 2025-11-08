# BlindTest - Jeu Musical Multi-Modes

## Description du Projet
BlindTest est une application Java/JavaFX qui permet de jouer à un jeu de blindtest musical en mode solo ou duel. Le jeu récupère des extraits musicaux via l'API iTunes Search, gère le scoring, les paramètres et la persistance des données.

## Membres de l'Équipe
- Iskander : Intégration API iTunes, AudioService, MediaPlayer
- Achraf : Développement UI (GameView, LeaderboardView, SettingsView), logique saisie réponses, timer
- Léo : Persistance JSON (scores, settings, playlists), Leaderboard, historique joueurs, tests persistance, documentation Javadoc

## Fonctionnalités Principales
- Modes de jeu : Solo et Duel
- Lecture d'extraits audio via API iTunes
- Système de scoring avec bonus rapidité
- Persistance des scores et paramètres en JSON
- Leaderboard et historique des joueurs
- Paramètres configurables (nombre manches, durée extraits, volume, genre)
- Tests unitaires JUnit 5

## Technologies Utilisées
- Java 21
- JavaFX 22
- Gradle 9.1
- Gson 2.10.1 pour JSON
- JUnit 5.10.0 pour tests
- API iTunes Search

## Installation et Exécution

### Prérequis
- Java JDK 21 ou plus (OpenJDK ou Temurin)
- Gradle 9.1 ou utiliser le wrapper inclus
- OpenJFX (JavaFX) 22
- JUnit 5.10.0 (inclus dans le projet via Gradle)

### Étapes
1. Cloner le dépôt
   ```
   git clone https://github.com/illeska/blindtest.git
   cd blindtest
   ```

2. Nettoyer et compiler le projet
   ```
   gradlew.bat clean build   # Windows
   ./gradlew clean build     # Linux / macOS
   ```

3. Lancer l'application JavaFX
   ```
   gradlew.bat run           # Windows
   ./gradlew run             # Linux / macOS
   ```

4. Exécuter les tests unitaires (JUnit 5)
   ```
   gradlew.bat test          # Windows
   ./gradlew test            # Linux / macOS
   ```

## Structure du Projet
- `app/` : Point d'entrée et UI JavaFX
- `library/` : Logique métier, modèles, services
- `doc/` : Documentation (CDC, diagrammes UML, Gantt)
- `data/` : Fichiers de persistance JSON

## Documentation
- [Cahier des Charges](doc/Cahier%20des%20charges%20BlindTest.pdf)
- [Diagrammes UML](doc/Classes.png)
- [Diagramme de Gantt](doc/Gantt.png)
- [Javadoc](build/docs/javadoc/index.html) - Générer avec `./gradlew javadoc`

## Tests
Les tests unitaires couvrent la logique de persistance, scoring et contrôleur de jeu. Exécuter avec `./gradlew test`.

## Licence
Voir le fichier LICENSE pour les détails.
