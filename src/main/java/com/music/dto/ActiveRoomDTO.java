package com.music.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ActiveRoomDTO {

	private String roomCode;

    private Integer membersCount;

    private String currentSong;

    private double currentTime;

    private boolean playing;
}
