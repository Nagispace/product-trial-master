package net.nagispace.productbackend.controller;

import org.openapitools.api.ProductsApi;
import org.openapitools.model.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/")
class ProductsApiController implements ProductsApi {

    /**
     * @return
     */
    @Override
    public Optional<NativeWebRequest> getRequest() {
        return ProductsApi.super.getRequest();
    }

    /**
     * GET /products : Liste tous les produits
     *
     * @return Liste des produits (status code 200)
     */
    @Override
    public ResponseEntity<List<Product>> productsGet() {
        return ProductsApi.super.productsGet();
    }

    /**
     * DELETE /products/{id} : Supprime un produit
     *
     * @param id (required)
     * @return Produit supprimé (status code 204)
     * or Produit non trouvé (status code 404)
     */
    @Override
    public ResponseEntity<Void> productsIdDelete(Integer id) {
        return ProductsApi.super.productsIdDelete(id);
    }

    /**
     * GET /products/{id} : Récupère un produit par ID
     *
     * @param id (required)
     * @return Produit trouvé (status code 200)
     * or Produit non trouvé (status code 404)
     */
    @Override
    public ResponseEntity<Product> productsIdGet(Integer id) {
        return ProductsApi.super.productsIdGet(id);
    }

    /**
     * PUT /products/{id} : Met à jour un produit existant
     *
     * @param id      (required)
     * @param product (required)
     * @return Produit mis à jour (status code 200)
     * or Produit non trouvé (status code 404)
     */
    @Override
    public ResponseEntity<Product> productsIdPut(Integer id, Product product) {
        return ProductsApi.super.productsIdPut(id, product);
    }

    /**
     * POST /products : Crée un nouveau produit
     *
     * @param product (required)
     * @return Produit créé (status code 201)
     */
    @Override
    public ResponseEntity<Product> productsPost(Product product) {
        return ProductsApi.super.productsPost(product);
    }
}
