package com.ecommerce.model.order;

import com.ecommerce.model.shared.Payable;
import com.ecommerce.model.shared.Shippable;
import com.ecommerce.model.user.Customer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order implements Payable, Shippable {
    private final String orderId;
    private final Customer customer;
    private final List<OrderItem> items;
    private final String paymentMethod;
    private final LocalDateTime createdAt;
    private OrderStatus status;

    public Order(String orderId, Customer customer, List<OrderItem> items, String paymentMethod) {
        this.orderId = orderId;
        this.customer = customer;
        this.items = new ArrayList<>(items);
        this.paymentMethod = paymentMethod;
        this.createdAt = LocalDateTime.now();
        this.status = OrderStatus.CREATED;
    }

    public String getOrderId() {
        return orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    @Override
    public double getPayableAmount() {
        return items.stream().mapToDouble(OrderItem::getLineTotal).sum();
    }

    @Override
    public String getShippingLabel() {
        return customer.getFullName() + " | " + customer.getShippingAddress();
    }
}
