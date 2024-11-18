package com.github.ilim.backend.auth;

import com.github.ilim.backend.entity.User;
import com.github.ilim.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Custom implementation of {@link UserDetailsService} for loading user-specific data.
 * <p>
 * Retrieves user information from the application's database and maps user roles to Spring Security authorities.
 * </p>
 *
 * @author
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    /**
     * Loads the user's data by their unique identifier.
     * <p>
     * Retrieves the {@link User} entity using the provided {@code userId}, maps the user's role to
     * a Spring Security authority, and returns a {@link UserDetails} object containing the user's
     * authentication information.
     * </p>
     *
     * @param userId the unique identifier of the user (typically email or username)
     * @return a {@link UserDetails} object containing the user's authentication information
     * @throws UsernameNotFoundException if no user is found with the provided {@code userId}
     */
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userService.findById(userId);
        // Map User.role to a Spring Security authority
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
        return new org.springframework.security.core.userdetails.User(user.getId(), "", authorities);
    }
}
