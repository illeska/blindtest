package com.blindtest.service;

import java.io.IOException;

/**
 * Service pour la gestion des playlists avec persistance JSON.
 */
public class PlaylistService {

    public void savePlaylist(Playlist playlist, String path) {
        try {
            PersistenceService.save(playlist, path);
        } catch (IOException e) {
            // 🔥 MODIFICATION : Meilleure gestion d'erreur
            System.err.println("[PlaylistService] ERREUR: Impossible de sauvegarder la playlist dans " + path + ": " + e.getMessage());
        }
    }

    public Playlist loadPlaylist(String path) {
        // PersistenceService.load gère déjà les erreurs de lecture et retourne null si le fichier n'est pas trouvé.
        return PersistenceService.load(path, Playlist.class);
    }
}