package com.music.filter;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.music.model.User;
import com.music.repository.UserRepository;
import com.music.service.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

	 @Autowired
	 private JwtUtil jwtUtil;
	 
	 @Autowired
	 private UserRepository userRepository;

	    @Override
	    protected void doFilterInternal(HttpServletRequest request,
	                                    HttpServletResponse response,
	                                    FilterChain filterChain)
	            throws ServletException, IOException {

	        String header = request.getHeader("Authorization");

	        String token = null;
	        String email = null;

	        if (header != null && header.startsWith("Bearer ")) {
	            token = header.substring(7);
	            email = jwtUtil.extractEmail(token);
	        }

	        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
	            if (jwtUtil.isTokenValid(token, email)) {
	            	User user =userRepository.findByEmail(email).orElseThrow();

	            		List<SimpleGrantedAuthority>
	            		authorities = List.of(new SimpleGrantedAuthority( user.getRole().name()));

	            		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken( email,null,authorities );

	                SecurityContextHolder.getContext().setAuthentication(auth);
	            }
	        }

	        filterChain.doFilter(request, response);
	    }
}
