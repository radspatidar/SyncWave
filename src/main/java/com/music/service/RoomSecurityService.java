package com.music.service;

import org.springframework.stereotype.Service;

import com.music.model.Room;
import com.music.repository.RoomMemberRepository;
import com.music.repository.RoomRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class RoomSecurityService {
	
	 private final RoomMemberRepository roomMemberRepository;
	 private final RoomRepository roomRepository;

	 public boolean isMember(String roomCode, Long userId) {

		    Room room = roomRepository.findByRoomCode(roomCode)
		            .orElseThrow();

		    return roomMemberRepository.existsByRoomIdAndUserId(room.getId(),userId);
	 }
}
