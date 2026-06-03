package com.music.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.music.dto.ActiveRoomDTO;
import com.music.dto.AdminStatsDTO;
import com.music.dto.RoomAdminDTO;
import com.music.dto.RoomState;
import com.music.dto.UserDTO;
import com.music.model.Room;
import com.music.model.Song;
import com.music.model.User;
import com.music.repository.RoomMemberRepository;
import com.music.repository.RoomRepository;
import com.music.repository.SongRepository;
import com.music.repository.UserRepository;
import com.music.service.RoomStateService;

@RestController
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoomRepository roomRepository;
	
	@Autowired
	private SongRepository songRepository;
	
	@Autowired
	private RoomMemberRepository roomMemberRepository;
	
	@Autowired
	private RoomStateService roomStateService;
	

	@GetMapping("/users")
	public List<User> getUsers() {

	    return userRepository.findAll();
	}
	
	@DeleteMapping("/users/{id}")
	public String deleteUser(@PathVariable Long id) {

	    userRepository.deleteById(id);

	    return "User Deleted";
	}
	
	@GetMapping("/rooms")
	public List<Room> getRooms() {

	    return roomRepository.findAll();
	}
	
	@DeleteMapping("/rooms/{id}")
	public String deleteRoom(@PathVariable Long id) {

	    roomRepository.deleteById(id);

	    return "Room Deleted";
	}
	
	@DeleteMapping("/songs/{id}")
	public String deleteSong(@PathVariable Long id) {

	    songRepository.deleteById(id);

	    return "Song Deleted";
	}
	
	@GetMapping("/songs")
	public List<Song> getAllSongs() {
		
	    return songRepository.findAll();
	}
	
	@GetMapping("/stats")
	public AdminStatsDTO getStats() {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		 System.out.println("AUTH = " + auth);
		 System.out.println("AUTHORITIES = " + auth.getAuthorities());

	    return new AdminStatsDTO(userRepository.count(),roomRepository.count(),songRepository.count());
	}
	
	@GetMapping("/rooms/details")
	public List<RoomAdminDTO> getRoomDetails() {

	    return roomRepository.findAll().stream().map( room -> {

	                Long count = roomMemberRepository.countMembersByRoomId(room.getId());

	                String currentSong = room.getCurrentSong() != null ? room.getCurrentSong().getTitle() : "No Song";

	                return new RoomAdminDTO(room.getId(),room.getRoomCode(),room.getCreatedBy(),count.intValue(),currentSong );
	            })
	            .toList();
	}
	
	@GetMapping("/rooms/{roomId}/members")
	public List<UserDTO> getRoomMembers( @PathVariable Long roomId) {

	    return roomMemberRepository.findUsersByRoomId(roomId);
	}
	
	@GetMapping("/active-rooms")
	public List<ActiveRoomDTO> getActiveRooms() {

	    return roomRepository.findAll().stream().map(room -> {

	                Long members = roomMemberRepository.countMembersByRoomId(room.getId());

	                RoomState state = roomStateService.getState(room.getRoomCode());

	                String currentSong = room.getCurrentSong() != null ? room.getCurrentSong().getTitle() : "No Song";

	                double currentTime = state != null ? state.getCurrentTime() : 0;

	                boolean playing = state != null && state.isPlaying();

	                return new ActiveRoomDTO(room.getRoomCode(),members.intValue(),currentSong,currentTime,playing);
	            })
	            .toList();
	}
}
