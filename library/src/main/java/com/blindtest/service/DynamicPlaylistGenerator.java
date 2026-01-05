package com.blindtest.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.blindtest.model.Playlist;
import com.blindtest.model.Track;

/**
 * Générateur de playlist dynamique basé sur le genre musical.
 * 100 morceaux par genre - Mix français/anglais avec focus sur 2000-2010 et 2020+
 */
public class DynamicPlaylistGenerator {
    
    private static final Map<String, List<Track>> GENRE_TRACKS = new HashMap<>();
    
    static {
        // === POP ===
        List<Track> popTracks = new ArrayList<>();
        // Français 2000-2010
        popTracks.add(new Track("Dernière Danse", "Kyo", 30));
        popTracks.add(new Track("Je veux", "Zaz", 30));
        popTracks.add(new Track("Alors on danse", "Stromae", 30));
        popTracks.add(new Track("Formidable", "Stromae", 30));
        popTracks.add(new Track("Papaoutai", "Stromae", 30));
        popTracks.add(new Track("Tous les mêmes", "Stromae", 30));
        popTracks.add(new Track("L'avenir", "Louane", 30));
        popTracks.add(new Track("Jour 1", "Louane", 30));
        popTracks.add(new Track("Maman", "Louane", 30));
        popTracks.add(new Track("La Même", "Maître Gims", 30));
        popTracks.add(new Track("Bella", "Maître Gims", 30));
        popTracks.add(new Track("J'me tire", "Maître Gims", 30));
        popTracks.add(new Track("Sapés comme jamais", "Maître Gims", 30));
        popTracks.add(new Track("Est-ce que tu m'aimes?", "Maître Gims", 30));
        popTracks.add(new Track("Balance ton quoi", "Angèle", 30));
        popTracks.add(new Track("Ta reine", "Angèle", 30));
        popTracks.add(new Track("Tout oublier", "Angèle", 30));
        popTracks.add(new Track("Flou", "Angèle", 30));
        popTracks.add(new Track("Bruxelles je t'aime", "Angèle", 30));
        popTracks.add(new Track("On était beau", "Louane", 30));
        popTracks.add(new Track("Désolé", "Soprano", 30));
        popTracks.add(new Track("Le coach", "Soprano", 30));
        popTracks.add(new Track("Cosmo", "Soprano", 30));
        popTracks.add(new Track("À nos héros du quotidien", "Soprano", 30));
        popTracks.add(new Track("Dommage", "Bigflo & Oli", 30));
        popTracks.add(new Track("Plus tard", "Bigflo & Oli", 30));
        popTracks.add(new Track("Personne", "Bigflo & Oli", 30));
        popTracks.add(new Track("Sur la lune", "Bigflo & Oli", 30));
        popTracks.add(new Track("Nous aussi", "Claudio Capéo", 30));
        popTracks.add(new Track("Un homme debout", "Claudio Capéo", 30));
        
        // Français 2020+
        popTracks.add(new Track("Dernière danse", "Indila", 30));
        popTracks.add(new Track("Love nwantiti", "CKay", 30));
        popTracks.add(new Track("Bande organisée", "13 Organisé", 30));
        popTracks.add(new Track("Djadja", "Aya Nakamura", 30));
        popTracks.add(new Track("Pookie", "Aya Nakamura", 30));
        popTracks.add(new Track("Copines", "Aya Nakamura", 30));
        popTracks.add(new Track("Jolie nana", "Aya Nakamura", 30));
        popTracks.add(new Track("La dot", "Aya Nakamura", 30));
        popTracks.add(new Track("Bling Bling", "Aya Nakamura", 30));
        popTracks.add(new Track("Tout va bien", "Aya Nakamura", 30));
        popTracks.add(new Track("Brisé", "Ninho", 30));
        popTracks.add(new Track("Lettre à une femme", "Ninho", 30));
        popTracks.add(new Track("Jefe", "Ninho", 30));
        popTracks.add(new Track("VVS", "Ninho", 30));
        popTracks.add(new Track("Calma", "Pedro Capó", 30));
        popTracks.add(new Track("Bam Bam", "Camila Cabello", 30));
        popTracks.add(new Track("La vie en rose", "Zaz", 30));
        
        // Anglais 2000-2010
        popTracks.add(new Track("Shape of You", "Ed Sheeran", 30));
        popTracks.add(new Track("Umbrella", "Rihanna", 30));
        popTracks.add(new Track("We Found Love", "Rihanna", 30));
        popTracks.add(new Track("Diamonds", "Rihanna", 30));
        popTracks.add(new Track("Poker Face", "Lady Gaga", 30));
        popTracks.add(new Track("Bad Romance", "Lady Gaga", 30));
        popTracks.add(new Track("Just Dance", "Lady Gaga", 30));
        popTracks.add(new Track("Paparazzi", "Lady Gaga", 30));
        popTracks.add(new Track("Telephone", "Lady Gaga", 30));
        popTracks.add(new Track("Roar", "Katy Perry", 30));
        popTracks.add(new Track("Firework", "Katy Perry", 30));
        popTracks.add(new Track("Dark Horse", "Katy Perry", 30));
        popTracks.add(new Track("California Gurls", "Katy Perry", 30));
        popTracks.add(new Track("Teenage Dream", "Katy Perry", 30));
        popTracks.add(new Track("Rolling in the Deep", "Adele", 30));
        popTracks.add(new Track("Someone Like You", "Adele", 30));
        popTracks.add(new Track("Set Fire to the Rain", "Adele", 30));
        popTracks.add(new Track("Hello", "Adele", 30));
        popTracks.add(new Track("Skyfall", "Adele", 30));
        
        // Anglais 2020+
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
        popTracks.add(new Track("Easy On Me", "Adele", 30));
        popTracks.add(new Track("Cruel Summer", "Taylor Swift", 30));
        popTracks.add(new Track("Shake It Off", "Taylor Swift", 30));
        popTracks.add(new Track("Blank Space", "Taylor Swift", 30));
        popTracks.add(new Track("Love Story", "Taylor Swift", 30));
        popTracks.add(new Track("Uptown Funk", "Bruno Mars", 30));
        popTracks.add(new Track("That's What I Like", "Bruno Mars", 30));
        popTracks.add(new Track("Leave The Door Open", "Bruno Mars", 30));
        popTracks.add(new Track("Die With A Smile", "Lady Gaga & Bruno Mars", 30));
        popTracks.add(new Track("Happier Than Ever", "Billie Eilish", 30));
        popTracks.add(new Track("bad guy", "Billie Eilish", 30));
        popTracks.add(new Track("Espresso", "Sabrina Carpenter", 30));
        GENRE_TRACKS.put("pop", popTracks);
        
        // === ROCK ===
        List<Track> rockTracks = new ArrayList<>();
        // Français 2000-2010
        rockTracks.add(new Track("Le vent nous portera", "Noir Désir", 30));
        rockTracks.add(new Track("Des armes", "Noir Désir", 30));
        rockTracks.add(new Track("L'homme pressé", "Noir Désir", 30));
        rockTracks.add(new Track("Tostaky", "Noir Désir", 30));
        rockTracks.add(new Track("Un jour en France", "Noir Désir", 30));
        rockTracks.add(new Track("Hier encore", "Téléphone", 30));
        rockTracks.add(new Track("Ça c'est vraiment toi", "Téléphone", 30));
        rockTracks.add(new Track("New York avec toi", "Téléphone", 30));
        rockTracks.add(new Track("Un autre monde", "Téléphone", 30));
        rockTracks.add(new Track("La bombe humaine", "Téléphone", 30));
        rockTracks.add(new Track("Peau de chagrin", "Louise Attaque", 30));
        rockTracks.add(new Track("Les nuits parisiennes", "Louise Attaque", 30));
        rockTracks.add(new Track("Léa", "Louise Attaque", 30));
        rockTracks.add(new Track("Je t'emmène au vent", "Louise Attaque", 30));
        rockTracks.add(new Track("Ton invitation", "Louise Attaque", 30));
        rockTracks.add(new Track("L'aventurier", "Indochine", 30));
        rockTracks.add(new Track("3 nuits par semaine", "Indochine", 30));
        rockTracks.add(new Track("J'ai demandé à la lune", "Indochine", 30));
        rockTracks.add(new Track("College Boy", "Indochine", 30));
        rockTracks.add(new Track("Un été français", "Indochine", 30));
        rockTracks.add(new Track("Nos célébrations", "Indochine", 30));
        rockTracks.add(new Track("Le Grand Secret", "Indochine", 30));
        rockTracks.add(new Track("Song 2", "Blur", 30));
        rockTracks.add(new Track("Come As You Are", "Nirvana", 30));
        rockTracks.add(new Track("Heart-Shaped Box", "Nirvana", 30));
        rockTracks.add(new Track("All Apologies", "Nirvana", 30));
        rockTracks.add(new Track("The Man Who Sold The World", "Nirvana", 30));
        rockTracks.add(new Track("Lithium", "Nirvana", 30));
        rockTracks.add(new Track("About a Girl", "Nirvana", 30));
        rockTracks.add(new Track("In Bloom", "Nirvana", 30));
        
        // Français 2020+
        rockTracks.add(new Track("Lettre à France", "Michel Polnareff", 30));
        rockTracks.add(new Track("Starmania", "Collectif", 30));
        rockTracks.add(new Track("Tombé du ciel", "Jacques Higelin", 30));
        rockTracks.add(new Track("Le Sud", "Nino Ferrer", 30));
        rockTracks.add(new Track("La groupie du pianiste", "Michel Berger", 30));
        
        // Anglais classiques & iconiques
        rockTracks.add(new Track("Bohemian Rhapsody", "Queen", 30));
        rockTracks.add(new Track("We Will Rock You", "Queen", 30));
        rockTracks.add(new Track("We Are The Champions", "Queen", 30));
        rockTracks.add(new Track("Don't Stop Me Now", "Queen", 30));
        rockTracks.add(new Track("Another One Bites the Dust", "Queen", 30));
        rockTracks.add(new Track("Radio Ga Ga", "Queen", 30));
        rockTracks.add(new Track("I Want to Break Free", "Queen", 30));
        rockTracks.add(new Track("Somebody to Love", "Queen", 30));
        rockTracks.add(new Track("Stairway to Heaven", "Led Zeppelin", 30));
        rockTracks.add(new Track("Whole Lotta Love", "Led Zeppelin", 30));
        rockTracks.add(new Track("Kashmir", "Led Zeppelin", 30));
        rockTracks.add(new Track("Black Dog", "Led Zeppelin", 30));
        rockTracks.add(new Track("Sweet Child O' Mine", "Guns N' Roses", 30));
        rockTracks.add(new Track("November Rain", "Guns N' Roses", 30));
        rockTracks.add(new Track("Paradise City", "Guns N' Roses", 30));
        rockTracks.add(new Track("Welcome to the Jungle", "Guns N' Roses", 30));
        rockTracks.add(new Track("Patience", "Guns N' Roses", 30));
        rockTracks.add(new Track("Don't Cry", "Guns N' Roses", 30));
        rockTracks.add(new Track("Hotel California", "Eagles", 30));
        rockTracks.add(new Track("Take It Easy", "Eagles", 30));
        rockTracks.add(new Track("Desperado", "Eagles", 30));
        rockTracks.add(new Track("Smells Like Teen Spirit", "Nirvana", 30));
        rockTracks.add(new Track("Wonderwall", "Oasis", 30));
        rockTracks.add(new Track("Don't Look Back in Anger", "Oasis", 30));
        rockTracks.add(new Track("Champagne Supernova", "Oasis", 30));
        rockTracks.add(new Track("Live Forever", "Oasis", 30));
        rockTracks.add(new Track("Back in Black", "AC/DC", 30));
        rockTracks.add(new Track("Highway to Hell", "AC/DC", 30));
        rockTracks.add(new Track("Thunderstruck", "AC/DC", 30));
        rockTracks.add(new Track("T.N.T.", "AC/DC", 30));
        rockTracks.add(new Track("You Shook Me All Night Long", "AC/DC", 30));
        rockTracks.add(new Track("Paranoid", "Black Sabbath", 30));
        rockTracks.add(new Track("Iron Man", "Black Sabbath", 30));
        rockTracks.add(new Track("War Pigs", "Black Sabbath", 30));
        rockTracks.add(new Track("Enter Sandman", "Metallica", 30));
        rockTracks.add(new Track("Nothing Else Matters", "Metallica", 30));
        rockTracks.add(new Track("Master of Puppets", "Metallica", 30));
        rockTracks.add(new Track("One", "Metallica", 30));
        rockTracks.add(new Track("The Unforgiven", "Metallica", 30));
        rockTracks.add(new Track("The Final Countdown", "Europe", 30));
        rockTracks.add(new Track("Don't Stop Believin'", "Journey", 30));
        rockTracks.add(new Track("Livin' on a Prayer", "Bon Jovi", 30));
        rockTracks.add(new Track("It's My Life", "Bon Jovi", 30));
        rockTracks.add(new Track("You Give Love a Bad Name", "Bon Jovi", 30));
        rockTracks.add(new Track("Wanted Dead or Alive", "Bon Jovi", 30));
        rockTracks.add(new Track("Eye of the Tiger", "Survivor", 30));
        rockTracks.add(new Track("Africa", "Toto", 30));
        rockTracks.add(new Track("Hold The Line", "Toto", 30));
        rockTracks.add(new Track("Jump", "Van Halen", 30));
        rockTracks.add(new Track("Dream On", "Aerosmith", 30));
        rockTracks.add(new Track("I Don't Want to Miss a Thing", "Aerosmith", 30));
        rockTracks.add(new Track("Walk This Way", "Aerosmith", 30));
        rockTracks.add(new Track("Sweet Emotion", "Aerosmith", 30));
        rockTracks.add(new Track("Born to Run", "Bruce Springsteen", 30));
        rockTracks.add(new Track("Dancing in the Dark", "Bruce Springsteen", 30));
        rockTracks.add(new Track("Should I Stay or Should I Go", "The Clash", 30));
        rockTracks.add(new Track("London Calling", "The Clash", 30));
        rockTracks.add(new Track("With or Without You", "U2", 30));
        rockTracks.add(new Track("One", "U2", 30));
        rockTracks.add(new Track("Sunday Bloody Sunday", "U2", 30));
        rockTracks.add(new Track("Beautiful Day", "U2", 30));
        rockTracks.add(new Track("Where the Streets Have No Name", "U2", 30));
        GENRE_TRACKS.put("rock", rockTracks);
        
       // === HIP-HOP/RAP ===
        List<Track> hipHopRapTracks = new ArrayList<>();
        
        // 40 SONS US (MIX 2000-2010 & 2020+)
        hipHopRapTracks.add(new Track("Lose Yourself", "Eminem", 30));
        hipHopRapTracks.add(new Track("In Da Club", "50 Cent", 30));
        hipHopRapTracks.add(new Track("Stan", "Eminem", 30));
        hipHopRapTracks.add(new Track("Hot in Herre", "Nelly", 30));
        hipHopRapTracks.add(new Track("Dilemma", "Nelly", 30));
        hipHopRapTracks.add(new Track("Ms. Jackson", "Outkast", 30));
        hipHopRapTracks.add(new Track("Gold Digger", "Kanye West", 30));
        hipHopRapTracks.add(new Track("Empire State of Mind", "Jay-Z", 30));
        hipHopRapTracks.add(new Track("Lollipop", "Lil Wayne", 30));
        hipHopRapTracks.add(new Track("Drop It Like It's Hot", "Snoop Dogg", 30));
        hipHopRapTracks.add(new Track("Crank That", "Soulja Boy", 30));
        hipHopRapTracks.add(new Track("Candy Shop", "50 Cent", 30));
        hipHopRapTracks.add(new Track("Smack That", "Akon", 30));
        hipHopRapTracks.add(new Track("Stronger", "Kanye West", 30));
        hipHopRapTracks.add(new Track("A Milli", "Lil Wayne", 30));
        hipHopRapTracks.add(new Track("Paper Planes", "M.I.A.", 30));
        hipHopRapTracks.add(new Track("Low", "Flo Rida", 30));
        hipHopRapTracks.add(new Track("Without Me", "Eminem", 30));
        hipHopRapTracks.add(new Track("The Next Episode", "Dr. Dre", 30));
        hipHopRapTracks.add(new Track("Hate It or Love It", "The Game", 30));
        hipHopRapTracks.add(new Track("Not Like Us", "Kendrick Lamar", 30));
        hipHopRapTracks.add(new Track("Like That", "Future & Metro Boomin", 30));
        hipHopRapTracks.add(new Track("Rich Flex", "Drake & 21 Savage", 30));
        hipHopRapTracks.add(new Track("FE!N", "Travis Scott", 30));
        hipHopRapTracks.add(new Track("First Class", "Jack Harlow", 30));
        hipHopRapTracks.add(new Track("Industry Baby", "Lil Nas X", 30));
        hipHopRapTracks.add(new Track("WAP", "Cardi B", 30));
        hipHopRapTracks.add(new Track("Savage", "Megan Thee Stallion", 30));
        hipHopRapTracks.add(new Track("Toosie Slide", "Drake", 30));
        hipHopRapTracks.add(new Track("The Box", "Roddy Ricch", 30));
        hipHopRapTracks.add(new Track("Rockstar", "DaBaby", 30));
        hipHopRapTracks.add(new Track("Paint The Town Red", "Doja Cat", 30));
        hipHopRapTracks.add(new Track("fukumean", "Gunna", 30));
        hipHopRapTracks.add(new Track("Surround Sound", "JID", 30));
        hipHopRapTracks.add(new Track("Just Wanna Rock", "Lil Uzi Vert", 30));
        hipHopRapTracks.add(new Track("Super Gremlin", "Kodak Black", 30));
        hipHopRapTracks.add(new Track("Wait For U", "Future", 30));
        hipHopRapTracks.add(new Track("Jimmy Cooks", "Drake", 30));
        hipHopRapTracks.add(new Track("God's Plan", "Drake", 30));
        hipHopRapTracks.add(new Track("Highest in the Room", "Travis Scott", 30));
        
        // 20 SONS FR (LE PRIME : JUL, PNL, NISKA, NINHO)
        hipHopRapTracks.add(new Track("Tchikita", "Jul", 30));
        hipHopRapTracks.add(new Track("On m'appelle l'ovni", "Jul", 30));
        hipHopRapTracks.add(new Track("My World", "Jul", 30));
        hipHopRapTracks.add(new Track("JCVD", "Jul", 30));
        hipHopRapTracks.add(new Track("Wesh Alors", "Jul", 30));
        hipHopRapTracks.add(new Track("Au DD", "PNL", 30));
        hipHopRapTracks.add(new Track("91's", "PNL", 30));
        hipHopRapTracks.add(new Track("Onizuka", "PNL", 30));
        hipHopRapTracks.add(new Track("Da", "PNL", 30));
        hipHopRapTracks.add(new Track("Naha", "PNL", 30));
        hipHopRapTracks.add(new Track("Réseaux", "Niska", 30));
        hipHopRapTracks.add(new Track("Matuidi Charo", "Niska", 30));
        hipHopRapTracks.add(new Track("Commando", "Niska", 30));
        hipHopRapTracks.add(new Track("Médellin", "Niska", 30));
        hipHopRapTracks.add(new Track("Sapés comme jamais", "Niska", 30));
        hipHopRapTracks.add(new Track("Mamacita", "Ninho", 30));
        hipHopRapTracks.add(new Track("Lettre à une femme", "Ninho", 30));
        hipHopRapTracks.add(new Track("Tout en Gucci", "Ninho", 30));
        hipHopRapTracks.add(new Track("Jefe", "Ninho", 30));
        hipHopRapTracks.add(new Track("Maman ne le sait pas", "Ninho", 30));
        
        // 40 SONS FR (POST-2020 : LES HITS RÉCENTS)
        hipHopRapTracks.add(new Track("Triple V", "Werenoi", 30));
        hipHopRapTracks.add(new Track("Piano", "Werenoi", 30));
        hipHopRapTracks.add(new Track("Poney", "Werenoi", 30));
        hipHopRapTracks.add(new Track("Pyramide", "Werenoi", 30));
        hipHopRapTracks.add(new Track("Laboratoire", "Werenoi", 30));
        hipHopRapTracks.add(new Track("Chemin d'or", "Werenoi", 30));
        hipHopRapTracks.add(new Track("10.03.2023", "Werenoi", 30));
        hipHopRapTracks.add(new Track("11.04.2025", "Werenoi", 30));
        hipHopRapTracks.add(new Track("Laisse moi", "Keblack", 30));
        hipHopRapTracks.add(new Track("Mood", "Keblack", 30));
        hipHopRapTracks.add(new Track("Boucan", "Keblack x Franglish", 30));
        hipHopRapTracks.add(new Track("Position", "Franglish", 30));
        hipHopRapTracks.add(new Track("Génération Impolie", "Franglish", 30));
        hipHopRapTracks.add(new Track("Bolide Allemand", "SDM", 30));
        hipHopRapTracks.add(new Track("Passat", "SDM", 30));
        hipHopRapTracks.add(new Track("Mr. Ocho", "SDM", 30));
        hipHopRapTracks.add(new Track("Daddy", "SDM", 30));
        hipHopRapTracks.add(new Track("A LA VIE A LA MORT", "SDM", 30));
        hipHopRapTracks.add(new Track("Galère", "Maes", 30));
        hipHopRapTracks.add(new Track("Fetty Wap", "Maes", 30));
        hipHopRapTracks.add(new Track("Madrina", "Maes", 30));
        hipHopRapTracks.add(new Track("Spider", "Gims", 30));
        hipHopRapTracks.add(new Track("Sois pas timide", "Gims", 30));
        hipHopRapTracks.add(new Track("La Kiffance", "Naps", 30));
        hipHopRapTracks.add(new Track("Best Life", "Naps", 30));
        hipHopRapTracks.add(new Track("Daytona", "L2B", 30));
        hipHopRapTracks.add(new Track("Bitume", "L2B", 30));
        hipHopRapTracks.add(new Track("Ballon D'or", "Le Crime", 30));
        hipHopRapTracks.add(new Track("Oulala", "Le Crime", 30));
        hipHopRapTracks.add(new Track("Le P'tit", "Bello & Dallas", 30));
        hipHopRapTracks.add(new Track("Elle aime ça", "Bello & Dallas", 30));
        hipHopRapTracks.add(new Track("Casanova", "Soolking", 30));
        hipHopRapTracks.add(new Track("Suavemente", "Soolking", 30));
        hipHopRapTracks.add(new Track("Bande Organisée 2", "Jul", 30));
        hipHopRapTracks.add(new Track("Nostalgique", "Jul", 30));
        hipHopRapTracks.add(new Track("TP sur TP", "Jul", 30));
        hipHopRapTracks.add(new Track("Imagine", "Carbonne", 30));
        hipHopRapTracks.add(new Track("Wayeh", "Theodort", 30));
        hipHopRapTracks.add(new Track("Die", "Gazo", 30));
        hipHopRapTracks.add(new Track("COCO", "Niska x Ninho", 30));
        
        GENRE_TRACKS.put("hip-hop/rap", hipHopRapTracks);
        
        // === R&B ===
        List<Track> rnbTracks = new ArrayList<>();
        // Français 2000-2010
        rnbTracks.add(new Track("Petite Émilie", "Keen'V", 30));
        rnbTracks.add(new Track("J'aimerais trop", "Keen'V", 30));
        rnbTracks.add(new Track("Rien qu'une fois", "Keen'V", 30));
        rnbTracks.add(new Track("Ma vie au soleil", "Keen'V", 30));
        rnbTracks.add(new Track("Elle m'a aimé", "Kendji Girac", 30));
        rnbTracks.add(new Track("Andalouse", "Kendji Girac", 30));
        rnbTracks.add(new Track("Conmigo", "Kendji Girac", 30));
        rnbTracks.add(new Track("Color Gitano", "Kendji Girac", 30));
        rnbTracks.add(new Track("Tiago", "Kendji Girac", 30));
        rnbTracks.add(new Track("Bijou", "Keen'V", 30));
        rnbTracks.add(new Track("Dis-moi oui", "Collectif Métissé", 30));
        rnbTracks.add(new Track("Laissez passer", "Collectif Métissé", 30));
        rnbTracks.add(new Track("Zouk la sé sel médikaman nou ni", "Kassav", 30));
        rnbTracks.add(new Track("Syé Bwa", "Kassav", 30));
        rnbTracks.add(new Track("Sove lanmou", "Harry Diboula", 30));
        rnbTracks.add(new Track("Avec toi", "Axel Tony", 30));
        rnbTracks.add(new Track("Ma réalité", "Lynnsha", 30));
        rnbTracks.add(new Track("Hommes femmes", "Lynnsha", 30));
        rnbTracks.add(new Track("Si seulement", "Lynnsha", 30));
        rnbTracks.add(new Track("Comme avant", "Matt Pokora", 30));
        rnbTracks.add(new Track("Juste un instant", "M. Pokora", 30));
        rnbTracks.add(new Track("Belinda", "M. Pokora", 30));
        rnbTracks.add(new Track("Juste une photo de toi", "M. Pokora", 30));
        rnbTracks.add(new Track("Elle me contrôle", "M. Pokora", 30));
        rnbTracks.add(new Track("À nos actes manqués", "M. Pokora", 30));
        rnbTracks.add(new Track("On est là", "M. Pokora", 30));
        rnbTracks.add(new Track("Les planètes", "M. Pokora", 30));
        rnbTracks.add(new Track("Si tu pars", "M. Pokora", 30));
        rnbTracks.add(new Track("Dangerous", "M. Pokora", 30));
        rnbTracks.add(new Track("Tombé", "M. Pokora", 30));
        
        // Français 2020+
        rnbTracks.add(new Track("Bande organisée", "Jul", 30));
        rnbTracks.add(new Track("Merci", "Dadju", 30));
        rnbTracks.add(new Track("Reine", "Dadju", 30));
        rnbTracks.add(new Track("Jaloux", "Dadju", 30));
        rnbTracks.add(new Track("Va dire à ton ex", "Dadju", 30));
        rnbTracks.add(new Track("Compliqué", "Dadju", 30));
        rnbTracks.add(new Track("Donne-moi l'accord", "Slimane", 30));
        rnbTracks.add(new Track("Viens on s'aime", "Slimane", 30));
        rnbTracks.add(new Track("Paname", "Slimane", 30));
        rnbTracks.add(new Track("Luna", "Slimane", 30));
        rnbTracks.add(new Track("Adieu", "Slimane", 30));
        rnbTracks.add(new Track("Les amants de la colline", "Yannick Noah", 30));
        rnbTracks.add(new Track("Destination ailleurs", "Yannick Noah", 30));
        rnbTracks.add(new Track("Saga Africa", "Yannick Noah", 30));
        rnbTracks.add(new Track("On court", "Yannick Noah", 30));
        
        // Anglais 2000-2010
        rnbTracks.add(new Track("Yeah!", "Usher", 30));
        rnbTracks.add(new Track("U Got It Bad", "Usher", 30));
        rnbTracks.add(new Track("Burn", "Usher", 30));
        rnbTracks.add(new Track("OMG", "Usher", 30));
        rnbTracks.add(new Track("Love In This Club", "Usher", 30));
        rnbTracks.add(new Track("No Diggity", "Blackstreet", 30));
        rnbTracks.add(new Track("Pony", "Ginuwine", 30));
        rnbTracks.add(new Track("Say My Name", "Destiny's Child", 30));
        rnbTracks.add(new Track("No Scrubs", "TLC", 30));
        rnbTracks.add(new Track("Waterfalls", "TLC", 30));
        rnbTracks.add(new Track("Creep", "TLC", 30));
        rnbTracks.add(new Track("Crazy in Love", "Beyonce", 30));
        rnbTracks.add(new Track("Irreplaceable", "Beyonce", 30));
        rnbTracks.add(new Track("Single Ladies", "Beyonce", 30));
        rnbTracks.add(new Track("Halo", "Beyonce", 30));
        rnbTracks.add(new Track("If I Were A Boy", "Beyonce", 30));
        rnbTracks.add(new Track("Love On Top", "Beyonce", 30));
        rnbTracks.add(new Track("Drunk In Love", "Beyonce", 30));
        rnbTracks.add(new Track("Formation", "Beyonce", 30));
        rnbTracks.add(new Track("Run the World", "Beyonce", 30));
        
        // Anglais 2020+
        rnbTracks.add(new Track("Blinding Lights", "The Weeknd", 30));
        rnbTracks.add(new Track("Starboy", "The Weeknd", 30));
        rnbTracks.add(new Track("The Hills", "The Weeknd", 30));
        rnbTracks.add(new Track("Earned It", "The Weeknd", 30));
        rnbTracks.add(new Track("Can't Feel My Face", "The Weeknd", 30));
        rnbTracks.add(new Track("Save Your Tears", "The Weeknd", 30));
        rnbTracks.add(new Track("I Feel It Coming", "The Weeknd", 30));
        rnbTracks.add(new Track("Die For You", "The Weeknd", 30));
        rnbTracks.add(new Track("After Hours", "The Weeknd", 30));
        rnbTracks.add(new Track("Good Days", "SZA", 30));
        rnbTracks.add(new Track("Kill Bill", "SZA", 30));
        rnbTracks.add(new Track("The Weekend", "SZA", 30));
        rnbTracks.add(new Track("Love Galore", "SZA", 30));
        rnbTracks.add(new Track("All The Stars", "SZA", 30));
        rnbTracks.add(new Track("Snooze", "SZA", 30));
        rnbTracks.add(new Track("Nobody Gets Me", "SZA", 30));
        rnbTracks.add(new Track("Shirt", "SZA", 30));
        rnbTracks.add(new Track("Finesse", "Bruno Mars", 30));
        rnbTracks.add(new Track("24K Magic", "Bruno Mars", 30));
        rnbTracks.add(new Track("That's What I Like", "Bruno Mars", 30));
        rnbTracks.add(new Track("Versace on the Floor", "Bruno Mars", 30));
        rnbTracks.add(new Track("Treasure", "Bruno Mars", 30));
        rnbTracks.add(new Track("Locked Out of Heaven", "Bruno Mars", 30));
        rnbTracks.add(new Track("When I Was Your Man", "Bruno Mars", 30));
        rnbTracks.add(new Track("Just The Way You Are", "Bruno Mars", 30));
        rnbTracks.add(new Track("Grenade", "Bruno Mars", 30));
        rnbTracks.add(new Track("The Lazy Song", "Bruno Mars", 30));

                // === 50 HITS RAP US (2020+) ===
        hipHopRapTracks.add(new Track("Redrum", "21 Savage", 30));
        hipHopRapTracks.add(new Track("Doja", "Central Cee", 30));
        hipHopRapTracks.add(new Track("BAND4BAND", "Central Cee & Lil Baby", 30));
        hipHopRapTracks.add(new Track("Sprinter", "Central Cee & Dave", 30));
        hipHopRapTracks.add(new Track("We Don't Trust You", "Future & Metro Boomin", 30));
        hipHopRapTracks.add(new Track("Type Shit", "Future & Metro Boomin", 30));
        hipHopRapTracks.add(new Track("Dum, Dumb, And Dumber", "Lil Baby & Future & Young Thug", 30));
        hipHopRapTracks.add(new Track("Outfit", "Lil Baby & 21 Savage", 30));
        hipHopRapTracks.add(new Track("I Promise", "Lil Baby", 30));
        hipHopRapTracks.add(new Track("American Dream", "21 Savage", 30));
        hipHopRapTracks.add(new Track("Knife Talk", "Drake & 21 Savage", 30));
        hipHopRapTracks.add(new Track("Mr. Right Now", "21 Savage & Metro Boomin", 30));
        hipHopRapTracks.add(new Track("Snitches & Rats", "21 Savage & Metro Boomin", 30));
        hipHopRapTracks.add(new Track("euphoria", "Kendrick Lamar", 30));
        hipHopRapTracks.add(new Track("meet the grahams", "Kendrick Lamar", 30));
        hipHopRapTracks.add(new Track("6:16 in LA", "Kendrick Lamar", 30));
        hipHopRapTracks.add(new Track("tv off", "Kendrick Lamar", 30));
        hipHopRapTracks.add(new Track("Squabble Up", "Kendrick Lamar", 30));
        hipHopRapTracks.add(new Track("Sticky", "Tyler, The Creator", 30));
        hipHopRapTracks.add(new Track("Noid", "Tyler, The Creator", 30));
        hipHopRapTracks.add(new Track("St. Chroma", "Tyler, The Creator", 30));
        hipHopRapTracks.add(new Track("Thought I Was Dead", "Tyler, The Creator", 30));
        hipHopRapTracks.add(new Track("Get It Sexyy", "Sexyy Red", 30));
        hipHopRapTracks.add(new Track("SkeeYee", "Sexyy Red", 30));
        hipHopRapTracks.add(new Track("Pound Town", "Sexyy Red", 30));
        hipHopRapTracks.add(new Track("Never Lose Me", "Flo Milli", 30));
        hipHopRapTracks.add(new Track("In The Party", "Flo Milli", 30));
        hipHopRapTracks.add(new Track("Conceited", "Flo Milli", 30));
        hipHopRapTracks.add(new Track("BRB", "ANYCIA", 30));
        hipHopRapTracks.add(new Track("Back Outside", "ANYCIA & Latto", 30));
        hipHopRapTracks.add(new Track("Lottery", "Latto", 30));
        hipHopRapTracks.add(new Track("Sunday Service", "Latto", 30));
        hipHopRapTracks.add(new Track("Big Energy", "Latto", 30));
        hipHopRapTracks.add(new Track("Richtivities", "Saweetie", 30));
        hipHopRapTracks.add(new Track("Tap In", "Saweetie", 30));
        hipHopRapTracks.add(new Track("Best Friend", "Saweetie", 30));
        hipHopRapTracks.add(new Track("Up", "Cardi B", 30));
        hipHopRapTracks.add(new Track("Hot Shit", "Cardi B", 30));
        hipHopRapTracks.add(new Track("Bodak Yellow", "Cardi B", 30));
        hipHopRapTracks.add(new Track("Chill Bae", "Lil Uzi Vert", 30));
        hipHopRapTracks.add(new Track("Pink Tape", "Lil Uzi Vert", 30));
        hipHopRapTracks.add(new Track("Eternal Atake 2", "Lil Uzi Vert", 30));
        hipHopRapTracks.add(new Track("Backrooms", "Playboi Carti & Travis Scott", 30));
        hipHopRapTracks.add(new Track("Sky", "Playboi Carti", 30));
        hipHopRapTracks.add(new Track("Get In With Me", "BossMan Dlow", 30));
        hipHopRapTracks.add(new Track("Johnny Dang", "That Mexican OT", 30));
        hipHopRapTracks.add(new Track("Texas", "BigXthaPlug", 30));
        hipHopRapTracks.add(new Track("Mmhmm", "BigXthaPlug", 30));
        hipHopRapTracks.add(new Track("Blow for Blow", "Tee Grizzley & J. Cole", 30));

        // === 50 HITS RAP FR (2020+) - WERENOI, SDM, PLK, JUL, VEGEDREAM, KEBLACK ===

        // WERENOI (10 titres)
        hipHopRapTracks.add(new Track("Solitaire", "Werenoi", 30));
        hipHopRapTracks.add(new Track("Selfie", "Werenoi", 30));
        hipHopRapTracks.add(new Track("3 singes", "Werenoi", 30));
        hipHopRapTracks.add(new Track("Ciao", "Werenoi", 30));
        hipHopRapTracks.add(new Track("Pétunias", "Werenoi", 30));
        hipHopRapTracks.add(new Track("Combien tu m'aimes", "Werenoi", 30));
        hipHopRapTracks.add(new Track("Baby", "Werenoi", 30));
        hipHopRapTracks.add(new Track("Andale", "Werenoi", 30));
        hipHopRapTracks.add(new Track("Souvenir", "Werenoi", 30));
        hipHopRapTracks.add(new Track("Fonce", "Werenoi", 30));

        // SDM (10 titres)
        hipHopRapTracks.add(new Track("Pour Elle", "SDM", 30));
        hipHopRapTracks.add(new Track("Cartier Santos", "SDM", 30));
        hipHopRapTracks.add(new Track("Alvalm", "SDM", 30));
        hipHopRapTracks.add(new Track("Nocif", "Hamza & SDM", 30));
        hipHopRapTracks.add(new Track("Toka", "SDM", 30));
        hipHopRapTracks.add(new Track("Soleil Levant", "Orelsan & SDM", 30));
        hipHopRapTracks.add(new Track("Banlieusard", "SDM", 30));
        hipHopRapTracks.add(new Track("Cagoulé", "SDM", 30));
        hipHopRapTracks.add(new Track("Outro", "SDM", 30));
        hipHopRapTracks.add(new Track("Bleu", "SDM", 30));

        hipHopRapTracks.add(new Track("Bella", "Maitre Gims", 30));
        hipHopRapTracks.add(new Track("Sapés comme jamais", "Maitre Gims", 30));
        hipHopRapTracks.add(new Track("Est-ce que tu m'aimes", "Maitre Gims", 30));

        // KEBLACK (10 titres)
        hipHopRapTracks.add(new Track("Laisse moi", "Keblack", 30));
        hipHopRapTracks.add(new Track("Aucune attache", "Keblack", 30));
        hipHopRapTracks.add(new Track("Bababa", "Keblack", 30));
        hipHopRapTracks.add(new Track("Melrose Place", "Keblack", 30));
        hipHopRapTracks.add(new Track("Charisme", "Keblack & Soolking", 30));
        hipHopRapTracks.add(new Track("Ne m'en veux pas", "Keblack", 30));
        hipHopRapTracks.add(new Track("Menteuse", "Keblack", 30));
        hipHopRapTracks.add(new Track("Tchop", "Keblack", 30));
        hipHopRapTracks.add(new Track("Complètement sonné", "Keblack", 30));
        hipHopRapTracks.add(new Track("1 2 3 soleil", "Naza & Keblack", 30));

        // VEGEDREAM (5 titres)
        hipHopRapTracks.add(new Track("Merci les bleus", "Vegedream", 30));
        hipHopRapTracks.add(new Track("Madame Djé", "Vegedream", 30));
        hipHopRapTracks.add(new Track("Touché dans le coeur", "Vegedream", 30));
        hipHopRapTracks.add(new Track("Pour nous", "Vegedream", 30));
        hipHopRapTracks.add(new Track("Matata", "Vegedream & Kaaris", 30));

        hipHopRapTracks.add(new Track("Je tourne en rond", "Jul", 30));
        
        GENRE_TRACKS.put("r&b", rnbTracks);
        
        // === TOUT GENRE (Mix de tous les genres) ===
        List<Track> allGenresTracks = new ArrayList<>();
        // On va piocher dans tous les autres genres
        GENRE_TRACKS.put("tout genre", allGenresTracks); // Sera rempli dynamiquement
    }
    
    /**
     * Génère une playlist basée sur le genre et le nombre de morceaux demandé.
     * @param genre Le genre musical
     * @param numberOfTracks Le nombre de morceaux à inclure
     * @return Une playlist générée
     */
    public static Playlist generatePlaylist(String genre, int numberOfTracks) {
        String normalizedGenre = genre.toLowerCase().trim();
        
        List<Track> availableTracks;
        
        // Gestion spéciale pour "Tout Genre"
        if (normalizedGenre.equals("tout genre")) {
            availableTracks = generateMixedGenreTracks();
        } else {
            availableTracks = GENRE_TRACKS.get(normalizedGenre);
        }
        
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
     * Génère une liste de tracks mixant tous les genres.
     * Pour le genre "Tout Genre", on pioche dans tous les autres genres.
     */
    private static List<Track> generateMixedGenreTracks() {
        List<Track> mixedTracks = new ArrayList<>();
        Random random = new Random();
        
        // Liste des genres à piocher (exclure "tout genre" lui-même)
        List<String> genresToMix = new ArrayList<>();
        genresToMix.add("pop");
        genresToMix.add("rock");
        genresToMix.add("hip-hop/rap");
        genresToMix.add("r&b");
        
        // Piocher 25 morceaux de chaque genre pour avoir 100 au total
        for (String genreKey : genresToMix) {
            List<Track> genreTracks = GENRE_TRACKS.get(genreKey);
            if (genreTracks != null && !genreTracks.isEmpty()) {
                List<Track> shuffled = new ArrayList<>(genreTracks);
                Collections.shuffle(shuffled);
                
                // Ajouter 25 morceaux de ce genre
                int count = Math.min(25, shuffled.size());
                for (int i = 0; i < count; i++) {
                    mixedTracks.add(shuffled.get(i));
                }
            }
        }
        
        // Mélanger le tout
        Collections.shuffle(mixedTracks);
        
        return mixedTracks;
    }
    
    /**
     * Liste tous les genres disponibles.
     */
    public static List<String> getAvailableGenres() {
        List<String> genres = new ArrayList<>();
        genres.add("Tout Genre");
        genres.add("Pop");
        genres.add("Rock");
        genres.add("Hip-Hop/Rap");
        genres.add("R&B");
        return genres;
    }
}