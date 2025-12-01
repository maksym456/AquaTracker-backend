package com.aquarium.aquarium;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // Wyłączamy CSRF dla API
                .authorizeHttpRequests()
                .anyRequest().permitAll(); // NA RAZIE POZWALAMY NA WSZYSTKO (do testów)

        return http.build();
    }
}