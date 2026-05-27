package com.music.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.music.model.Room;
import com.music.model.User;
import com.music.repository.RoomRepository;
import com.music.repository.SongRepository;
import com.music.repository.UserRepository;

@RestController
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoomRepository roomRepository;
	
	@Autowired
	private SongRepository songRepository;
	

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
	public String deleteSong(

	        @PathVariable Long id
	) {

	    songRepository.deleteById(id);

	    return "Song Deleted";
	}
}
