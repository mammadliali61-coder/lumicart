package com.ecommerce.model.user;

public class Customer extends User {
    private final String shippingAddress;

    public Customer(String id, String fullName, String email, String shippingAddress) {
        super(id, fullName, email);
        this.shippingAddress = shippingAddress;
    }

    @Override
    public String getRole() {
        return "CUSTOMER";
    }

    public String getShippingAddress() {
        return shippingAddress;
    }
}
