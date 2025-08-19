package net.nagispace.productbackend.service;

import net.nagispace.productbackend.entity.WishlistEntity;
import net.nagispace.productbackend.entity.UserEntity;
import net.nagispace.productbackend.repository.WishlistRepository;
import org.openapitools.model.Wishlist;
import org.openapitools.model.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductService productService;
    private final UserService userService;

    public WishlistService(WishlistRepository wishlistRepository, ProductService productService, UserService userService) {
        this.wishlistRepository = wishlistRepository;
        this.productService = productService;
        this.userService = userService;
    }

    /**
     * Récupère ou crée une wishlist pour l’utilisateur et la renvoie en DTO.
     */
    public Wishlist getWishlist(UserEntity user) {
        WishlistEntity entity = wishlistRepository.findByUser(user)
                .orElseGet(() -> {
                    WishlistEntity wishlist = new WishlistEntity();
                    wishlist.setUser(user);
                    wishlist.setProducts(new ArrayList<>());
                    return wishlistRepository.save(wishlist);
                });

        return toWishlistDto(entity);
    }

    /**
     * Ajoute un produit dans la wishlist et renvoie la wishlist mise à jour en DTO.
     */
    public Wishlist addProduct(UserEntity user, Product productDto) {
        WishlistEntity entity = wishlistRepository.findByUser(user)
                .orElseGet(() -> {
                    WishlistEntity wishlist = new WishlistEntity();
                    wishlist.setUser(user);
                    wishlist.setProducts(new ArrayList<>());
                    return wishlistRepository.save(wishlist);
                });

        entity.getProducts().add(productService.toEntity(productDto));

        WishlistEntity saved = wishlistRepository.save(entity);
        return toWishlistDto(saved);
    }

    //mapping

    private Wishlist toWishlistDto(WishlistEntity entity) {
        Wishlist dto = new Wishlist();
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
