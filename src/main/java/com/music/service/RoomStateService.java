package com.music.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.music.dto.RoomState;

@Service
public class RoomStateService {
	
	 private final Map<String, RoomState> roomStates = new ConcurrentHashMap<>();
	 
	 public RoomState getState(String roomCode) {
	        return roomStates.computeIfAbsent( roomCode, code -> new RoomState() );
	 }
	 
	 public void updateState(String roomCode, String audioUrl, double currentTime, boolean playing, Integer position) {
	        RoomState state = getState(roomCode);
	        state.setAudioUrl(audioUrl);
	        state.setCurrentTime(currentTime);
	        state.setPlaying(playing);
	        state.setCurrentPosition(position);
	 }
}
