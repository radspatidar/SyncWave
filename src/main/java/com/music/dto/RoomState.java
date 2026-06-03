package com.music.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoomState {
	
	private Integer currentPosition;
	private String audioUrl;
    private double currentTime;
    private boolean playing;
    private long lastUpdateTime;
}
