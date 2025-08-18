package net.nagispace.productbackend.controller;

import net.nagispace.productbackend.service.AuthService;
import org.openapitools.api.ProductsApi;
import org.openapitools.model.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import net.nagispace.productbackend.service.ProductService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class ProductsApiController implements ProductsApi {

    private final ProductService productService;
    private final AuthService authService;

    public ProductsApiController(ProductService productService, AuthService authService) {
        this.productService = productService;
        this.authService = authService;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return ProductsApi.super.getRequest();
    }

    /**
     * GET /products : Liste tous les produits
     * Nécessite que l'utilisateur soit connecté
     */
    @Override
    public ResponseEntity<List<Product>> productsGet(String authHeader) {
        if (!authService.isAuthenticated(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * DELETE /products/{id} : Supprime un produit
     * Nécessite que l'utilisateur soit admin
     */
    @Override
    public ResponseEntity<Void> productsIdDelete(Long id, String authHeader) {
        if (!authService.isAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (productService.getProductById(id).isPresent()) {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * GET /products/{id} : Récupère un produit par ID
     * Nécessite que l'utilisateur soit connecté
     */
    @Override
    public ResponseEntity<Product> productsIdGet(Long id, String authHeader) {
        if (!authService.isAuthenticated(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PUT /products/{id} : Met à jour un produit existant
     * Nécessite que l'utilisateur soit admin
     */
    @Override
    public ResponseEntity<Product> productsIdPut(Long id, String authHeader, Product product) {
        if (!authService.isAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return productService.updateProduct(id, product)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /products : Crée un nouveau produit
     * Nécessite que l'utilisateur soit admin
     */
    @Override
    public ResponseEntity<Product> productsPost(String authHeader, Product product) {
        if (!authService.isAdmin(authHeader)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Product created = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
