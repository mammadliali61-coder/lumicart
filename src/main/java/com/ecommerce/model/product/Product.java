package com.ecommerce.model.product;

import com.ecommerce.model.shared.Discountable;
import java.util.Objects;

public abstract class Product implements Discountable {
    private final String id;
    private final String name;
    private final double basePrice;

    protected Product(String id, String name, double basePrice) {
        this.id = id;
        this.name = name;
        this.basePrice = basePrice;
    }

    public abstract String getCategory();

    @Override
    public abstract double applyDiscount();

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public double getFinalPrice() {
        return applyDiscount();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product product)) {
            return false;
        }
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
