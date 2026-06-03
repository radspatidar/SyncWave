package com.music.model;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(
        strategy = GenerationType.IDENTITY
    )
    private Long id;
    private String username;
    private String email;
    private String password;

    @Enumerated(
        EnumType.STRING
    )
    private Role role;   
    
}