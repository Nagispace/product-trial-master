package net.nagispace.productbackend.service;

import net.nagispace.productbackend.entity.WishlistEntity;
import net.nagispace.productbackend.entity.ProductEntity;
import net.nagispace.productbackend.entity.UserEntity;
import net.nagispace.productbackend.repository.WishlistRepository;
import org.openapitools.model.Wishlist;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;

    public WishlistService(WishlistRepository wishlistRepository){
        this.wishlistRepository = wishlistRepository;
    }

    public WishlistEntity getWishlist(UserEntity user) {
        return wishlistRepository.findByUser(user)
                .orElseGet(() -> {
                    WishlistEntity wishlist = new WishlistEntity();
                    wishlist.setUser(user);
                    wishlist.setProducts(new ArrayList<>());
                    return wishlistRepository.save(wishlist);
                });
    }

    public WishlistEntity addProduct(UserEntity user, ProductEntity product) {
        WishlistEntity wishlist = getWishlist(user);
        wishlist.getProducts().add(product);
        return wishlistRepository.save(wishlist);
    }
    // TODO finir les mapper de ses morts entity <-> dto
}

