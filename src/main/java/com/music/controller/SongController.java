package com.music.controller;

import com.music.model.Song;

import com.music.repository.SongRepository;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/songs")
@CrossOrigin("*")
public class SongController {


    private final SongRepository songRepository;

    @PostMapping
    public Song createSong(@RequestBody Song song) {
    	
        return songRepository.save(song);
    }
    
    @GetMapping
    public List<Song> getAllSongs() {

        return songRepository.findAll();
    }

    @GetMapping("/search")
    public List<Song> searchSongs(@RequestParam String keyword ) {

        return songRepository.findByTitleContainingIgnoreCase(keyword);
    }

    @DeleteMapping("/{id}")
    public String deleteSong(@PathVariable Long id) {

        songRepository.deleteById(id);

        return "Song Deleted";
    }
}