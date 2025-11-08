package com.blindtest.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service général pour la persistance JSON.
 * Gère la sauvegarde et le chargement des données au format JSON.
 */
public class PersistenceService {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .setPrettyPrinting()
            .create();

    /**
     * Sauvegarde un objet au format JSON dans un fichier.
     * @param object L'objet à sauvegarder
     * @param path Le chemin du fichier
     * @throws IOException En cas d'erreur d'écriture
     */
    public static void save(Object object, String path) throws IOException {
        ensureDirectoryExists(path);
        try (FileWriter writer = new FileWriter(path)) {
            gson.toJson(object, writer);
        }
    }

    /**
     * Charge un objet depuis un fichier JSON.
     * @param path Le chemin du fichier
     * @param classOfT La classe de l'objet
     * @param <T> Le type de l'objet
     * @return L'objet chargé, ou null si le fichier n'existe pas ou en cas d'erreur
     */
    public static <T> T load(String path, Class<T> classOfT) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        try (FileReader reader = new FileReader(file)) {
            return gson.fromJson(reader, classOfT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Charge une liste d'objets depuis un fichier JSON.
     * @param path Le chemin du fichier
     * @param typeToken Le TypeToken pour la liste (ex: {@code new TypeToken<List<Score>>(){}})
     * @param <T> Le type des éléments de la liste
     * @return La liste chargée, ou une liste vide si le fichier n'existe pas ou en cas d'erreur
     */
    public static <T> List<T> loadList(String path, TypeToken<List<T>> typeToken) {
        File file = new File(path);
        if (!file.exists()) {
            return new java.util.ArrayList<>();
        }
        try (FileReader reader = new FileReader(file)) {
            Type type = typeToken.getType();
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    /**
     * Assure que le répertoire parent du fichier existe.
     * @param path Le chemin du fichier
     */
    private static void ensureDirectoryExists(String path) {
        File file = new File(path);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
    }
}
