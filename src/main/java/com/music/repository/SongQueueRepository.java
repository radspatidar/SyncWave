package com.music.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.music.model.Room;
import com.music.model.SongQueue;

import java.util.List;

public interface SongQueueRepository extends JpaRepository<com.music.model.SongQueue, Long> {

    List<SongQueue> findByRoomOrderByPositionAsc(Room room);
}