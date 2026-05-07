package com.music.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.music.model.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {

}
