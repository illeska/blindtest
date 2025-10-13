public class Player {

    // Attributs
    private String name;
    private int score;
    private boolean hasAnswered;

    // Constructeur
    public Player(String name) {
        this.name = name;
        this.score = 0;
        this.hasAnswered = false;
    }

    // Getter pour le nom
    public String getName() {
        return name;
    }

    // Setter pour le nom (optionnel si le nom ne change jamais)
    public void setName(String name) {
        this.name = name;
    }

    // Getter pour le score
    public int getScore() {
        return score;
    }

    // Ajouter des points
    public void addPoints(int points) {
        this.score += points;
    }

    // Réinitialiser le score (ex: nouvelle partie)
    public void resetScore() {
        this.score = 0;
    }

    // Indique si le joueur a déjà répondu
    public boolean hasAnswered() {
        return hasAnswered;
    }

    // Marquer comme ayant répondu
    public void setHasAnswered(boolean hasAnswered) {
        this.hasAnswered = hasAnswered;
    }

    // Affichage des infos du joueur
    @Override
    public String toString() {
        return "Player{name='" + name + "', score=" + score + ", hasAnswered=" + hasAnswered + '}';
    }
}
