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

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${aws.cognito.jwkUrl}")
    private String jwkUrl;

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)                                           // Disable CSRF
            .authorizeHttpRequests(auth -> auth                                              // authorization rules
                .requestMatchers("/auth/**").permitAll()                                   // Permit all requests to auth endpoints
                .requestMatchers("/admin/**").hasRole("ADMIN")                             // Secure admin endpoints
                .requestMatchers("/instructor/**").hasRole("INSTRUCTOR")                   // Secure instructor endpoints
                .requestMatchers("/student/instructor-application/all**").hasRole("ADMIN") // Only admin sees all instructor-applications
                .requestMatchers("/student/**").authenticated()                            // Secure student endpoints
                .requestMatchers("/user/all**").hasRole("ADMIN")                           // Only admin sees all users
                .requestMatchers("/user/**").authenticated()                               // Secure user endpoints
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

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(jwkUrl).build();
    }

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
