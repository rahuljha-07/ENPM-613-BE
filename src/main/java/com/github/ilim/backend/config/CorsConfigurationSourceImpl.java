package com.github.ilim.backend.config;

import io.micrometer.common.lang.NonNull;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

/**
 * Implementation of {@link CorsConfigurationSource} to define Cross-Origin Resource Sharing (CORS) configurations.
 * <p>
 * Configures allowed headers, origins, methods, credentials, and exposed headers for incoming HTTP requests.
 * </p>
 *
 * @author
 */
@Configuration
public class CorsConfigurationSourceImpl implements CorsConfigurationSource {

    /**
     * Provides the CORS configuration based on the incoming HTTP request.
     * <p>
     * Sets the allowed headers to "Authorization", "Cache-Control", and "Content-Type".
     * Allows origins matching "http://localhost:3000". Permits HTTP methods GET, POST, PUT, DELETE, OPTIONS, PATCH.
     * Enables credentials and exposes specific headers in the response.
     * </p>
     *
     * @param request the incoming HTTP request
     * @return the configured {@link CorsConfiguration} instance
     */
    @Override
    public CorsConfiguration getCorsConfiguration(@NonNull HttpServletRequest request) {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        corsConfiguration.setAllowedOriginPatterns(List.of("http://localhost:3000"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PUT", "OPTIONS", "PATCH", "DELETE"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setExposedHeaders(List.of("Authorization", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
        return corsConfiguration;
    }
}
