package com.music.controller;

import com.music.dto.AddSongRequest;
import com.music.model.Room;
import com.music.model.Song;
import com.music.model.SongQueue;
import com.music.repository.RoomRepository;

import com.music.repository.SongQueueRepository;
import com.music.repository.SongRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/queue")
@CrossOrigin("*")
public class QueueController {

    private final SongQueueRepository queueRepository;

    private final RoomRepository roomRepository;
    
    private final SongRepository songRepository;

    public QueueController(SongQueueRepository queueRepository, SongRepository songRepository, RoomRepository roomRepository) {

        this.queueRepository = queueRepository;
        
        this.songRepository = songRepository;

        this.roomRepository = roomRepository;
    }

    @PostMapping("/add/{roomCode}/{songId}")
    public SongQueue addSongToQueue(@PathVariable String roomCode, @PathVariable Long songId) {

        Room room = roomRepository.findByRoomCode(roomCode).orElseThrow();

        Song song = songRepository.findById(songId).orElseThrow();
        
        int nextPosition = queueRepository.findByRoomOrderByPositionAsc(room).size() + 1;

        SongQueue queuesong = new SongQueue();

        queuesong.setAudioUrl(song.getAudioUrl());

        queuesong.setTitle(song.getTitle());

        queuesong.setPosition(nextPosition);

        queuesong.setRoom(room);

        return queueRepository.save(queuesong);
    }

    @GetMapping("/{roomCode}")
    public List<SongQueue> getQueue(@PathVariable String roomCode) {

        Room room = roomRepository.findByRoomCode(roomCode).orElseThrow();

        return queueRepository.findByRoomOrderByPositionAsc(room);
    }
}
