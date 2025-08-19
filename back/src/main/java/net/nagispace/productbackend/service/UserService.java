package net.nagispace.productbackend.service;

import lombok.Getter;
import net.nagispace.productbackend.entity.UserEntity;
import net.nagispace.productbackend.repository.UserRepository;
import org.openapitools.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    @Getter
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    
    public User createUser(User userDto) {
        UserEntity entity = toEntity(userDto);
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        UserEntity saved = userRepository.save(entity);
        return toDto(saved);
    }

    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email).map(this::toDto);
    }



    public User toDto(UserEntity entity) {
        if (entity == null) return null;
        User dto = new User();
        dto.setId(entity.getId());
        dto.setEmail(entity.getEmail());
        dto.setPassword(entity.getPassword()); // en vrai ca faut pas le faire, mais je le fais pour le dev
        dto.setFirstname(entity.getFirstname());
        return dto;
    }

    public UserEntity toEntity(User dto) {
        if (dto == null) return null;
        UserEntity entity = new UserEntity();
        if (dto.getId() != null) entity.setId(dto.getId());
        entity.setEmail(dto.getEmail());
        entity.setPassword(dto.getPassword());
        entity.setFirstname(dto.getFirstname());
        return entity;
    }
}
