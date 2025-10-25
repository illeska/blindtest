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
            e.printStackTrace();
        }
    }

    public Playlist loadPlaylist(String path) {
        return PersistenceService.load(path, Playlist.class);
    }
}
