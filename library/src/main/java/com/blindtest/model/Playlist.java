package com.blindtest.model;
import com.blindtest.model.Track;
import java.util.List;
import java.util.ArrayList;

/**
 * Représente une playlist contenant une collection de morceaux de musique.
 */
public class Playlist {
    /**
     * Le nom de la playlist.
     */
    private String name;
    
    /**
     * La liste des morceaux de la playlist.
     */
    private List<Track> tracks;
    
    /**
     * Crée une nouvelle playlist avec un nom donné.
     * @param name Le nom de la playlist
     */
    public Playlist(String name) {
        this.name = name;
        this.tracks = new ArrayList<>();
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List<Track> getTracks() {
        return tracks;
    }
    
    /**
     * Ajoute un morceau à la playlist.
     * @param track Le morceau à ajouter
     */
    public void addTrack(Track track) {
        tracks.add(track);
    }
    
    /**
     * Retire un morceau de la playlist.
     * @param track Le morceau à retirer
     */
    public void removeTrack(Track track) {
        tracks.remove(track);
    }
}