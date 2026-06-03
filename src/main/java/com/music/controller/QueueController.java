package com.music.controller;

import com.music.dto.MusicEvent;
import com.music.dto.QueueUpdateDTO;
import com.music.model.Room;
import com.music.model.Song;
import com.music.model.SongQueue;
import com.music.model.User;
import com.music.repository.RoomRepository;

import com.music.repository.SongQueueRepository;
import com.music.repository.SongRepository;
import com.music.repository.UserRepository;
import com.music.service.QueueService;
import com.music.service.RoomSecurityService;

import lombok.AllArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/queue")
@CrossOrigin("*")
public class QueueController {
	
	@Autowired
    private final SongQueueRepository queueRepository;
    
	@Autowired
    private final RoomRepository roomRepository;
    
	@Autowired
    private final SongRepository songRepository;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    private final UserRepository userRepository;
    
    private final RoomSecurityService roomSecurityService;
    
    private final QueueService queueService;

    @PostMapping("/add/{roomCode}/{songId}")
    public  List<SongQueue> addSongToQueue(@PathVariable String roomCode, @PathVariable Long songId) {
    	
    	validateRoomMember(roomCode);
    	
        Room room = roomRepository.findByRoomCode(roomCode).orElseThrow();

        Song song = songRepository.findById(songId).orElseThrow();
        
        List<SongQueue> queue = queueRepository.findByRoomOrderByPositionAsc(room);
        
        int nextPosition = queue.size();

        SongQueue queuesong = new SongQueue();

        queuesong.setAudioUrl(song.getAudioUrl());

        queuesong.setTitle(song.getTitle());

        queuesong.setPosition(nextPosition);

        queuesong.setRoom(room);

        queueRepository.save(queuesong);
        
        queue.add(queuesong);
        
        messagingTemplate.convertAndSend( "/topic/room/" + roomCode, new QueueUpdateDTO( "QUEUE_UPDATE", queue ));

        return queue;
    }

    
    @GetMapping("/{roomCode}")
    public List<SongQueue> getQueue(@PathVariable String roomCode) {

        Room room = roomRepository.findByRoomCode(roomCode).orElseThrow();

        return queueRepository.findByRoomOrderByPositionAsc(room);
    }
    
    
    @PostMapping("/remove/{roomCode}/{songId}")
    public void removeSong(@PathVariable String roomCode, @PathVariable Long songId ) {
    	
    	validateRoomMember(roomCode);

        Room room = roomRepository.findByRoomCode(roomCode).orElseThrow();
        
        if(room.getCurrentSong() != null && room.getCurrentSong().getId().equals(songId)) {

        		    SongQueue next = queueService.getNextSong(room, songId);

        		    room.setCurrentSong(next);

        		    roomRepository.save(room);

        		    MusicEvent loadEvent = new MusicEvent();

        		    loadEvent.setAction("LOAD");
        		    loadEvent.setRoomCode(roomCode);
        		    loadEvent.setAudioUrl(next.getAudioUrl());
        		    loadEvent.setCurrentTime(0);
        		    loadEvent.setPosition(next.getPosition());
        		    loadEvent.setSongId(next.getId());

        		    messagingTemplate.convertAndSend("/topic/room/" + roomCode, loadEvent );
        }

        List<SongQueue> queue = queueRepository.findByRoomOrderByPositionAsc(room);

        queue.removeIf(q -> q.getId().equals(songId));

        reindex(queue);

        queueRepository.deleteById(songId);

        broadcastQueue(roomCode, queue);
    }
    
    @PostMapping("/up/{roomCode}/{songId}")
    public void moveUp(@PathVariable String roomCode, @PathVariable Long songId) {
    	System.out.println("MOVE UP CALLED");
    	
    	validateRoomMember(roomCode);

        Room room = roomRepository.findByRoomCode(roomCode).orElseThrow();
        
        System.out.println("ROOM = " + room.getRoomCode());

        List<SongQueue> queue = queueRepository.findByRoomOrderByPositionAsc(room);
        
        System.out.println("QUEUE SIZE = " + queue.size());

        for (int i = 1; i < queue.size(); i++) {
            if (queue.get(i).getId().equals(songId)) {
                swap(queue, i, i - 1);
                break;
            }
        }

        broadcastQueue(roomCode, queue);
    }
    
    @PostMapping("/down/{roomCode}/{songId}")
    public void moveDown(@PathVariable String roomCode, @PathVariable Long songId) {
    	
    	validateRoomMember(roomCode);

        Room room = roomRepository.findByRoomCode(roomCode).orElseThrow();

        List<SongQueue> queue = queueRepository.findByRoomOrderByPositionAsc(room);

        for (int i = 0; i < queue.size() - 1; i++) {
            if (queue.get(i).getId().equals(songId)) {
                swap(queue, i, i + 1);
                break;
            }
        }

        broadcastQueue(roomCode, queue);
    }
    
    @PostMapping("/next/{roomCode}")
    public void playNext(@PathVariable String roomCode) {

        Room room = roomRepository.findByRoomCode(roomCode).orElseThrow();

        List<SongQueue> queue = queueRepository.findByRoomOrderByPositionAsc(room);

        if (queue.isEmpty()) return;

        SongQueue finished = queue.remove(0);
        
        queueRepository.deleteById(finished.getId());

        reindex(queue);

        messagingTemplate.convertAndSend("/topic/room/" + roomCode, new QueueUpdateDTO("QUEUE_UPDATE", queue));

        if (!queue.isEmpty()) {
            SongQueue next = queue.get(0);

            messagingTemplate.convertAndSend("/topic/room/" + roomCode, new QueueUpdateDTO("AUTO_PLAY", next));
        }
    }
    
    private void swap(List<SongQueue> queue, int i, int j) {

        SongQueue first = queue.get(i);
        SongQueue second = queue.get(j);

        Integer tempPosition = first.getPosition();

        first.setPosition(second.getPosition());
        second.setPosition(tempPosition);

        queueRepository.save(first);
        queueRepository.save(second);

        queue.set(i, second);
        queue.set(j, first);
    }
    
    private void reindex(List<SongQueue> queue) {
        for (int i = 0; i < queue.size(); i++) {
            queue.get(i).setPosition(i);
        }
        
        queueRepository.saveAll(queue);
    }
    
    private void broadcastQueue(String roomCode, List<SongQueue> queue) {
        messagingTemplate.convertAndSend("/topic/room/" + roomCode,new QueueUpdateDTO("QUEUE_UPDATE", queue) );
    }
    
    private void validateRoomMember(String roomCode) {

    	Authentication auth =
    	        SecurityContextHolder
    	                .getContext()
    	                .getAuthentication();
    	
    	String email = null;
    	
    	if (auth.getPrincipal() instanceof org.springframework.security.oauth2.core.user.OAuth2User oauthUser) {

    	    email = oauthUser.getAttribute("email");

    	} else {

    	    email = auth.getName();
    	}
    	
        System.out.println("EMAIL = " + email);
        
        User user = userRepository.findByEmail(email).orElseThrow();
        
        System.out.println("USER = " + user);
        
        if(user == null){
            throw new RuntimeException("User not found");
        }


        boolean isMember = roomSecurityService.isMember(roomCode, user.getId());
        
        System.out.println("IS MEMBER = " + isMember);

        if (!isMember) {
            throw new RuntimeException("You are not room Member");
        }
    }
    
    @PostMapping("/current/{roomCode}/{songId}")
    public void updateCurrentSong(@PathVariable String roomCode, @PathVariable Long songId) {

        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow();

        SongQueue song = queueRepository.findById(songId)
                        .orElseThrow();

        room.setCurrentSong(song);

        roomRepository.save(room);
    }
}
