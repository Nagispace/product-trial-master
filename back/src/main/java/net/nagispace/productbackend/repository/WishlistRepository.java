package net.nagispace.productbackend.repository;

import net.nagispace.productbackend.entity.CartEntity;
import net.nagispace.productbackend.entity.UserEntity;
import net.nagispace.productbackend.entity.WishlistEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishlistRepository extends JpaRepository<WishlistEntity, Long> {
    Optional<WishlistEntity> findByUser(UserEntity user);
}