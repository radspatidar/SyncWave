package com.music.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AdminStatsDTO {
	
	private long totalUsers;
    private long totalRooms;
    private long totalSongs;
}
