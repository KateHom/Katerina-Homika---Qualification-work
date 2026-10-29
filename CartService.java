package com.example.qualificationwork.service;

import com.example.qualificationwork.models.Cart;
import com.example.qualificationwork.models.Order;
import com.example.qualificationwork.models.OrderStatus;
import com.example.qualificationwork.models.Product;
import com.example.qualificationwork.repository.OrderRepository;
import com.example.qualificationwork.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class CartService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public CartService(ProductRepository productRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    // Функция для добавления продукта в корзину с проверкой лимита склада
    // Function for adding product to cart with stock limit check
    public void add(Long productId, Cart cart) {
        Product product = getProductById(productId);
        if (product == null) return;

        int currentInCart = cart.getItems().getOrDefault(productId, 0);

        // Если в корзине уже лежит весь доступный остаток со склада — не даем добавить еще
        // if client try to add more item that it's on the stock, system will inform about available stock
        if (currentInCart + 1 > product.getQuantity()) {
            throw new IllegalArgumentException("Cannot add more. Max available stock is " + product.getQuantity());
        }
        cart.getItems().put(productId, currentInCart + 1);
    }

    // функция для удаления товара из корзины
     // function to remove item from the cart
    public void remove(Long productId, Cart cart) {
        cart.getItems().remove(productId);
    }

    // функция для того, чтобы убрать только одно количество товара (было два, клиент решил оставить 1)
     // function to decrease the product in the cart
    public void removeOne(Long productId, Cart cart) {
        cart.removeProduct(productId);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    // функция для оформления заказа
     // function to check out the order
    @Transactional
    public void checkout(Cart cart, String customerName, String customerEmail) {

        if (cart.getItems().isEmpty()) {
            return;
        }
        // Проверяем наличие каждого товара
        // check if item is available
        for (Map.Entry<Long, Integer> entry : cart.getItems().entrySet()) {
            Long productId = entry.getKey();
            Integer requestedQuantity = entry.getValue();

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            if (requestedQuantity > product.getQuantity()) {
                throw new IllegalArgumentException(
                        "Not enough stock for " + product.getName() +
                                ". Available: " + product.getQuantity() + ", requested: " + requestedQuantity
                );
            }

        LocalDateTime commonOrderDate = LocalDateTime.now();
        // Форматируем дату в вид "28092026"
        // formate date for order like "28092026"
        String datePart = commonOrderDate.format(DateTimeFormatter.ofPattern("ddMMyyyy"));

        // Переменная для хранения ЕДИНОГО номера заказа для всей корзины
        // Variable for storing a SINGLE order number for the entire cart
        String commonOrderNumber = null;

            // Списание со склада
            // Write-off from warehouse
            product.setQuantity(product.getQuantity() - requestedQuantity);
            productRepository.save(product);

            // Создаем позицию заказа
            // create position of order
            Order order = new Order();
            order.setOrderDate(commonOrderDate);
            order.setOrderStatus(OrderStatus.NEW);
            order.setProductName(product.getName());
            order.setProductQuantity(requestedQuantity);
            order.setCustomerName(customerName);
            order.setCustomerEmail(customerEmail);

            // Сохраняем объект в базу, чтобы база присвоила ему уникальный ID
            // save the order to database, that database gave a unique id
            Order savedOrder = orderRepository.save(order);

            // Если это ПЕРВЫЙ товар в цикле — генерируем общий номер чека: ID + Дата (например, ORD-1-28092026)
            // If this is the FIRST item in the cycle, we generate a general receipt number: ID + Date (for example, ORD-1-28092026)
            if (commonOrderNumber == null) {
                commonOrderNumber = "ORD-"+savedOrder.getId()+"-" + datePart;
            }

            // Присваиваем сформированный единый номер и обновляем запись
            // Assign the generated single number and update the record
            savedOrder.setOrderNumber(commonOrderNumber);
            orderRepository.save(savedOrder);
        }
    }
}