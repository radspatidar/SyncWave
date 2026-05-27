package com.music.controller;

import com.music.dto.RoomEvent;
import com.music.model.*;
import com.music.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
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

    @PostMapping("/create")
    public Room createRoom() {

        // get logged-in email from JWT
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // find user
        User user = userRepository.findByEmail(email)
                .orElseThrow();

        // create room
        Room room = new Room();

        room.setRoomCode(generateRoomCode());

        room.setCreatedBy(user.getId());

        Room savedRoom = roomRepository.save(room);

        // creator becomes ADMIN
        RoomMember member = new RoomMember();

        member.setRoomId(savedRoom.getId());

        member.setUserId(user.getId());

        member.setRole("AUTHOR");

        roomMemberRepository.save(member);

        return savedRoom;
    }
    
    @PostMapping("/join/{roomCode}")
    public String joinRoom(@PathVariable String roomCode) {

        // get logged-in user email
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // find user
        User user = userRepository.findByEmail(email)
                .orElseThrow();

        // find room
        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // prevent duplicate joining
        boolean alreadyJoined = roomMemberRepository
                .existsByRoomIdAndUserId(room.getId(), user.getId());

        if (alreadyJoined) {
            return "User already joined room";
        }

        // add user to room
        RoomMember member = new RoomMember();

        member.setRoomId(room.getId());

        member.setUserId(user.getId());

        member.setRole("USER");

        roomMemberRepository.save(member);
        
        List<User> users = roomMemberRepository.findByRoomId(room.getId())
                .stream()
                .map(m -> userRepository.findById(m.getUserId()).orElse(null))
                .toList();
        
        messagingTemplate.convertAndSend(
                "/topic/room/" + roomCode,
                new RoomEvent("MEMBERS_UPDATE",users)
        );
        return "Joined room successfully";
    }
    
    
    @GetMapping("/members/{roomCode}")
    public List<User> getRoomMembers(
            @PathVariable String roomCode
    ) {

        Room room = roomRepository
                .findByRoomCode(roomCode)
                .orElseThrow(() ->

                new RuntimeException(
                        "Room not found"
                ));

        List<RoomMember> members =
                roomMemberRepository.findByRoomId(
                        room.getId()
                );

        return members.stream()

                .map(member ->

                    userRepository.findById(
                        member.getUserId()
                    ).orElse(null)

                )

                .toList();
    }
    
    
    private String generateRoomCode() {
        return UUID.randomUUID()
                .toString()
                .substring(0, 6)
                .toUpperCase();
    }
    
    @PostMapping("/leave/{roomCode}")
    public String leaveRoom(@PathVariable String roomCode) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        roomMemberRepository.deleteByRoomIdAndUserId(
                room.getId(),
                user.getId()
        );

        List<User> users = roomMemberRepository.findByRoomId(room.getId())
                .stream()
                .map(m -> userRepository.findById(m.getUserId()).orElse(null))
                .toList();

        messagingTemplate.convertAndSend(
                "/topic/room/" + roomCode,
                new RoomEvent("MEMBERS_UPDATE", users)
          );

        return "Left room successfully";
    }
    
   
}
