package com.music.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.music.model.ChatMessage;

@Controller
public class ChatController {

	    @Autowired
	    private SimpMessagingTemplate messagingTemplate;

	    @MessageMapping("/chat")
	    public void sendMessage(ChatMessage message) {

	        System.out.println(message.getRoomCode());

	        messagingTemplate.convertAndSend(
	                "/topic/room/" + message.getRoomCode(),
	                message
	        );
	    }
}
