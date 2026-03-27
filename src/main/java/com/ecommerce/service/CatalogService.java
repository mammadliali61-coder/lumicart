package com.ecommerce.service;

import com.ecommerce.model.product.ClothingProduct;
import com.ecommerce.model.product.ElectronicsProduct;
import com.ecommerce.model.product.Product;
import com.ecommerce.model.cart.Cart;
import com.ecommerce.model.cart.CartItem;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class CatalogService {
    private final List<Product> products = new ArrayList<>();
    private final Map<String, Integer> salesCounts = new LinkedHashMap<>();
    private int nextElectronicsId = 112;
    private int nextClothingId = 212;

    @PostConstruct
    void initCatalog() {
        products.add(new ElectronicsProduct("P-100", "Laptop Pro 14", 1800.0, "TechBrand", 24));
        products.add(new ElectronicsProduct("P-101", "Wireless Headphones", 250.0, "SoundMax", 12));
        products.add(new ElectronicsProduct("P-102", "Smart Watch", 320.0, "PulseGear", 18));
        products.add(new ElectronicsProduct("P-103", "Mechanical Keyboard", 145.0, "KeyNova", 12));
        products.add(new ElectronicsProduct("P-104", "4K Monitor", 520.0, "ViewEdge", 24));
        products.add(new ElectronicsProduct("P-105", "Portable SSD 1TB", 180.0, "FlashCore", 18));
        products.add(new ElectronicsProduct("P-106", "Gaming Mouse", 85.0, "AeroClick", 12));
        products.add(new ElectronicsProduct("P-107", "Bluetooth Speaker", 135.0, "EchoBeat", 12));
        products.add(new ElectronicsProduct("P-108", "USB-C Dock Station", 210.0, "Dockify", 18));
        products.add(new ElectronicsProduct("P-109", "5-Star Ultra Earbuds", 199.0, "LumiSound", 18));
        products.add(new ElectronicsProduct("P-110", "Creator Tablet X", 410.0, "NovaSketch", 24));
        products.add(new ElectronicsProduct("P-111", "Crystal Webcam Pro", 165.0, "VisionFlow", 12));
        products.add(new ClothingProduct("P-200", "Winter Jacket", 160.0, "L", "Blue"));
        products.add(new ClothingProduct("P-201", "Casual Sneakers", 120.0, "42", "White"));
        products.add(new ClothingProduct("P-202", "Essential Hoodie", 90.0, "M", "Black"));
        products.add(new ClothingProduct("P-203", "Cotton T-Shirt", 35.0, "M", "Ivory"));
        products.add(new ClothingProduct("P-204", "Denim Overshirt", 78.0, "L", "Stone Blue"));
        products.add(new ClothingProduct("P-205", "Relaxed Joggers", 64.0, "M", "Graphite"));
        products.add(new ClothingProduct("P-206", "Everyday Cap", 26.0, "Standard", "Beige"));
        products.add(new ClothingProduct("P-207", "Lightweight Puffer", 132.0, "XL", "Olive"));
        products.add(new ClothingProduct("P-208", "Minimal Knit Sweater", 88.0, "S", "Sand"));
        products.add(new ClothingProduct("P-209", "5-Star Street Runner", 148.0, "43", "Onyx"));
        products.add(new ClothingProduct("P-210", "Premium Linen Set", 112.0, "M", "Cream"));
        products.add(new ClothingProduct("P-211", "Signature Travel Hoodie", 96.0, "L", "Terracotta"));
        for (int index = 0; index < products.size(); index++) {
            salesCounts.put(products.get(index).getId(), 160 - index * 6);
        }
    }

    public List<Product> getAllProducts() {
        return Collections.unmodifiableList(products);
    }

    public Product findById(String id) {
        return products.stream()
                .filter(product -> product.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
    }

    public Product addProduct(String category, String name, double price, String primaryDetail, String secondaryDetail) {
        Product product;
        if ("CLOTHING".equalsIgnoreCase(category)) {
            product = new ClothingProduct(
                    "P-" + nextClothingId++,
                    name,
                    price,
                    primaryDetail,
                    secondaryDetail
            );
        } else {
            int warrantyMonths = 12;
            try {
                warrantyMonths = Integer.parseInt(secondaryDetail);
            } catch (NumberFormatException ignored) {
                // Default warranty used for admin-added electronics.
            }
            product = new ElectronicsProduct(
                    "P-" + nextElectronicsId++,
                    name,
                    price,
                    primaryDetail,
                    warrantyMonths
            );
        }
        products.add(0, product);
        salesCounts.put(product.getId(), 0);
        return product;
    }

    public Map<String, Integer> getSalesCounts() {
        return Collections.unmodifiableMap(salesCounts);
    }

    public int getSalesCount(String productId) {
        return salesCounts.getOrDefault(productId, 0);
    }

    public void recordPurchase(Cart cart) {
        for (CartItem item : cart.getItems()) {
            salesCounts.merge(item.getProduct().getId(), item.getQuantity(), Integer::sum);
        }
    }
}
