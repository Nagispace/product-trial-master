package net.nagispace.productbackend.controller;

import net.nagispace.productbackend.entity.UserEntity;
import net.nagispace.productbackend.service.AuthService;
import net.nagispace.productbackend.service.CartService;
import net.nagispace.productbackend.service.UserService;
import org.openapitools.api.CartApi;
import org.openapitools.model.Cart;
import org.openapitools.model.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CartController implements CartApi {

    private final CartService cartService;
    private final AuthService authService;
    private final UserService userService;

    public CartController(CartService cartService, AuthService authService, UserService userService) {
        this.cartService = cartService;
        this.authService = authService;
        this.userService = userService;
    }

    @Override
    public ResponseEntity<Cart> cartGet(String authorization) {
        if (!authService.isAuthenticated(authorization)) {
            return ResponseEntity.status(403).build();
        }

        UserEntity user = userService.toEntity(authService.getCurrentUser(authorization));
        Cart cartDto = cartService.getCart(user);
        return ResponseEntity.ok(cartDto);
    }

    @Override
    public ResponseEntity<Cart> cartPost(String authorization, Product productDto) {
        if (!authService.isAuthenticated(authorization)) {
            return ResponseEntity.status(403).build();
        }

        UserEntity user = userService.toEntity(authService.getCurrentUser(authorization));
        Cart updatedCart = cartService.addProduct(user, productDto);
        return ResponseEntity.ok(updatedCart);
    }
}
