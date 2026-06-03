package com.music.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.music.model.Room;
import com.music.model.SongQueue;
import com.music.repository.RoomRepository;
import com.music.repository.SongQueueRepository;

@Service
public class QueueService {

	@Autowired
    private SongQueueRepository queueRepository;
    public SongQueue getNextSong(Room room, Long currentSongId)  {
     
        List<SongQueue> queue = queueRepository.findByRoomOrderByPositionAsc(room);
        
        for (int i = 0; i < queue.size(); i++) {
            if (queue.get(i).getId().equals(currentSongId)) {
                if (i + 1 < queue.size()) {
                    return queue.get(i + 1);
                }
                if(room.isRepeatQueue()) {
                    return queue.get(0);
                }
                return null;
            }
        }
        return null;
    }
}
