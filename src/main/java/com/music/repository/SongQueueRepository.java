package com.music.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.music.model.Room;
import com.music.model.SongQueue;

import java.util.List;
import java.util.Optional;

public interface SongQueueRepository extends JpaRepository<com.music.model.SongQueue, Long> {

    List<SongQueue> findByRoomOrderByPositionAsc(Room room);
    Optional<SongQueue> findByRoomAndPosition(Room room,Integer position);
    
    @Query("""
		       SELECT COALESCE(MAX(q.position),0)
		       FROM SongQueue q
		       WHERE q.room = :room
		       """)
		Integer getMaxPosition(Room room);
}