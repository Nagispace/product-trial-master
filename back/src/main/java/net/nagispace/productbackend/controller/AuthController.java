package net.nagispace.productbackend.controller;

import net.nagispace.productbackend.entity.UserEntity;
import net.nagispace.productbackend.security.JwtUtil;
import net.nagispace.productbackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil){
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/account")
    public ResponseEntity<UserEntity> createAccount(@RequestBody UserEntity user){
        UserEntity created = userService.createUser(user);
        return ResponseEntity.status(201).body(created);
    }

    @PostMapping("/token")
    public ResponseEntity<TokenResponse> login(@RequestBody UserEntity login) {
        // Basic input validation
        if (login.getEmail() == null || login.getEmail().trim().isEmpty() ||
                login.getPassword() == null || login.getPassword().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return userService.findByEmail(login.getEmail().trim())
                .filter(user -> userService.getPasswordEncoder().matches(login.getPassword(), user.getPassword()))
                .map(user -> ResponseEntity.ok(new TokenResponse(jwtUtil.generateToken(user.getEmail()))))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    record TokenResponse(String token){}
}
