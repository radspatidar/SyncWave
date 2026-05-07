package com.music.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.music.model.User;
import com.music.repository.UserRepository;
import com.music.service.JwtUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {

	    @Autowired
	    private UserRepository userRepository;

	    @Autowired
	    private PasswordEncoder passwordEncoder;

	    @Autowired
	    private JwtUtil jwtUtil;

	    @PostMapping("/signup")
	    public String signup(@RequestBody User user) {
	        user.setPassword(passwordEncoder.encode(user.getPassword()));
	        userRepository.save(user);
	        return "User registered";
	    }

	    @PostMapping("/login")
	    public String login(@RequestBody User user) {

	        User dbUser = userRepository.findByEmail(user.getEmail())
	                .orElseThrow(() -> new RuntimeException("User not found"));

	        if (!passwordEncoder.matches(user.getPassword(), dbUser.getPassword())) {
	            throw new RuntimeException("Invalid password");
	        }

	        return jwtUtil.generateToken(user.getEmail());
	    }
}
