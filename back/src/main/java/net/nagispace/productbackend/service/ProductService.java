package net.nagispace.productbackend.service;

import net.nagispace.productbackend.entity.ProductEntity;
import net.nagispace.productbackend.repository.ProductRepository;
import org.openapitools.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public List<Product> getAllProducts() {
        return repository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Optional<Product> getProductById(Long id) {
        return repository.findById(id).map(this::toDto);
    }

    public Product createProduct(Product product) {
        ProductEntity entity = toEntity(product);
        ProductEntity saved = repository.save(entity);
        return toDto(saved);
    }

    public Optional<Product> updateProduct(Long id, Product product) {
        return repository.findById(id).map(existing -> {
            ProductEntity entity = toEntity(product);
            entity.setId(id); // keep the same ID
            ProductEntity saved = repository.save(entity);
            return toDto(saved);
        });
    }

    public void deleteProduct(Long id) {
        repository.deleteById(id);
    }

    // Mapping
    public Product toDto(ProductEntity entity) {
        Product dto = new Product();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setImage(entity.getImage());
        dto.setCategory(entity.getCategory());
        dto.setPrice(entity.getPrice());
        dto.setQuantity(entity.getQuantity());
        dto.setInternalReference(entity.getInternalReference());
        dto.setShellId(entity.getShellId());
        dto.setInventoryStatus(Product.InventoryStatusEnum.valueOf(entity.getInventoryStatus().name()));
        dto.setRating(entity.getRating());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    public ProductEntity toEntity(Product dto) {
        ProductEntity entity = new ProductEntity();
        if (dto.getId() != null) entity.setId(dto.getId().longValue());
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setImage(dto.getImage());
        entity.setCategory(dto.getCategory());
        entity.setPrice(dto.getPrice());
        entity.setQuantity(dto.getQuantity());
        entity.setInternalReference(dto.getInternalReference());
        entity.setShellId(dto.getShellId());
        if (dto.getInventoryStatus() != null) {
            entity.setInventoryStatus(ProductEntity.InventoryStatus.valueOf(dto.getInventoryStatus().name()));
        }
        entity.setRating(dto.getRating());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        return entity;
    }
}
