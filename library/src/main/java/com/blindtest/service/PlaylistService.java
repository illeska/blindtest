package com.blindtest.service;

import com.blindtest.model.Playlist;
import com.blindtest.model.Track;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Service pour la gestion des playlists avec persistance JSON et génération depuis l'API.
 */
public class PlaylistService {

    /**
     * Sauvegarde une playlist dans un fichier JSON.
     * @param playlist La playlist à sauvegarder
     * @param path Le chemin du fichier
     */
    public void savePlaylist(Playlist playlist, String path) {
        try {
            PersistenceService.save(playlist, path);
        } catch (IOException e) {
            System.err.println("[PlaylistService] ERREUR: Impossible de sauvegarder la playlist dans " + path + ": " + e.getMessage());
        }
    }

    /**
     * Charge une playlist depuis un fichier JSON.
     * @param path Le chemin du fichier
     * @return La playlist chargée ou null si erreur
     */
    public Playlist loadPlaylist(String path) {
        return PersistenceService.load(path, Playlist.class);
    }

    /**
     * Génère une playlist aléatoire à partir de l'API pour un genre donné.
     * @param genre Le genre musical
     * @param numberOfTracks Le nombre de morceaux à récupérer
     * @return Une playlist générée
     */
    public Playlist generatePlaylistFromAPI(String genre, int numberOfTracks) {
        Playlist playlist = new Playlist(genre + " - Generated");

        try {
            System.out.println("🔍 Recherche de musiques du genre: " + genre);

            // Simulation de recherche (HintManager supprimé)
            List<Track> tracks = searchQuerySimulated(genre);

            if (tracks == null || tracks.isEmpty()) {
                System.out.println("⚠️ Recherche par genre vide, tentative avec des artistes populaires...");
                tracks = searchPopularTracksForGenre(genre);
            }

            if (tracks == null || tracks.isEmpty()) {
                System.err.println("❌ Aucun résultat trouvé pour le genre: " + genre);
                return null;
            }

            Collections.shuffle(tracks);
            int limit = Math.min(numberOfTracks, tracks.size());

            for (int i = 0; i < limit; i++) {
                playlist.addTrack(tracks.get(i));
            }

            System.out.println("✅ Playlist générée: " + playlist.getTracks().size() + " morceaux du genre '" + genre + "'");

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la génération de la playlist: " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        return playlist;
    }

    /**
     * Simulation : renvoie une liste vide (aucun HintManager encore)
     */
    private List<Track> searchQuerySimulated(String genre) {
        return java.util.Collections.emptyList();
    }

    /**
     * Recherche des morceaux populaires pour un genre en utilisant des artistes connus.
     * Fallback si la recherche par genre échoue.
     */
    private List<Track> searchPopularTracksForGenre(String genre) {
        List<Track> allTracks = new java.util.ArrayList<>();

        // Artistes par genre
        String[] artists = getPopularArtistsForGenre(genre);

        for (String artist : artists) {
            try {
                // Simulation → renvoie une liste vide
                List<Track> tracks = java.util.Collections.emptyList();
                if (tracks != null && !tracks.isEmpty()) {
                    allTracks.addAll(tracks);
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la recherche de l'artiste: " + artist);
            }
        }

        return allTracks;
    }

    /**
     * Retourne une liste d'artistes populaires pour un genre donné.
     * @param genre Le genre musical
     * @return Un tableau d'artistes représentatifs du genre
     */
    private String[] getPopularArtistsForGenre(String genre) {
        switch (genre.toLowerCase()) {
            case "pop":
                return new String[]{"Taylor Swift", "Ed Sheeran", "Dua Lipa", "The Weeknd", "Ariana Grande"};
            case "rock":
                return new String[]{"Queen", "Led Zeppelin", "AC/DC", "Nirvana", "Foo Fighters"};
            case "hip-hop/rap":
            case "hip-hop":
            case "rap":
                return new String[]{"Eminem", "Drake", "Kendrick Lamar", "Travis Scott", "Post Malone"};
            case "r&b":
                return new String[]{"The Weeknd", "Frank Ocean", "SZA", "Usher", "Alicia Keys"};
            case "électro/edm":
            case "electro":
            case "edm":
                return new String[]{"Daft Punk", "Calvin Harris", "David Guetta", "Avicii", "Marshmello"};
            case "jazz":
                return new String[]{"Miles Davis", "John Coltrane", "Billie Holiday", "Ella Fitzgerald", "Louis Armstrong"};
            case "metal":
                return new String[]{"Metallica", "Iron Maiden", "Slayer", "Black Sabbath", "Judas Priest"};
            case "classique":
            case "classical":
                return new String[]{"Mozart", "Beethoven", "Bach", "Chopin", "Vivaldi"};
            case "country":
                return new String[]{"Johnny Cash", "Dolly Parton", "Luke Bryan", "Carrie Underwood", "Blake Shelton"};
            case "reggae":
                return new String[]{"Bob Marley", "Peter Tosh", "Damian Marley", "Shaggy", "Sean Paul"};
            default:
                return new String[]{"The Beatles", "Michael Jackson", "Madonna", "Coldplay", "Maroon 5"};
        }
    }
}
