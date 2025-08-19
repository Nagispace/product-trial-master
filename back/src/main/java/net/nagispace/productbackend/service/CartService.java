package net.nagispace.productbackend.service;

import net.nagispace.productbackend.entity.CartEntity;
import net.nagispace.productbackend.entity.UserEntity;
import net.nagispace.productbackend.repository.CartRepository;
import org.openapitools.model.Cart;
import org.openapitools.model.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductService productService;
    private final UserService userService;

    public CartService(CartRepository cartRepository, ProductService productService, UserService userService) {
        this.cartRepository = cartRepository;
        this.productService = productService;
        this.userService = userService;
    }

    /**
     * Get or create a cart for a user and return it as DTO.
     */
    public Cart getCart(UserEntity user) {
        CartEntity entity = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    CartEntity cart = new CartEntity();
                    cart.setUser(user);
                    cart.setProducts(new ArrayList<>());
                    return cartRepository.save(cart);
                });

        return toCartDto(entity);
    }

    /**
     * Add a product to the user’s cart and return the updated cart as DTO.
     */
    public Cart addProduct(UserEntity user, Product productDto) {
        CartEntity entity = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    CartEntity cart = new CartEntity();
                    cart.setUser(user);
                    cart.setProducts(new ArrayList<>());
                    return cartRepository.save(cart);
                });

        entity.getProducts().add(productService.toEntity(productDto));

        CartEntity saved = cartRepository.save(entity);
        return toCartDto(saved);
    }



    private Cart toCartDto(CartEntity entity) {
        Cart dto = new Cart();
        dto.setId(entity.getId());
        dto.setUser(userService.toDto(entity.getUser()));
        dto.setProducts(
                entity.getProducts().stream()
                        .map(productService::toDto)
                        .collect(Collectors.toList())
        );
        return dto;
    }
}
