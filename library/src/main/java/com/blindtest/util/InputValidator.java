package com.blindtest.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Utilitaire pour valider et normaliser les inputs utilisateur.
 * Gère le trim, la normalisation, et les caractères spéciaux.
 */
public class InputValidator {

    // Pattern pour détecter les caractères spéciaux dangereux
    private static final Pattern SPECIAL_CHARS_PATTERN = Pattern.compile("[<>\"'&;{}()]");
    
    // Pattern pour valider un pseudo (lettres, chiffres, espaces, tirets, underscores)
    private static final Pattern PSEUDO_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s_-]+$");

    /**
     * Normalise une chaîne : trim, suppression accents, minuscules.
     * Utilisé pour comparer les réponses de l'utilisateur.
     * 
     * @param input La chaîne à normaliser
     * @return La chaîne normalisée
     */
    public static String normalize(String input) {
        if (input == null) return "";
        
        // Trim et conversion en minuscules
        String normalized = input.trim().toLowerCase();
        
        // Suppression des accents
        normalized = Normalizer.normalize(normalized, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("\\p{M}", "");
        
        // Suppression des caractères spéciaux de ponctuation
        normalized = normalized.replaceAll("[''`']", "'"); // Normaliser les apostrophes
        normalized = normalized.replaceAll("[\\p{Punct}&&[^'-]]", ""); // Garder tirets et apostrophes
        
        // Suppression des espaces multiples
        normalized = normalized.replaceAll("\\s+", " ");
        
        return normalized.trim();
    }

    /**
     * Valide et nettoie un pseudo utilisateur.
     * 
     * @param pseudo Le pseudo à valider
     * @return Le pseudo nettoyé
     * @throws IllegalArgumentException Si le pseudo est invalide
     */
    public static String validatePseudo(String pseudo) {
        if (pseudo == null || pseudo.trim().isEmpty()) {
            throw new IllegalArgumentException("Le pseudo ne peut pas être vide.");
        }

        String cleaned = pseudo.trim();

        // Vérifier la longueur
        if (cleaned.length() < 2) {
            throw new IllegalArgumentException("Le pseudo doit contenir au moins 2 caractères.");
        }
        if (cleaned.length() > 20) {
            throw new IllegalArgumentException("Le pseudo ne peut pas dépasser 20 caractères.");
        }

        // Vérifier les caractères autorisés
        if (!PSEUDO_PATTERN.matcher(cleaned).matches()) {
            throw new IllegalArgumentException("Le pseudo ne peut contenir que des lettres, chiffres, espaces, tirets et underscores.");
        }

        // Vérifier les caractères spéciaux dangereux
        if (SPECIAL_CHARS_PATTERN.matcher(cleaned).find()) {
            throw new IllegalArgumentException("Le pseudo contient des caractères non autorisés.");
        }

        return cleaned;
    }

    /**
     * Valide une réponse de l'utilisateur (titre ou artiste).
     * 
     * @param answer La réponse à valider
     * @return La réponse nettoyée
     * @throws IllegalArgumentException Si la réponse est invalide
     */
    public static String validateAnswer(String answer) {
        if (answer == null) {
            throw new IllegalArgumentException("La réponse ne peut pas être nulle.");
        }

        String cleaned = answer.trim();

        // Accepter les réponses vides (pas de réponse)
        if (cleaned.isEmpty()) {
            return cleaned;
        }

        // Vérifier la longueur maximale
        if (cleaned.length() > 100) {
            throw new IllegalArgumentException("La réponse ne peut pas dépasser 100 caractères.");
        }

        // Vérifier les caractères spéciaux dangereux
        if (SPECIAL_CHARS_PATTERN.matcher(cleaned).find()) {
            throw new IllegalArgumentException("La réponse contient des caractères non autorisés.");
        }

        return cleaned;
    }

    /**
     * Compare deux chaînes de manière normalisée (pour vérifier les réponses).
     * 
     * @param userAnswer La réponse de l'utilisateur
     * @param correctAnswer La réponse correcte
     * @return true si les réponses correspondent, false sinon
     */
    public static boolean compareAnswers(String userAnswer, String correctAnswer) {
        if (userAnswer == null || correctAnswer == null) return false;
        
        String normalizedUser = normalize(userAnswer);
        String normalizedCorrect = normalize(correctAnswer);
        
        return normalizedUser.equals(normalizedCorrect);
    }

    /**
     * Vérifie si une réponse est approximativement correcte (distance de Levenshtein).
     * Utilisé pour accepter des réponses avec de petites fautes de frappe.
     * 
     * @param userAnswer La réponse de l'utilisateur
     * @param correctAnswer La réponse correcte
     * @param threshold Le seuil de tolérance (nombre de caractères différents acceptés)
     * @return true si la réponse est approximativement correcte
     */
    public static boolean isFuzzyMatch(String userAnswer, String correctAnswer, int threshold) {
        String normalizedUser = normalize(userAnswer);
        String normalizedCorrect = normalize(correctAnswer);
        
        int distance = levenshteinDistance(normalizedUser, normalizedCorrect);
        return distance <= threshold;
    }

    /**
     * Calcule la distance de Levenshtein entre deux chaînes.
     * 
     * @param s1 Première chaîne
     * @param s2 Deuxième chaîne
     * @return La distance de Levenshtein
     */
    private static int levenshteinDistance(String s1, String s2) {
        int len1 = s1.length();
        int len2 = s2.length();
        
        int[][] dp = new int[len1 + 1][len2 + 1];
        
        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }
        
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                    Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                );
            }
        }
        
        return dp[len1][len2];
    }

    /**
     * Valide un nombre entier dans une plage.
     * 
     * @param value La valeur à valider
     * @param min La valeur minimale
     * @param max La valeur maximale
     * @param fieldName Le nom du champ (pour le message d'erreur)
     * @return La valeur validée
     * @throws IllegalArgumentException Si la valeur est hors limites
     */
    public static int validateIntRange(int value, int min, int max, String fieldName) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(
                String.format("%s doit être entre %d et %d.", fieldName, min, max)
            );
        }
        return value;
    }

    /**
     * Sanitize une chaîne pour l'export CSV (échappe les guillemets et virgules).
     * 
     * @param input La chaîne à sanitize
     * @return La chaîne sécurisée pour CSV
     */
    public static String sanitizeForCSV(String input) {
        if (input == null) return "";
        
        String sanitized = input.trim();
        
        // Si contient des guillemets, virgules ou retours à la ligne, encadrer avec des guillemets
        if (sanitized.contains("\"") || sanitized.contains(",") || sanitized.contains("\n")) {
            sanitized = "\"" + sanitized.replace("\"", "\"\"") + "\"";
        }
        
        return sanitized;
    }
}