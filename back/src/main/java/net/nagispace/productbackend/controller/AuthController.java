package net.nagispace.productbackend.controller;

import net.nagispace.productbackend.entity.UserEntity;
import net.nagispace.productbackend.security.JwtUtil;
import net.nagispace.productbackend.service.UserService;
import org.openapitools.api.AccountApi;
import org.openapitools.api.TokenApi;
import org.openapitools.model.JwtResponse;
import org.openapitools.model.LoginRequest;
import org.openapitools.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Optional;

@RestController
public class AuthController implements AccountApi, TokenApi {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * @return
     */
    @Override
    public Optional<NativeWebRequest> getRequest() {
        return AccountApi.super.getRequest();
    }

    // --- AccountApi: create user ---
    @Override
    public ResponseEntity<Void> accountPost(User userDto) {
        UserEntity entity = new UserEntity();
        entity.setEmail(userDto.getEmail());
        entity.setUsername(userDto.getUsername());
        entity.setPassword(userDto.getPassword());

        userService.createUser(userService.toDto(entity));
        return ResponseEntity.status(201).build();
    }

    // --- TokenApi: login ---
    @Override
    public ResponseEntity<JwtResponse> tokenPost(LoginRequest loginRequest) {
        if (loginRequest.getEmail() == null || loginRequest.getEmail().trim().isEmpty() ||
                loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return userService.findByEmail(loginRequest.getEmail().trim())
                .filter(user -> userService.getPasswordEncoder().matches(loginRequest.getPassword(), user.getPassword()))
                .map(user -> {
                    JwtResponse response = new JwtResponse();
                    response.setToken(jwtUtil.generateToken(user.getEmail()));
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.status(401).build());
    }
}
