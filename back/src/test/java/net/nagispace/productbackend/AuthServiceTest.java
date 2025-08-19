package net.nagispace.productbackend;

import net.nagispace.productbackend.security.JwtUtil;
import net.nagispace.productbackend.service.AuthService;
import net.nagispace.productbackend.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openapitools.model.User;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("isAuthenticated returns false when header is null")
    void isAuthenticated_nullHeader_false() {
        assertFalse(authService.isAuthenticated(null));
    }

    @Test
    @DisplayName("isAuthenticated returns false when header does not start with Bearer")
    void isAuthenticated_badPrefix_false() {
        assertFalse(authService.isAuthenticated("Token abc"));
    }

    @Test
    @DisplayName("isAuthenticated returns true when token valid and user exists")
    void isAuthenticated_valid_true() {
        String header = "Bearer TOKEN";
        String email = "user@example.com";

        when(jwtUtil.extractEmail("TOKEN")).thenReturn(email);
        when(jwtUtil.isTokenValid("TOKEN", email)).thenReturn(true);
        when(userService.findByEmail(email)).thenReturn(Optional.of(new User().email(email)));

        assertTrue(authService.isAuthenticated(header));
    }

    @Test
    @DisplayName("isAuthenticated returns false when token invalid")
    void isAuthenticated_invalidToken_false() {
        String header = "Bearer TOKEN";
        String email = "user@example.com";

        when(jwtUtil.extractEmail("TOKEN")).thenReturn(email);
        when(jwtUtil.isTokenValid("TOKEN", email)).thenReturn(false);

        assertFalse(authService.isAuthenticated(header));
    }

    @Test
    @DisplayName("isAuthenticated returns false when user not found")
    void isAuthenticated_userNotFound_false() {
        String header = "Bearer TOKEN";
        String email = "user@example.com";

        when(jwtUtil.extractEmail("TOKEN")).thenReturn(email);
        when(jwtUtil.isTokenValid("TOKEN", email)).thenReturn(true);
        when(userService.findByEmail(email)).thenReturn(Optional.empty());

        assertFalse(authService.isAuthenticated(header));
    }

    @Test
    @DisplayName("getCurrentUser throws when header missing or invalid")
    void getCurrentUser_invalidHeader_throws() {
        assertThrows(RuntimeException.class, () -> authService.getCurrentUser(null));
        assertThrows(RuntimeException.class, () -> authService.getCurrentUser("Token abc"));
    }

    @Test
    @DisplayName("getCurrentUser returns user when token contains email and user exists")
    void getCurrentUser_valid_returnsUser() {
        String header = "Bearer TOKEN";
        String email = "user@example.com";
        User user = new User().email(email);

        when(jwtUtil.extractEmail("TOKEN")).thenReturn(email);
        when(userService.findByEmail(email)).thenReturn(Optional.of(user));

        User result = authService.getCurrentUser(header);
        assertEquals(email, result.getEmail());
    }

    @Test
    @DisplayName("isAdmin returns true only for admin@admin.com")
    void isAdmin_adminEmail_true() {
        String header = "Bearer TOKEN";
        when(jwtUtil.extractEmail("TOKEN")).thenReturn("admin@admin.com");
        when(userService.findByEmail("admin@admin.com")).thenReturn(Optional.of(new User().email("admin@admin.com")));

        assertTrue(authService.isAdmin(header));
    }

    @Test
    @DisplayName("isAdmin returns false for non-admin or when exception occurs")
    void isAdmin_nonAdmin_false() {
        String header = "Bearer TOKEN";
        when(jwtUtil.extractEmail("TOKEN")).thenReturn("user@example.com");
        when(userService.findByEmail("user@example.com")).thenReturn(Optional.of(new User().email("user@example.com")));

        assertFalse(authService.isAdmin(header));

        // When getCurrentUser throws (e.g., user not found)
        when(userService.findByEmail("user@example.com")).thenReturn(Optional.empty());
        assertFalse(authService.isAdmin(header));
    }
}