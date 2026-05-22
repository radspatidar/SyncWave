package com.music.controller;

import com.cloudinary.Cloudinary;

import com.cloudinary.utils.ObjectUtils;
import com.music.model.Song;
import com.music.repository.SongRepository;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/songs")
@CrossOrigin("*")
public class SongController {

    private final Cloudinary cloudinary;

    private final SongRepository songRepository;

    public SongController(Cloudinary cloudinary,SongRepository songRepository) {

        this.cloudinary = cloudinary;

        this.songRepository = songRepository;
    }

    @PostMapping("/upload")
    public Song uploadSong(
    		@RequestParam("title") String title,
    		@RequestParam("audio") MultipartFile audio
    )     throws Exception {

    try {
        // Upload Audio

        File audioFile = File.createTempFile(
                "audio",
                audio.getOriginalFilename()
            );

        audio.transferTo(audioFile);

        Map audioUpload =  cloudinary.uploader().upload(audioFile,

                ObjectUtils.asMap(
                    "resource_type",
                    "video",

                    "folder",
                    "syncwave/audio"
                )
            );

        Song song = new Song();

        song.setTitle(title);

        song.setAudioUrl(audioUpload.get("secure_url").toString());

        return songRepository.save(song);
    }
    catch
    (Exception e) {

        e.printStackTrace();

        throw e;
    }
    }
    
    

    @GetMapping
    public List<Song> getAllSongs() {

        return songRepository.findAll();
    }

    @GetMapping("/search")
    public List<Song> searchSongs(@RequestParam String query ) {

        return songRepository.findByTitleContainingIgnoreCase(query);
    }
}