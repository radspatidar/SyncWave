package com.music.controller;

import com.music.model.*;
import com.music.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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

        member.setRole("ADMIN");

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

        return "Joined room successfully";
    }
    
    
    private String generateRoomCode() {
        return UUID.randomUUID()
                .toString()
                .substring(0, 6)
                .toUpperCase();
    }
}
