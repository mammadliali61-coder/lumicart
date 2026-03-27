package com.ecommerce.model.user;

public class Admin extends User {
    private final String department;

    public Admin(String id, String fullName, String email, String department) {
        super(id, fullName, email);
        this.department = department;
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }

    public String getDepartment() {
        return department;
    }
}
