package com.music.controller;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.music.dto.MusicEvent;
import com.music.dto.RoomState;
import com.music.model.Room;
import com.music.model.SongQueue;
import com.music.repository.RoomRepository;
import com.music.repository.SongQueueRepository;
import com.music.service.QueueService;
import com.music.service.RoomStateService;

@Controller
public class MusicSyncController {

	    @Autowired
	    private SimpMessagingTemplate messagingTemplate;
	    
	    @Autowired
	    private RoomStateService roomStateService;
	    
	    @Autowired
	    private QueueService queueService;
	    
	    @Autowired
	    private RoomRepository roomRepository;
	   
	    @Autowired
	    private SongQueueRepository songQueueRepository;
	    
	    private final Set<String> processingEnded = ConcurrentHashMap.newKeySet();

	    @MessageMapping("/music.sync")
	    public void syncMusic(com.music.dto.MusicEvent event) {
	       
	        System.out.println("ACTION = " + event.getAction() + " FROM = " + event.getSender());
	        
	        RoomState state = roomStateService.getState(event.getRoomCode());
	        
	        switch (event.getAction()) {

	        case "LOAD":
	        	Room room = roomRepository.findByRoomCode(event.getRoomCode()).orElseThrow();
	        	
	        	SongQueue currentSong =  songQueueRepository.findById(event.getSongId()).orElse(null);
	        	
	        	room.setCurrentSong(currentSong);

	            roomRepository.save(room);
	            
	            if(currentSong != null) {
	                System.out.println("CURRENT SONG SAVED = " + currentSong.getTitle());
	            }
	            
	            System.out.println("SONG ID RECEIVED = " + event.getSongId());
	            
	            state.setAudioUrl(event.getAudioUrl());
	            state.setCurrentTime(0);
	            state.setPlaying(true);
	            state.setCurrentPosition(event.getPosition());
	            
	            break;

	        case "PLAY":
	            state.setCurrentTime(event.getCurrentTime());
	            state.setPlaying(true);
	            state.setLastUpdateTime(System.currentTimeMillis());
	            break;

	        case "PAUSE":
	        	double elapsed =(System.currentTimeMillis() - state.getLastUpdateTime())/ 1000.0;
	            state.setCurrentTime(event.getCurrentTime() + elapsed);
	            state.setPlaying(false);
	            break;

	        case "SEEK":
	            state.setCurrentTime(event.getCurrentTime());
	            state.setPlaying(event.isPlaying());
	            if(event.isPlaying()) {
	                state.setLastUpdateTime(System.currentTimeMillis());
	            }
	            break;

	        case "STOP":
	            state.setCurrentTime(0);
	            state.setPlaying(false);
	            break;
	            
	        case "ENDED":
	        	
	            if(!processingEnded.add(event.getRoomCode())) {
	                return;
	            }
	            
	            try {
	            	 System.out.println("CURRENT POSITION = " + state.getCurrentPosition() );
		        	 
		        	 Room r = roomRepository.findByRoomCode(event.getRoomCode()).orElseThrow();	        	 
		        	 
		        	 System.out.println("CURRENT SONG = " + (r.getCurrentSong() != null ? r .getCurrentSong().getTitle() : "NULL"));
		        	 
		        	 SongQueue currentSongQueue = r.getCurrentSong();
		        	 
		        	 System.out.println("CURRENT SONG ID = " + currentSongQueue.getId());
		        	 
		        	 SongQueue nextSong = queueService.getNextSong( r, currentSongQueue.getId());
		        	 
		        	 System.out.println("NEXT SONG = " + (nextSong != null ? nextSong.getTitle() : "NULL"));
		        	 
		        	 if(nextSong != null) {
		        		 System.out.println("NEXT SONG = " + nextSong.getTitle());
		        	 }
		        	 
		        	 if (nextSong != null) {
		        		 
		        		 	r.setCurrentSong(nextSong);
		        		 	
		        		 	roomRepository.save(r);
		        		 
		        		    state.setCurrentPosition(nextSong.getPosition());
		        		   
		        		    state.setAudioUrl(nextSong.getAudioUrl());

		        		    state.setCurrentTime(0);

		        		    state.setPlaying(true);

		        	        MusicEvent loadEvent = new MusicEvent();

		        	        loadEvent.setAction("LOAD");

		        	        loadEvent.setRoomCode(event.getRoomCode());

		        	        loadEvent.setAudioUrl(nextSong.getAudioUrl());

		        	        loadEvent.setCurrentTime(0);

		        	        loadEvent.setPosition(nextSong.getPosition());
		        	        
		        	        loadEvent.setSongId(nextSong.getId());

		        	        messagingTemplate.convertAndSend("/topic/room/"+ event.getRoomCode(),loadEvent);
		        	 }
		        	 
		        	 return;
	            }
	            
	            finally {
	            	 processingEnded.remove(event.getRoomCode());
	            }
	        		
	    }

	        messagingTemplate.convertAndSend( "/topic/room/" + event.getRoomCode(),event );
	    }
}
