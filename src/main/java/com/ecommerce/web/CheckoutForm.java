package com.ecommerce.web;

import jakarta.validation.constraints.NotBlank;

public class CheckoutForm {
    @NotBlank
    private String shippingAddress;

    @NotBlank
    private String paymentMethod;

    private String cardOption;

    private String checkoutCardNumber;

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getCardOption() {
        return cardOption;
    }

    public void setCardOption(String cardOption) {
        this.cardOption = cardOption;
    }

    public String getCheckoutCardNumber() {
        return checkoutCardNumber;
    }

    public void setCheckoutCardNumber(String checkoutCardNumber) {
        this.checkoutCardNumber = checkoutCardNumber;
    }
}
