package com.music.controller;

import com.music.dto.AddSongRequest;
import com.music.model.Room;
import com.music.model.SongQueue;
import com.music.repository.RoomRepository;

import com.music.repository.SongQueueRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/queue")
@CrossOrigin("*")
public class QueueController {

    private final SongQueueRepository queueRepository;

    private final RoomRepository roomRepository;

    public QueueController(SongQueueRepository queueRepository,RoomRepository roomRepository) {

        this.queueRepository = queueRepository;

        this.roomRepository = roomRepository;
    }

    @PostMapping("/add")
    public SongQueue addSong(@RequestBody AddSongRequest request) {

        Room room = roomRepository.findByRoomCode(request.getRoomCode()).orElseThrow();

        int nextPosition = queueRepository.findByRoomOrderByPositionAsc(room).size() + 1;

        SongQueue song = new SongQueue();

        song.setAudioUrl(request.getAudioUrl());

        song.setTitle(request.getTitle());

        song.setPosition(nextPosition);

        song.setRoom(room);

        return queueRepository.save(song);
    }

    @GetMapping("/{roomCode}")
    public List<SongQueue> getQueue(@PathVariable String roomCode) {

        Room room = roomRepository.findByRoomCode(roomCode).orElseThrow();

        return queueRepository.findByRoomOrderByPositionAsc(room);
    }
}