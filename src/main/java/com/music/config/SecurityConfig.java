package com.music.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.music.security.JwtFilter;
import com.music.security.OAuthSuccessHandler;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;
    
    @Autowired
    private OAuthSuccessHandler oAuthSuccessHandler;
    
    @Bean
    public SecurityFilterChain securityFilterChain( HttpSecurity http ) throws Exception {

        http

            .csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth
                .requestMatchers("/","/index.html","/pages/**","/css/**","/js/**").permitAll()
                
                .requestMatchers("/auth/**" ).permitAll()
                
                .requestMatchers("/ws/**").permitAll()

                .requestMatchers("/topic/**").permitAll()

                .requestMatchers("/app/**").permitAll()
                
                .requestMatchers("/oauth2/**","/login/**" ).permitAll()
                
                .requestMatchers("/songs/**").permitAll()  
                
                .requestMatchers("/users/**").permitAll()
                
                .requestMatchers( "/songs/upload" ).hasAuthority("ADMIN") 
                
                .requestMatchers( "/admin/**" ).hasAuthority("ADMIN")
                
                .requestMatchers( "/queue/**").authenticated()
                
                .requestMatchers( "/room/**").authenticated()
                
                .anyRequest().authenticated()
            )
            
            .oauth2Login(oauth -> oauth.successHandler(oAuthSuccessHandler))
            
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}