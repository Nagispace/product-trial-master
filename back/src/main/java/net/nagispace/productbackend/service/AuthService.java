package net.nagispace.productbackend.service;

import net.nagispace.productbackend.entity.UserEntity;
import net.nagispace.productbackend.security.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;
    private final JwtUtil jwtService;

    public AuthService(UserService userService, JwtUtil jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    public UserEntity getCurrentUser(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token manquant ou invalide");
        }
        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    public boolean isAdmin(String authHeader) {
        try {
            UserEntity user = getCurrentUser(authHeader);
            return "admin@admin.com".equals(user.getEmail());
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAuthenticated(String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return false;
            }

            String token = authHeader.substring(7);
            String email = jwtService.extractEmail(token);

            return jwtService.isTokenValid(token, email) &&
                    userService.findByEmail(email).isPresent();
        } catch (Exception e) {
            return false;
        }
    }
}

