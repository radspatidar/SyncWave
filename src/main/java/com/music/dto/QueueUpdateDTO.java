package com.music.dto;

import java.util.List;

import com.music.model.SongQueue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QueueUpdateDTO {
	
	private String type;
	private SongQueue song;
    private List<SongQueue> queue;
    
    public QueueUpdateDTO(String type, SongQueue song) {
        this.type = type;
        this.song = song;
    }

    public QueueUpdateDTO(String type, List<SongQueue> queue) {
        this.type = type;
        this.queue = queue;
    }
}
