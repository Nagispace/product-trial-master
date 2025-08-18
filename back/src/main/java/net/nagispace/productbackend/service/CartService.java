package net.nagispace.productbackend.service;

import net.nagispace.productbackend.entity.CartEntity;
import net.nagispace.productbackend.entity.ProductEntity;
import net.nagispace.productbackend.entity.UserEntity;
import net.nagispace.productbackend.repository.CartRepository;
import org.openapitools.model.Product;
import org.openapitools.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CartService {

    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository){
        this.cartRepository = cartRepository;
    }

    public CartEntity getCart(UserEntity user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    CartEntity cart = new CartEntity();
                    cart.setUser(user);
                    cart.setProducts(new ArrayList<>());
                    return cartRepository.save(cart);
                });
    }

    public CartEntity addProduct(User user, Product product) {
        CartEntity cart = getCart(user);
        cart.getProducts().add(product);
        return cartRepository.save(cart);
    }
}
