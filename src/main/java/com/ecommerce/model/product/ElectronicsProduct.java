package com.ecommerce.model.product;

public class ElectronicsProduct extends Product {
    private final String brand;
    private final int warrantyMonths;

    public ElectronicsProduct(String id, String name, double basePrice, String brand, int warrantyMonths) {
        super(id, name, basePrice);
        this.brand = brand;
        this.warrantyMonths = warrantyMonths;
    }

    @Override
    public String getCategory() {
        return "Electronics";
    }

    @Override
    public double applyDiscount() {
        return getBasePrice() * 0.90;
    }

    public String getBrand() {
        return brand;
    }

    public int getWarrantyMonths() {
        return warrantyMonths;
    }
}
