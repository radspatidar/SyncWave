package com.music.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SongQueue{
	@Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY
    )
    private Long id;

    private String audioUrl;

    private String title;

    private Integer position;

    @ManyToOne
    private Room room;
	
}