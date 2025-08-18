package net.nagispace.productbackend.repository;

import net.nagispace.productbackend.entity.CartEntity;
import net.nagispace.productbackend.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<CartEntity, Long> {
    Optional<CartEntity> findByUser(UserEntity user);
}
