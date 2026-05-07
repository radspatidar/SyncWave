package com.music.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.music.model.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
	
	Optional<Room> findByRoomCode(String roomCode);

}
