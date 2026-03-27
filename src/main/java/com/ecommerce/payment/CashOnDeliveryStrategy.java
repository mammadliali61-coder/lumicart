package com.ecommerce.payment;

public class CashOnDeliveryStrategy implements PaymentStrategy {
    @Override
    public boolean pay(double amount) {
        return amount > 0;
    }

    @Override
    public String getPaymentName() {
        return "Cash On Delivery";
    }
}
