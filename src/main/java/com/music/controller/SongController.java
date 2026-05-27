package com.music.controller;

import com.cloudinary.Cloudinary;

import com.music.model.Song;

import com.music.repository.SongRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/songs")
@CrossOrigin("*")
public class SongController {

    private final Cloudinary cloudinary;

    private final SongRepository songRepository;

    public SongController(
            Cloudinary cloudinary,
            SongRepository songRepository
    ) {

        this.cloudinary = cloudinary;

        this.songRepository = songRepository;
    }

    // =====================================
    // SAVE SONG
    // =====================================

    @PostMapping
    public Song createSong(
            @RequestBody Song song
    ) {

        return songRepository.save(song);
    }

    // =====================================
    // GET ALL SONGS
    // =====================================

    @GetMapping
    public List<Song> getAllSongs() {

        return songRepository.findAll();
    }

    // =====================================
    // SEARCH SONGS
    // =====================================

    @GetMapping("/search")
    public List<Song> searchSongs(

            @RequestParam String keyword
    ) {

        return songRepository
                .findByTitleContainingIgnoreCase(
                        keyword
                );
    }

    // =====================================
    // DELETE SONG
    // =====================================

    @DeleteMapping("/{id}")
    public String deleteSong(
            @PathVariable Long id
    ) {

        songRepository.deleteById(id);

        return "Song Deleted";
    }
}