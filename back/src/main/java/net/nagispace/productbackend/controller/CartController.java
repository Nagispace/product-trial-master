package net.nagispace.productbackend.controller;

import net.nagispace.productbackend.entity.CartEntity;
import net.nagispace.productbackend.entity.ProductEntity;
import net.nagispace.productbackend.entity.UserEntity;
import net.nagispace.productbackend.service.AuthService;
import net.nagispace.productbackend.service.CartService;
import org.openapitools.api.CartApi;
import org.openapitools.model.Cart;
import org.openapitools.model.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Optional;

@RestController
@RequestMapping("/cart")
public class CartController implements CartApi {

    private final CartService cartService;
    private final AuthService authService;

    public CartController(CartService cartService, AuthService authService) {
        this.cartService = cartService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<CartEntity> getCart(@RequestHeader("Authorization") String authHeader){
        UserEntity user = authService.getCurrentUser(authHeader);
        return ResponseEntity.ok(cartService.getCart(user));
    }

    @PostMapping
    public ResponseEntity<CartEntity> addProduct(@RequestHeader("Authorization") String authHeader,
                                                 @RequestBody ProductEntity product){

    }

    /**
     * @return
     */
    @Override
    public Optional<NativeWebRequest> getRequest() {
        return CartApi.super.getRequest();
    }

    /**
     * GET /cart : Récupère le panier de l&#39;utilisateur connecté
     *
     * @param authorization Bearer token JWT (required)
     * @return Panier récupéré (status code 200)
     * or Non autorisé (status code 403)
     */
    @Override
    public ResponseEntity<Cart> cartGet(String authorization) {
        return CartApi.super.cartGet(authorization);
    }

    /**
     * POST /cart : Ajoute un produit au panier
     *
     * @param authorization Bearer token JWT (required)
     * @param product       (required)
     * @return Produit ajouté au panier (status code 200)
     * or Non autorisé (status code 403)
     */
    @Override
    public ResponseEntity<Cart> cartPost(String authorization, Product product) {
        UserEntity user = authService.getCurrentUser(authorization);
        return ResponseEntity.ok(cartService.addProduct(user, product));
    }
}
