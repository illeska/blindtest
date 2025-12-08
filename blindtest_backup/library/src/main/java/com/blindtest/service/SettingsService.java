package com.blindtest.service;

import com.blindtest.model.Settings;
import java.io.IOException;

/**
 * Service pour la gestion des paramètres de configuration.
 * Utilise PersistenceService pour sauvegarder et charger les settings au format JSON.
 */
public class SettingsService {
    private static final String SETTINGS_FILE = "data/settings.json";

    /**
     * Sauvegarde les paramètres dans le fichier JSON.
     * @param settings Les paramètres à sauvegarder
     */
    public static void saveSettings(Settings settings) {
        try {
            PersistenceService.save(settings, SETTINGS_FILE);
        } catch (IOException e) {
            // 🔥 MODIFICATION : Meilleure gestion d'erreur
            System.err.println("[SettingsService] ERREUR: Impossible de sauvegarder les paramètres dans " + SETTINGS_FILE + ": " + e.getMessage());
        }
    }

    /**
     * Charge les paramètres depuis le fichier JSON.
     * @return Les paramètres chargés, ou des paramètres par défaut si le fichier n'existe pas
     */
    public static Settings loadSettings() {
        Settings settings = PersistenceService.load(SETTINGS_FILE, Settings.class);
        if (settings == null) {
            settings = new Settings();
        }
        return settings;
    }
}