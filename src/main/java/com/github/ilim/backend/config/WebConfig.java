package com.github.ilim.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration class for setting up MVC-related settings.
 * <p>
 * Configures global CORS mappings to allow cross-origin requests from specified origins and HTTP methods.
 * </p>
 *
 * @author
 */
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    /**
     * Adds CORS mappings to the registry.
     * <p>
     * Allows cross-origin requests to all endpoints ("/**") from the origin "http://localhost:3000".
     * Permits HTTP methods GET, POST, PUT, DELETE, and OPTIONS.
     * </p>
     *
     * @param registry the {@link CorsRegistry} to add mappings to
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOriginPatterns("http://localhost:3000")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }
}
