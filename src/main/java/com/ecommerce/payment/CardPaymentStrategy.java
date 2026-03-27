package com.ecommerce.payment;

public class CardPaymentStrategy implements PaymentStrategy {
    private final String cardHolder;
    private final String maskedCardNumber;

    public CardPaymentStrategy(String cardHolder, String maskedCardNumber) {
        this.cardHolder = cardHolder;
        this.maskedCardNumber = maskedCardNumber;
    }

    @Override
    public boolean pay(double amount) {
        return amount > 0 && cardHolder != null && !cardHolder.isBlank()
                && maskedCardNumber != null && !maskedCardNumber.isBlank();
    }

    @Override
    public String getPaymentName() {
        return "Card Payment";
    }
}
