package com.blindtest.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blindtest.model.Playlist;
import com.blindtest.model.Track;

/**
 * Générateur de playlist dynamique basé sur le genre musical.
 */
public class DynamicPlaylistGenerator {
    
    private static final Map<String, List<Track>> GENRE_TRACKS = new HashMap<>();
    
    static {
        // === POP ===
        List<Track> popTracks = new ArrayList<>();
        popTracks.add(new Track("Shape of You", "Ed Sheeran", 30));
        popTracks.add(new Track("Blinding Lights", "The Weeknd", 30));
        popTracks.add(new Track("Levitating", "Dua Lipa", 30));
        popTracks.add(new Track("Watermelon Sugar", "Harry Styles", 30));
        popTracks.add(new Track("Anti-Hero", "Taylor Swift", 30));
        popTracks.add(new Track("As It Was", "Harry Styles", 30));
        popTracks.add(new Track("Flowers", "Miley Cyrus", 30));
        popTracks.add(new Track("Dance The Night", "Dua Lipa", 30));
        popTracks.add(new Track("Unholy", "Sam Smith", 30));
        popTracks.add(new Track("Bad Habits", "Ed Sheeran", 30));
        popTracks.add(new Track("Stay", "The Kid LAROI", 30));
        popTracks.add(new Track("Heat Waves", "Glass Animals", 30));
        popTracks.add(new Track("Shivers", "Ed Sheeran", 30));
        popTracks.add(new Track("Good 4 U", "Olivia Rodrigo", 30));
        popTracks.add(new Track("drivers license", "Olivia Rodrigo", 30));
        GENRE_TRACKS.put("pop", popTracks);
        
        // === ROCK ===
        List<Track> rockTracks = new ArrayList<>();
        rockTracks.add(new Track("Bohemian Rhapsody", "Queen", 30));
        rockTracks.add(new Track("Stairway to Heaven", "Led Zeppelin", 30));
        rockTracks.add(new Track("Sweet Child O' Mine", "Guns N' Roses", 30));
        rockTracks.add(new Track("Hotel California", "Eagles", 30));
        rockTracks.add(new Track("Smells Like Teen Spirit", "Nirvana", 30));
        rockTracks.add(new Track("Wonderwall", "Oasis", 30));
        rockTracks.add(new Track("Back in Black", "AC/DC", 30));
        rockTracks.add(new Track("Highway to Hell", "AC/DC", 30));
        rockTracks.add(new Track("November Rain", "Guns N' Roses", 30));
        rockTracks.add(new Track("Paranoid", "Black Sabbath", 30));
        rockTracks.add(new Track("Enter Sandman", "Metallica", 30));
        rockTracks.add(new Track("The Final Countdown", "Europe", 30));
        rockTracks.add(new Track("Don't Stop Believin'", "Journey", 30));
        rockTracks.add(new Track("Livin' on a Prayer", "Bon Jovi", 30));
        rockTracks.add(new Track("Eye of the Tiger", "Survivor", 30));
        GENRE_TRACKS.put("rock", rockTracks);
        
        // === HIP-HOP/RAP ===
        List<Track> hipHopTracks = new ArrayList<>();
        hipHopTracks.add(new Track("Lose Yourself", "Eminem", 30));
        hipHopTracks.add(new Track("God's Plan", "Drake", 30));
        hipHopTracks.add(new Track("HUMBLE.", "Kendrick Lamar", 30));
        hipHopTracks.add(new Track("Sicko Mode", "Travis Scott", 30));
        hipHopTracks.add(new Track("Hotline Bling", "Drake", 30));
        hipHopTracks.add(new Track("In Da Club", "50 Cent", 30));
        hipHopTracks.add(new Track("Without Me", "Eminem", 30));
        hipHopTracks.add(new Track("Mockingbird", "Eminem", 30));
        hipHopTracks.add(new Track("Stan", "Eminem", 30));
        hipHopTracks.add(new Track("Alright", "Kendrick Lamar", 30));
        hipHopTracks.add(new Track("Money Trees", "Kendrick Lamar", 30));
        hipHopTracks.add(new Track("One Dance", "Drake", 30));
        hipHopTracks.add(new Track("Rockstar", "Post Malone", 30));
        hipHopTracks.add(new Track("Sunflower", "Post Malone", 30));
        hipHopTracks.add(new Track("Circles", "Post Malone", 30));
        GENRE_TRACKS.put("hip-hop", hipHopTracks);
        GENRE_TRACKS.put("rap", hipHopTracks);
        GENRE_TRACKS.put("hip-hop/rap", hipHopTracks);
        
        // === R&B ===
        List<Track> rnbTracks = new ArrayList<>();
        rnbTracks.add(new Track("Redbone", "Childish Gambino", 30));
        rnbTracks.add(new Track("Blinding Lights", "The Weeknd", 30));
        rnbTracks.add(new Track("Starboy", "The Weeknd", 30));
        rnbTracks.add(new Track("The Hills", "The Weeknd", 30));
        rnbTracks.add(new Track("Earned It", "The Weeknd", 30));
        rnbTracks.add(new Track("Thinkin Bout You", "Frank Ocean", 30));
        rnbTracks.add(new Track("Pyramids", "Frank Ocean", 30));
        rnbTracks.add(new Track("Yeah!", "Usher", 30));
        rnbTracks.add(new Track("No Diggity", "Blackstreet", 30));
        rnbTracks.add(new Track("Pony", "Ginuwine", 30));
        rnbTracks.add(new Track("Say My Name", "Destiny's Child", 30));
        rnbTracks.add(new Track("No Scrubs", "TLC", 30));
        rnbTracks.add(new Track("Crazy in Love", "Beyonce", 30));
        rnbTracks.add(new Track("Irreplaceable", "Beyonce", 30));
        rnbTracks.add(new Track("Single Ladies", "Beyonce", 30));
        GENRE_TRACKS.put("r&b", rnbTracks);
        
        // === ÉLECTRO/EDM ===
        List<Track> electroTracks = new ArrayList<>();
        electroTracks.add(new Track("Get Lucky", "Daft Punk", 30));
        electroTracks.add(new Track("One More Time", "Daft Punk", 30));
        electroTracks.add(new Track("Titanium", "David Guetta ft. Sia", 30));
        electroTracks.add(new Track("Wake Me Up", "Avicii", 30));
        electroTracks.add(new Track("Levels", "Avicii", 30));
        electroTracks.add(new Track("Animals", "Martin Garrix", 30));
        electroTracks.add(new Track("Don't You Worry Child", "Swedish House Mafia", 30));
        electroTracks.add(new Track("Clarity", "Zedd", 30));
        electroTracks.add(new Track("Fade", "Alan Walker", 30));
        electroTracks.add(new Track("Scared to Be Lonely", "Martin Garrix", 30));
        electroTracks.add(new Track("This Is What You Came For", "Calvin Harris", 30));
        electroTracks.add(new Track("Summer", "Calvin Harris", 30));
        electroTracks.add(new Track("Closer", "The Chainsmokers", 30));
        electroTracks.add(new Track("Something Just Like This", "The Chainsmokers", 30));
        electroTracks.add(new Track("Happier", "Marshmello", 30));
        GENRE_TRACKS.put("électro", electroTracks);
        GENRE_TRACKS.put("electro", electroTracks);
        GENRE_TRACKS.put("edm", electroTracks);
        GENRE_TRACKS.put("électro/edm", electroTracks);
        
        // === JAZZ ===
        List<Track> jazzTracks = new ArrayList<>();
        jazzTracks.add(new Track("Take Five", "Dave Brubeck", 30));
        jazzTracks.add(new Track("So What", "Miles Davis", 30));
        jazzTracks.add(new Track("Round Midnight", "Thelonious Monk", 30));
        jazzTracks.add(new Track("My Favorite Things", "John Coltrane", 30));
        jazzTracks.add(new Track("Summertime", "Ella Fitzgerald", 30));
        jazzTracks.add(new Track("What a Wonderful World", "Louis Armstrong", 30));
        jazzTracks.add(new Track("Fly Me to the Moon", "Frank Sinatra", 30));
        jazzTracks.add(new Track("The Girl from Ipanema", "Stan Getz", 30));
        jazzTracks.add(new Track("Blue in Green", "Miles Davis", 30));
        jazzTracks.add(new Track("Autumn Leaves", "Bill Evans", 30));
        GENRE_TRACKS.put("jazz", jazzTracks);
        
        // === CLASSIQUES (Oldies) ===
        List<Track> classicTracks = new ArrayList<>();
        classicTracks.add(new Track("Billie Jean", "Michael Jackson", 30));
        classicTracks.add(new Track("Thriller", "Michael Jackson", 30));
        classicTracks.add(new Track("Beat It", "Michael Jackson", 30));
        classicTracks.add(new Track("Imagine", "John Lennon", 30));
        classicTracks.add(new Track("Hey Jude", "The Beatles", 30));
        classicTracks.add(new Track("Let It Be", "The Beatles", 30));
        classicTracks.add(new Track("Yesterday", "The Beatles", 30));
        classicTracks.add(new Track("What's Going On", "Marvin Gaye", 30));
        classicTracks.add(new Track("Respect", "Aretha Franklin", 30));
        classicTracks.add(new Track("I Will Always Love You", "Whitney Houston", 30));
        classicTracks.add(new Track("Purple Rain", "Prince", 30));
        classicTracks.add(new Track("Like a Rolling Stone", "Bob Dylan", 30));
        classicTracks.add(new Track("Take on Me", "A-ha", 30));
        classicTracks.add(new Track("Every Breath You Take", "The Police", 30));
        classicTracks.add(new Track("Sweet Child O' Mine", "Guns N' Roses", 30));
        GENRE_TRACKS.put("classique", classicTracks);
        GENRE_TRACKS.put("oldies", classicTracks);
    }
    
    /**
     * Génère une playlist basée sur le genre et le nombre de morceaux demandé.
     * @param genre Le genre musical
     * @param numberOfTracks Le nombre de morceaux à inclure
     * @return Une playlist générée
     */
    public static Playlist generatePlaylist(String genre, int numberOfTracks) {
        String normalizedGenre = genre.toLowerCase().trim();
        
        List<Track> availableTracks = GENRE_TRACKS.get(normalizedGenre);
        
        // Si le genre n'existe pas, utiliser pop par défaut
        if (availableTracks == null || availableTracks.isEmpty()) {
            System.out.println("[DynamicPlaylist] Genre '" + genre + "' inconnu, utilisation de 'pop' par defaut");
            availableTracks = GENRE_TRACKS.get("pop");
        }
        
        // Mélanger les morceaux
        List<Track> shuffledTracks = new ArrayList<>(availableTracks);
        Collections.shuffle(shuffledTracks);
        
        // Créer la playlist
        Playlist playlist = new Playlist(genre + " Playlist");
        
        // Ajouter le nombre demandé de morceaux
        int count = Math.min(numberOfTracks, shuffledTracks.size());
        for (int i = 0; i < count; i++) {
            playlist.addTrack(shuffledTracks.get(i));
        }
        
        // Si on a besoin de plus de morceaux que disponible, on recommence la liste
        if (numberOfTracks > shuffledTracks.size()) {
            int remaining = numberOfTracks - shuffledTracks.size();
            for (int i = 0; i < remaining; i++) {
                playlist.addTrack(shuffledTracks.get(i % shuffledTracks.size()));
            }
        }
        
        System.out.println("[DynamicPlaylist] Playlist generee avec " + playlist.getTracks().size() + " morceaux du genre '" + genre + "'");
        
        return playlist;
    }
    
    /**
     * Liste tous les genres disponibles.
     */
    public static List<String> getAvailableGenres() {
        return new ArrayList<>(GENRE_TRACKS.keySet());
    }
}