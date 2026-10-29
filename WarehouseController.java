package com.example.qualificationwork.controllers;

import com.example.qualificationwork.models.Order;
import com.example.qualificationwork.models.OrderStatus;
import com.example.qualificationwork.models.Product;
import com.example.qualificationwork.repository.ProductRepository;
import com.example.qualificationwork.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class WarehouseController {

    private final OrderService orderService;
    private final ProductRepository productRepository;

    public WarehouseController(OrderService orderService, ProductRepository productRepository) {
        this.orderService = orderService;
        this.productRepository = productRepository;
    }

    // Отображение ТОЛЬКО активных заказов (NEW, PROCESSING)
    // Show only active orders (NEW, PROCESSING)
    @GetMapping("/orders")
    public String showActiveOrders(Model model) {
        Map<String, List<Order>> ordersByNumber = orderService.getActiveOrdersGroupedByNumber();
        model.addAttribute("ordersByNumber", ordersByNumber);
        return "warehouse/orders";
    }

    // Смена статуса заказа (например, с NEW на PROCESSING или COMPLETED)
    // Update order status
    @PostMapping("/warehouse/orders/update-status")
    public String updateStatus(@RequestParam String orderNumber, @RequestParam OrderStatus newStatus) {
        orderService.updateOrderStatus(orderNumber, newStatus);
        return "redirect:/orders";
    }

    // Страница товаров на складе
    // On the warehouse link has icon Product, where are illustrated all product, which are on the website
    @GetMapping("/products")
    public String showProducts(Model model) {
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);
        return "warehouse/products";
    }

    // Обновление информации о товаре
    @PostMapping("/warehouse/update-information")
    public String updateInformation(@RequestParam Long productId,
                                    @RequestParam double newPrice,
                                    @RequestParam int newQuantity) {
        // Проверяем существование товара по ID, чтобы избежать падения, если товар не найден
        // Check if product exists by ID to prevent application crash if product is missing
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id: " + productId));

        product.setPrice(newPrice);
        product.setQuantity(newQuantity);

        productRepository.save(product);
        return "redirect:/products";
    }

    // История выполненных заказов
    // History of all completed orders
    @GetMapping("history")
    public String showHistory(Model model) {
        Map<String, List<Order>> completedOrders = orderService.getCompletedOrdersGroupedByNumber();
        model.addAttribute("completedOrders", completedOrders);
        return "warehouse/history";
    }

    // Удаление заказа из истории
    // Delete order from the history
    @PostMapping("/warehouse/history/delete/{orderNumber}")
    public String deleteOrder(@PathVariable String orderNumber) {
        orderService.deleteByOrderNumber(orderNumber);
        return "redirect:/history";
    }
}