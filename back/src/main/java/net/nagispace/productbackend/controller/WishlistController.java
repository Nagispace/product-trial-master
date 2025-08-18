package net.nagispace.productbackend.controller;

import org.openapitools.api.WishlistApi;
import org.openapitools.model.Product;
import org.openapitools.model.Wishlist;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Optional;

@RestController
@RequestMapping("/wishlist")
public class WishlistController implements WishlistApi {

    /**
     * @return
     */
    @Override
    public Optional<NativeWebRequest> getRequest() {
        return WishlistApi.super.getRequest();
    }

    /**
     * GET /wishlist : Récupère la wishlist de l&#39;utilisateur connecté
     *
     * @param authorization Bearer token JWT (required)
     * @return Wishlist récupérée (status code 200)
     * or Non autorisé (status code 403)
     */
    @Override
    public ResponseEntity<Wishlist> wishlistGet(String authorization) {
        return WishlistApi.super.wishlistGet(authorization);
    }

    /**
     * POST /wishlist : Ajoute un produit à la wishlist
     *
     * @param authorization Bearer token JWT (required)
     * @param product       (required)
     * @return Produit ajouté à la wishlist (status code 200)
     * or Non autorisé (status code 403)
     */
    @Override
    public ResponseEntity<Wishlist> wishlistPost(String authorization, Product product) {
        return WishlistApi.super.wishlistPost(authorization, product);
    }
}

