package com.music.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.music.model.MusicEvent;

@Controller
public class MusicSyncController {

	    @Autowired
	    private SimpMessagingTemplate messagingTemplate;

	    @MessageMapping("/music.sync")
	    public void syncMusic(MusicEvent event) {

	        System.out.println(event.getAction());

	        messagingTemplate.convertAndSend(
	                "/topic/room/" + event.getRoomCode(),
	                event
	        );
	    }
}
