package com.music.config;

import com.music.filter.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

            .csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth

                // Public APIs
            		
            		.requestMatchers(
            			    "/",
            			    "/index.html",
            			    "/pages/**",
            			    "/css/**",
            			    "/js/**",
            			    "/assets/**",
            			    "/favicon.ico"
            			).permitAll()
                .requestMatchers("/auth/**" ).permitAll()

                .requestMatchers("/ws/**").permitAll()

                .requestMatchers("/topic/**").permitAll()

                .requestMatchers("/app/**").permitAll()
                
                .requestMatchers( "/songs/upload" ).hasAuthority("ADMIN")


                .requestMatchers("/songs/**").permitAll()

                .requestMatchers( "/queue/**").permitAll()

                .requestMatchers( "/room/**").permitAll()

                // Admin only

                .requestMatchers( "/admin/**" ).hasAuthority("ADMIN")

                .requestMatchers("/users/**").permitAll()
                // Everything else

                .anyRequest().authenticated()
            )

            .addFilterBefore(
                jwtFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}