package com.ecommerce.model.cart;

import com.ecommerce.model.product.Product;
import com.ecommerce.model.shared.Payable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cart implements Payable {
    private final List<CartItem> items = new ArrayList<>();

    public void addProduct(Product product, int quantity) {
        for (CartItem item : items) {
            if (item.getProduct().equals(product)) {
                item.increaseQuantity(quantity);
                return;
            }
        }
        items.add(new CartItem(product, quantity));
    }

    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    @Override
    public double getPayableAmount() {
        return items.stream().mapToDouble(CartItem::getLineTotal).sum();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public int getTotalUnits() {
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }

    public void clear() {
        items.clear();
    }
}
