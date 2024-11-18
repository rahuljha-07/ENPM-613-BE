package com.github.ilim.backend.config;

import com.github.ilim.backend.auth.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;

/**
 * Configuration class for Spring Security.
 * <p>
 * Defines security filter chains, JWT decoding, and authentication converters.
 * Configures CORS, CSRF, authorization rules, and OAuth2 resource server settings.
 * </p>
 *
 * @author
 */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${aws.cognito.jwkUrl}")
    private String jwkUrl;

    private final CustomUserDetailsService userDetailsService;
    private final CorsConfigurationSourceImpl corsConfigurationSource;

    /**
     * Configures the security filter chain for HTTP requests.
     * <p>
     * Sets up CORS configurations, disables CSRF protection, defines authorization rules for various endpoints,
     * configures OAuth2 resource server with JWT support, and integrates the custom user details service.
     * </p>
     *
     * @param http the {@link HttpSecurity} to modify
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs while configuring security
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(AbstractHttpConfigurer::disable)                                           // Disable CSRF
            .authorizeHttpRequests(auth -> auth                                              // authorization rules
                .requestMatchers("/auth/**").permitAll()                                   // Permit all requests to auth endpoints
                .requestMatchers("/admin/**").hasRole("ADMIN")                             // Secure admin endpoints
                .requestMatchers("/instructor/**").hasRole("INSTRUCTOR")                   // Secure instructor endpoints
                .requestMatchers("/student/instructor-application/all**").hasRole("ADMIN") // Only admin sees all instructor-applications
                .requestMatchers("/student/**").authenticated()                            // Secure student endpoints
                .requestMatchers("/user/all**").hasRole("ADMIN")                           // Only admin sees all users
                .requestMatchers("/user/**").authenticated()                               // Secure user endpoints
                .requestMatchers("/quiz/**").authenticated()                               // Secure quiz endpoints
                .requestMatchers("/module/**").authenticated()                             // Secure module endpoints
                .requestMatchers("/video/**").authenticated()                              // Secure video endpoints
                .requestMatchers("/support/**").authenticated()                            // Secure support endpoints
                .anyRequest().permitAll()                                                    // Permit all other requests
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .decoder(jwtDecoder())
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            )
            .userDetailsService(userDetailsService);
        return http.build();
    }

    /**
     * Configures the JWT decoder using the specified JWK Set URI.
     * <p>
     * Utilizes {@link NimbusJwtDecoder} to decode JWT tokens based on the JWK set provided by AWS Cognito.
     * </p>
     *
     * @return the configured {@link JwtDecoder} instance
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(jwkUrl).build();
    }

    /**
     * Configures the JWT authentication converter.
     * <p>
     * Converts JWT tokens into Spring Security authorities by loading user details and extracting their roles.
     * </p>
     *
     * @return the configured {@link JwtAuthenticationConverter} instance
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            String userId = jwt.getSubject();
            UserDetails userDetails = userDetailsService.loadUserByUsername(userId);
            return new ArrayList<>(userDetails.getAuthorities());
        });
        return converter;
    }
}
