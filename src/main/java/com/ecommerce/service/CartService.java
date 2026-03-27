package com.ecommerce.service;

import com.ecommerce.model.cart.Cart;
import com.ecommerce.model.product.Product;
import org.springframework.stereotype.Service;

@Service
public class CartService {
    private final Cart cart = new Cart();

    public Cart getCart() {
        return cart;
    }

    public void addProduct(Product product, int quantity) {
        cart.addProduct(product, quantity);
    }

    public void clear() {
        cart.clear();
    }
}
