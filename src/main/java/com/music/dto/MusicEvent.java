package com.music.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MusicEvent {

	 private String roomCode;

	 private String action;

	 private double currentTime;

	 private String audioUrl;
	    
	 private boolean playing;
	    
	 private String sender;

	 private Integer position;
	    
	 private Long songId;
}

