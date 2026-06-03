package com.music.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RoomAdminDTO {

	private Long id;

    private String roomCode;

    private Long createdBy;

    private Integer membersCount;

    private String currentSong;
}
