package net.nagispace.productbackend.repository;

import net.nagispace.productbackend.entity.ProductEntity;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
}
