package com.ecommerce.service;

import com.ecommerce.model.cart.Cart;
import com.ecommerce.model.cart.CartItem;
import com.ecommerce.model.order.Order;
import com.ecommerce.model.order.OrderItem;
import com.ecommerce.model.order.OrderStatus;
import com.ecommerce.model.user.Customer;
import com.ecommerce.payment.CardPaymentStrategy;
import com.ecommerce.payment.CashOnDeliveryStrategy;
import com.ecommerce.payment.PaymentStrategy;
import com.ecommerce.payment.WalletPaymentStrategy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final List<Order> orders = new ArrayList<>();

    public Order checkout(Customer customer, Cart cart, String paymentMethod) {
        if (cart.isEmpty()) {
            throw new IllegalStateException("Cart cannot be empty.");
        }

        List<OrderItem> orderItems = cart.getItems().stream()
                .map(this::toOrderItem)
                .collect(Collectors.toList());

        PaymentStrategy paymentStrategy = createPaymentStrategy(paymentMethod, customer);
        Order order = new Order(generateOrderId(), customer, orderItems, paymentStrategy.getPaymentName());
        boolean paid = paymentStrategy.pay(order.getPayableAmount());
        order.setStatus(paid ? OrderStatus.PAID : OrderStatus.FAILED);
        orders.add(0, order);
        return order;
    }

    public List<Order> getOrders() {
        return Collections.unmodifiableList(orders);
    }

    private PaymentStrategy createPaymentStrategy(String paymentMethod, Customer customer) {
        if ("CARD".equalsIgnoreCase(paymentMethod)) {
            return new CardPaymentStrategy(customer.getFullName(), "****-****-****-4242");
        }
        if ("WALLET".equalsIgnoreCase(paymentMethod)) {
            return new WalletPaymentStrategy();
        }
        return new CashOnDeliveryStrategy();
    }

    private OrderItem toOrderItem(CartItem cartItem) {
        return new OrderItem(
                cartItem.getProduct().getName(),
                cartItem.getQuantity(),
                cartItem.getProduct().getFinalPrice()
        );
    }

    private String generateOrderId() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
