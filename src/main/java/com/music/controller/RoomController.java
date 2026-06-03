package com.music.controller;

import com.music.dto.RoomEvent;
import com.music.dto.RoomState;
import com.music.dto.UserDTO;
import com.music.model.*;
import com.music.repository.*;
import com.music.service.RoomStateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/room")
public class RoomController {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomMemberRepository roomMemberRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private RoomStateService roomStateService;

    @PostMapping("/create")
    public Room createRoom() {
    	
    	System.out.println("create room called");
    	
    	String email = getLoggedInEmail();
	
    	User user = userRepository.findByEmail(email)
    			.orElseThrow();
    	
    	Room room = new Room();
	
    	room.setRoomCode(generateRoomCode());
	
    	room.setCreatedBy(user.getId());
	
    	Room savedRoom = roomRepository.save(room);
	
    	RoomMember member = new RoomMember();
	
    	member.setRoomId(savedRoom.getId());
	
    	member.setUserId(user.getId());
	
    	member.setRole("AUTHOR");
	
    	roomMemberRepository.save(member);
	
    	return savedRoom;
    }
    
    @PostMapping("/join/{roomCode}")
    public String joinRoom(@PathVariable String roomCode) {
    	
	    System.out.println("join room called");
	
	    System.out.println(SecurityContextHolder.getContext().getAuthentication());
	    	
	    String email = getLoggedInEmail();
	        
	    System.out.println("ROOM CODE = " + roomCode);
	    
	    System.out.println("USER EXISTS = " +  userRepository.findByEmail(email));
	        
	    System.out.println( "ROOM EXISTS = " + roomRepository.findByRoomCode(roomCode) );
	       
	    User user = userRepository.findByEmail(email).orElseThrow();
	
	    Room room = roomRepository.findByRoomCode(roomCode).orElseThrow(() -> new RuntimeException("Room not found"));
	
	    boolean alreadyJoined = roomMemberRepository.existsByRoomIdAndUserId(room.getId(), user.getId());
	
	    if (alreadyJoined) {
	    	return "User already joined room";
	    }
	        
	    RoomMember member = new RoomMember();
	
	    member.setRoomId(room.getId());
	
	    member.setUserId(user.getId());
	
	    member.setRole("USER");
	
	    roomMemberRepository.save(member);
	        
	    List<UserDTO> users = roomMemberRepository.findUsersByRoomId(room.getId());
	        
	    messagingTemplate.convertAndSend("/topic/room/" + roomCode, new RoomEvent("MEMBERS_UPDATE",users));
	        
	    return "Joined room successfully";
    }
    
    @GetMapping("/members/{roomCode}")
    public List<UserDTO> getRoomMembers(@PathVariable String roomCode) {

        Room room = roomRepository.findByRoomCode(roomCode)
        		.orElseThrow(() ->new RuntimeException("Room not found"));

        return roomMemberRepository.findUsersByRoomId(room.getId() );
    }
    
    
    private String generateRoomCode() {
        return UUID.randomUUID()
                .toString()
                .substring(0, 6)
                .toUpperCase();
    }
    
    
    @PostMapping("/leave/{roomCode}")
    public String leaveRoom(@PathVariable String roomCode) {
    	
    	System.out.println(SecurityContextHolder.getContext().getAuthentication());
    	
    	String email = getLoggedInEmail();
        
        System.out.println("EMAIL = " + email);
        
        System.out.println("USER EXISTS = " +  userRepository.findByEmail(email));
        
        System.out.println( "ROOM EXISTS = " + roomRepository.findByRoomCode(roomCode) );
        
        User user = userRepository.findByEmail(email)
                .orElseThrow();

        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        roomMemberRepository.deleteByRoomIdAndUserId(
                room.getId(),
                user.getId()
        );

        List<UserDTO> users = roomMemberRepository.findUsersByRoomId(room.getId());

        messagingTemplate.convertAndSend("/topic/room/" + roomCode,new RoomEvent("MEMBERS_UPDATE", users));

        return "Left room successfully";
    }
    
    
    @GetMapping("/state/{roomCode}")
    public RoomState getState(@PathVariable String roomCode) {
    	
    	RoomState roomState = roomStateService.getState(roomCode);
    	
    	 if (roomState.isPlaying()) {

    	        double elapsed = (System.currentTimeMillis() - roomState.getLastUpdateTime()) / 1000.0;

    	        RoomState copy = new RoomState();

    	        copy.setAudioUrl(roomState.getAudioUrl());
    	        copy.setPlaying(true);
    	        copy.setCurrentPosition(roomState.getCurrentPosition());
    	        copy.setCurrentTime(roomState.getCurrentTime() + elapsed);

    	        return copy;
    	    }

        return roomState;
    }
    
    
    @GetMapping("/current-song/{roomCode}")
    public SongQueue getCurrentSong(@PathVariable String roomCode) {

        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow();

        return room.getCurrentSong();
    }
    
    
    @PostMapping("/repeat/{roomCode}")
    public boolean toggleRepeat(@PathVariable String roomCode) {

        Room room = roomRepository.findByRoomCode(roomCode).orElseThrow();

        room.setRepeatQueue(!room.isRepeatQueue());

        roomRepository.save(room);

        return room.isRepeatQueue();
    }
    
    @GetMapping("/repeat/{roomCode}")
    public boolean getRepeatStatus(@PathVariable String roomCode) {

        return roomRepository.findByRoomCode(roomCode).orElseThrow().isRepeatQueue();
    }
    
    private String getLoggedInEmail() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication instanceof OAuth2AuthenticationToken oauth) {

            return oauth.getPrincipal().getAttribute("email");
        }

        return authentication.getName();
    }
    
    
}
