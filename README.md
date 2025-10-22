# INSTALLATION DU PROJET BLINDTEST

# 1. Prérequis :
#    - Java JDK 21 ou plus (OpenJDK ou Temurin)
#    - Gradle 9.1 ou utiliser le wrapper inclus
#    - OpenJFX (JavaFX) 22
#    - JUnit 5.10.0 (inclus dans le projet via Gradle)

# 2. Cloner le dépôt
git clone https://github.com/illeska/blindtest.git
cd blindtest

# 3. Nettoyer et compiler le projet
gradlew.bat clean build   # Windows
./gradlew clean build     # Linux / macOS

# 4. Lancer l'application JavaFX
gradlew.bat run           # Windows
./gradlew run             # Linux / macOS

# 5. Exécuter les tests unitaires (JUnit 5)
gradlew.bat test          # Windows
./gradlew test            # Linux / macOS
