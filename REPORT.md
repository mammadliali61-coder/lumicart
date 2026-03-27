# E-Commerce Platform Report

This project is a Spring Boot web application written in Java for managing products, cart operations, orders, and payments in the browser.

## Main Features

- Product catalog management
- Adding products to cart
- Checkout and order generation
- Multiple payment methods
- Shipping information in completed orders
- Browser-based interface with Thymeleaf templates

## OOP Requirements Mapping

- 2 abstract classes:
  - `User`
  - `Product`
- 3 interfaces:
  - `Discountable`
  - `Payable`
  - `Shippable`
  - Extra: `PaymentStrategy`
- Composition:
  - `Cart` contains `CartItem`
  - `Order` contains `OrderItem`
- Inheritance:
  - `Customer` and `Admin` extend `User`
  - `ElectronicsProduct` and `ClothingProduct` extend `Product`
- Design Pattern:
  - Strategy pattern for payment processing via `PaymentStrategy`
- equals/hashCode:
  - Implemented in `User`, `Product`, and `CartItem`

## Packages

- `com.ecommerce.model.user`
- `com.ecommerce.model.product`
- `com.ecommerce.model.cart`
- `com.ecommerce.model.order`
- `com.ecommerce.model.shared`
- `com.ecommerce.payment`
- `com.ecommerce.service`
- `com.ecommerce.web`

## How To Run

IntelliJ IDEA:

- Open the project as a Maven project
- Wait for Maven dependencies to load
- Run `com.ecommerce.EcommerceApplication`
- Open `http://localhost:8080` in Chrome

Terminal:

- Run `mvn spring-boot:run`
- Open `http://localhost:8080` in Chrome
