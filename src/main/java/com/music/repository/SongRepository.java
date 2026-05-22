package com.music.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.music.model.Song;

public interface SongRepository extends JpaRepository<Song, Long> {
	List<Song> findByTitleContainingIgnoreCase(String title);
}