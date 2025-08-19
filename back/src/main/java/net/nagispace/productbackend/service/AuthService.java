package net.nagispace.productbackend.service;

import net.nagispace.productbackend.entity.UserEntity;
import net.nagispace.productbackend.security.JwtUtil;
import org.openapitools.model.User;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;
    private final JwtUtil jwtService;

    public AuthService(UserService userService, JwtUtil jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    /**
     * Get the current authenticated user from the Authorization header.
     */
    public User getCurrentUser(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token manquant ou invalide");
        }
        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);

        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    /**
     * Checks if the current user is an admin.
     * For now, only "admin@admin.com" is considered admin.
     */
    public boolean isAdmin(String authHeader) {
        try {
            User user = getCurrentUser(authHeader);
            return "admin@admin.com".equals(user.getEmail());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validates if the token is present, valid and linked to a known user.
     */
    public boolean isAuthenticated(String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return false;
            }

            String token = authHeader.substring(7);
            String email = jwtService.extractEmail(token);

            return jwtService.isTokenValid(token, email)
                    && userService.findByEmail(email).isPresent();
        } catch (Exception e) {
            return false;
        }
    }
}
