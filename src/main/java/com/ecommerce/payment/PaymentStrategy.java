package com.ecommerce.payment;

public interface PaymentStrategy {
    boolean pay(double amount);
    String getPaymentName();
}
