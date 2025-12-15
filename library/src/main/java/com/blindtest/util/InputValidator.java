package com.blindtest.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Utilitaire pour la validation et la normalisation des entrées utilisateur.
 * Gère les accents, caractères spéciaux, espaces, comparaison tolérante, CSV, etc.
 */
public class InputValidator {

    // Patterns pour validation
    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s-_]+$");
    private static final Pattern PSEUDO_PATTERN = Pattern.compile("^[a-zA-Z0-9\\s_-]+$");
    private static final Pattern SPECIAL_CHARS_PATTERN = Pattern.compile("[<>\"'&;{}()]");
    private static final Pattern UNSAFE_CHARS_PATTERN = Pattern.compile("[^a-zA-Z0-9\\s-_]");

    /**
     * Nettoie et normalise une entrée utilisateur (pseudo, réponse).
     */
    public static String sanitize(String input) {
        if (input == null || input.isEmpty()) return "";

        String cleaned = input.trim();
        cleaned = Normalizer.normalize(cleaned, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        cleaned = UNSAFE_CHARS_PATTERN.matcher(cleaned).replaceAll(" ");
        cleaned = cleaned.replaceAll("\\s+", " ").trim();

        return cleaned;
    }

    /**
     * Normalise une réponse pour comparaison stricte ou approximative.
     */
    public static String normalize(String input) {
        if (input == null) return "";

        String normalized = sanitize(input).toLowerCase();

        // Supprime les mots-outils pour comparaison plus tolérante
        normalized = removeCommonWords(normalized);

        return normalized.replaceAll("\\s+", " ").trim();
    }

    private static String removeCommonWords(String text) {
        String[] stopWords = {
            "the", "a", "an", "le", "la", "les", "un", "une", "des",
            "of", "de", "du", "d", "l", "&", "and", "et"
        };

        String result = " " + text + " ";
        for (String word : stopWords) {
            result = result.replaceAll("(?i)\\s" + word + "\\s", " ");
        }

        return result.trim();
    }

    /**
     * Valide et nettoie un pseudo utilisateur.
     */
    public static String validatePseudo(String pseudo) {
        if (pseudo == null || pseudo.trim().isEmpty()) {
            throw new IllegalArgumentException("Le pseudo ne peut pas être vide.");
        }

        String cleaned = pseudo.trim();

        if (cleaned.length() < 2 || cleaned.length() > 20) {
            throw new IllegalArgumentException("Le pseudo doit contenir entre 2 et 20 caractères.");
        }

        if (!PSEUDO_PATTERN.matcher(cleaned).matches() || SPECIAL_CHARS_PATTERN.matcher(cleaned).find()) {
            throw new IllegalArgumentException("Le pseudo contient des caractères non autorisés.");
        }

        return cleaned;
    }

    /**
     * Valide une réponse de l'utilisateur.
     */
    public static String validateAnswer(String answer) {
        if (answer == null) throw new IllegalArgumentException("La réponse ne peut pas être nulle.");

        String cleaned = answer.trim();
        if (cleaned.isEmpty()) return cleaned;
        if (cleaned.length() > 100) throw new IllegalArgumentException("La réponse ne peut pas dépasser 100 caractères.");
        if (SPECIAL_CHARS_PATTERN.matcher(cleaned).find()) throw new IllegalArgumentException("La réponse contient des caractères non autorisés.");

        return cleaned;
    }

    /**
     * Compare deux réponses de manière stricte.
     */
    public static boolean compareAnswers(String userAnswer, String correctAnswer) {
        return normalize(userAnswer).equals(normalize(correctAnswer));
    }

    /**
     * Compare deux réponses avec tolérance aux fautes de frappe.
     */
    public static boolean isFuzzyMatch(String userAnswer, String correctAnswer, int threshold) {
        String normUser = normalize(userAnswer);
        String normCorrect = normalize(correctAnswer);

        int distance = levenshteinDistance(normUser, normCorrect);
        return distance <= threshold;
    }

    /**
     * Calcule la similarité entre deux chaînes (0 à 1).
     */
    public static double similarity(String s1, String s2) {
        if (s1 == null || s2 == null) return 0.0;
        if (s1.equals(s2)) return 1.0;

        int maxLength = Math.max(s1.length(), s2.length());
        if (maxLength == 0) return 1.0;

        int distance = levenshteinDistance(s1, s2);
        return 1.0 - ((double) distance / maxLength);
    }

    /**
     * Distance de Levenshtein.
     */
    private static int levenshteinDistance(String s1, String s2) {
        int len1 = s1.length();
        int len2 = s2.length();

        int[][] dp = new int[len1 + 1][len2 + 1];
        for (int i = 0; i <= len1; i++) dp[i][0] = i;
        for (int j = 0; j <= len2; j++) dp[0][j] = j;

        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                int cost = s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }

        return dp[len1][len2];
    }

    /**
     * Valide un entier dans une plage.
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
     * Sanitize pour export CSV.
     */
    public static String sanitizeForCSV(String input) {
        if (input == null) return "";

        String sanitized = input.trim();
        if (sanitized.contains("\"") || sanitized.contains(",") || sanitized.contains("\n")) {
            sanitized = "\"" + sanitized.replace("\"", "\"\"") + "\"";
        }

        return sanitized;
    }
    public static String normalizeAnswer(String answer) {
        return normalize(answer);
    }

    /**
     * Vérifie si un pseudo est valide (true/false).
     * Alias pour compatibilité avec l'ancien code.
     */
    public static boolean isValidPseudo(String pseudo) {
        try {
            validatePseudo(pseudo);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
