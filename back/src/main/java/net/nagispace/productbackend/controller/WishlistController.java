package net.nagispace.productbackend.controller;

import net.nagispace.productbackend.entity.UserEntity;
import net.nagispace.productbackend.service.AuthService;
import net.nagispace.productbackend.service.UserService;
import net.nagispace.productbackend.service.WishlistService;
import org.openapitools.api.WishlistApi;
import org.openapitools.model.Product;
import org.openapitools.model.Wishlist;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WishlistController implements WishlistApi {

    private final WishlistService wishlistService;
    private final AuthService authService;
    private final UserService userService;

    public WishlistController(WishlistService wishlistService, AuthService authService, UserService userService) {
        this.wishlistService = wishlistService;
        this.authService = authService;
        this.userService = userService;
    }

    @Override
    public ResponseEntity<Wishlist> wishlistGet(String authorization) {
        if (!authService.isAuthenticated(authorization)) {
            return ResponseEntity.status(403).build();
        }

        UserEntity user = userService.toEntity(authService.getCurrentUser(authorization));
        Wishlist wishlistDto = wishlistService.getWishlist(user);
        return ResponseEntity.ok(wishlistDto);
    }

    @Override
    public ResponseEntity<Wishlist> wishlistPost(String authorization, Product productDto) {
        if (!authService.isAuthenticated(authorization)) {
            return ResponseEntity.status(403).build();
        }

        UserEntity user = userService.toEntity(authService.getCurrentUser(authorization));
        Wishlist updatedWishlist = wishlistService.addProduct(user, productDto);
        return ResponseEntity.ok(updatedWishlist);
    }
}
