package com.ecommerce.web;

public class AdminProductForm {
    private String category;
    private String name;
    private String price;
    private String primaryDetail;
    private String secondaryDetail;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrimaryDetail() {
        return primaryDetail;
    }

    public void setPrimaryDetail(String primaryDetail) {
        this.primaryDetail = primaryDetail;
    }

    public String getSecondaryDetail() {
        return secondaryDetail;
    }

    public void setSecondaryDetail(String secondaryDetail) {
        this.secondaryDetail = secondaryDetail;
    }
}
