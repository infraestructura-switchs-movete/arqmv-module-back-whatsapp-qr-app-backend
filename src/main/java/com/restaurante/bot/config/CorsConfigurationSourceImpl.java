package com.restaurante.bot.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfigurationSourceImpl implements CorsConfigurationSource {
    @Override
    public CorsConfiguration getCorsConfiguration(@NonNull HttpServletRequest request) {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        corsConfiguration.setAllowedOriginPatterns(List.of("http://localhost:8080"));
        corsConfiguration.setAllowedOriginPatterns(List.of("http://localhost:4000"));
        corsConfiguration.setAllowedOriginPatterns(List.of("http://localhost:5175"));
        corsConfiguration.setAllowedOriginPatterns(List.of("http://localhost:5174"));
        corsConfiguration.setAllowedOriginPatterns(List.of("https://module-landing-page-qr-app-frontend-t0rn.onrender.com"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PUT", "OPTIONS", "PATCH", "DELETE"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setExposedHeaders(List.of("Authorization", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
        return corsConfiguration;
    }
}
