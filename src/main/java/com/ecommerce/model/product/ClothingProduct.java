package com.ecommerce.model.product;

public class ClothingProduct extends Product {
    private final String size;
    private final String color;

    public ClothingProduct(String id, String name, double basePrice, String size, String color) {
        super(id, name, basePrice);
        this.size = size;
        this.color = color;
    }

    @Override
    public String getCategory() {
        return "Clothing";
    }

    @Override
    public double applyDiscount() {
        return getBasePrice() * 0.85;
    }

    public String getSize() {
        return size;
    }

    public String getColor() {
        return color;
    }
}
