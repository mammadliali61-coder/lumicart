package com.ecommerce.web;

public class SessionUser {
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String shippingAddress;
    private String cardNumber;
    private double balance;

    public SessionUser(String fullName, String email, String role) {
        String[] parts = splitFullName(fullName);
        this.firstName = parts[0];
        this.lastName = parts[1];
        this.email = email;
        this.role = role;
        this.shippingAddress = "";
        this.cardNumber = "";
        this.balance = 0.0;
    }

    public String getFullName() {
        return (firstName + " " + lastName).trim();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getMaskedCardNumber() {
        if (cardNumber == null || cardNumber.isBlank()) {
            return "No card added";
        }
        String digits = cardNumber.replaceAll("\\s+", "");
        if (digits.length() <= 4) {
            return digits;
        }
        return "**** **** **** " + digits.substring(digits.length() - 4);
    }

    public boolean hasSavedCard() {
        return cardNumber != null && !cardNumber.isBlank();
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    private static String[] splitFullName(String fullName) {
        String value = fullName == null ? "" : fullName.trim();
        if (value.isEmpty()) {
            return new String[]{"", ""};
        }
        String[] parts = value.split("\\s+", 2);
        if (parts.length == 1) {
            return new String[]{parts[0], ""};
        }
        return parts;
    }
}
