package com.ecommerce.payment;

public class WalletPaymentStrategy implements PaymentStrategy {
    @Override
    public boolean pay(double amount) {
        return amount > 0;
    }

    @Override
    public String getPaymentName() {
        return "Wallet";
    }
}
