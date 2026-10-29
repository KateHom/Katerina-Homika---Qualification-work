package com.example.qualificationwork.models;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    // Уникальный идентификатор записи (Primary Key)
    // Unique record identifier (Primary Key)
    private Long id;

    private String orderNumber;
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    // в базе данных сохранится именно слово, а не число, если использовать EnumType.STRING
    private OrderStatus orderStatus;

    // Связь "Многие к одному" с таблицей продуктов (каждый элемент заказа ссылается на товар)
    // Many-to-One relationship with the product table (each order item references a product)
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private Product product;
    private String productName;
    private int productQuantity;

    private String customerName;
    private String customerEmail;

    // Геттеры и сеттеры для доступа к полям сущности
    // Getters and setters for accessing entity fields
    public Long getId() {
        return id;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
