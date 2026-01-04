package com.blindtest.service;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Adaptateur Gson pour LocalDateTime.
 */
public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Sérialise un objet LocalDateTime en chaîne JSON.
     * @param out Le writer JSON
     * @param value La date à sérialiser
     * @throws IOException en cas d'erreur d'écriture
     */
    @Override
    public void write(JsonWriter out, LocalDateTime value) throws IOException {
        out.value(value.format(formatter));
    }

    /**
     * Désérialise une chaîne JSON en objet LocalDateTime.
     * @param in Le reader JSON
     * @return La date désérialisée
     * @throws IOException en cas d'erreur de lecture
     */
    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        return LocalDateTime.parse(in.nextString(), formatter);
    }
}
