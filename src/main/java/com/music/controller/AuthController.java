package com.music.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.*;

import com.music.model.Role;
import com.music.model.User;

import com.music.repository.UserRepository;

import com.music.service.JwtUtil;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/signup")
    public String signup(@RequestBody User user) {

        // Encode password

        user.setPassword( passwordEncoder.encode(user.getPassword()));

        // Default role

        user.setRole(Role.USER);

        userRepository.save(user);

        return "User Registered";
    }

    @PostMapping("/login")
    public Map<String, String> login(
            @RequestBody User user
    ) {

        User dbUser = userRepository

                .findByEmail(user.getEmail())

                .orElseThrow(() ->

                        new RuntimeException(
                                "User not found"
                        )
                );

        if(

            !passwordEncoder.matches(

                user.getPassword(),

                dbUser.getPassword()
            )

        ) {

            throw new RuntimeException(
                    "Invalid password"
            );
        }

        String token =

                jwtUtil.generateToken(
                        user.getEmail()
                );

        Map<String, String> response =
                new HashMap<>();

        response.put(
                "token",
                token
        );

        response.put(
                "username",
                dbUser.getUsername()
        );

        return response;
    }
}