package com.blindtest.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;

public class PlaylistService {
    private static final Gson gson = new Gson();

    public void savePlaylist(Playlist playlist, String path) {
        try (FileWriter writer = new FileWriter(path)) {
            gson.toJson(playlist, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Playlist loadPlaylist(String path) {
        try (FileReader reader = new FileReader(path)) {
            return gson.fromJson(reader, Playlist.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
