package com.example.qualificationwork.service;

import com.example.qualificationwork.models.Order;
import com.example.qualificationwork.models.OrderStatus;
import com.example.qualificationwork.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // Возвращает сгруппированные активные заказы (NEW и PROCESSING)
    // Return grouped active orders
    public Map<String, List<Order>> getActiveOrdersGroupedByNumber() {
        List<OrderStatus> activeOrderStatus = List.of(
                OrderStatus.NEW,
                OrderStatus.PROCESSING
        );
        List<Order> activeOrders = orderRepository.findByOrderStatusIn(activeOrderStatus);

        // Создаем LinkedHashMap для сохранения порядка и группируем позиции по номеру заказа
        // Creating a LinkedHashMap to preserve order and group items by order number
        Map<String, List<Order>> ordersByNumber = new LinkedHashMap<>();
        for (Order order : activeOrders) {
            ordersByNumber.computeIfAbsent(order.getOrderNumber(), k -> new ArrayList<>()).add(order);
        }

        return ordersByNumber;
    }

    // Возвращает выполненные заказы для истории (сгруппированные по orderNumber)
    // return completed orders for history
    public Map<String, List<Order>> getCompletedOrdersGroupedByNumber() {
        List<OrderStatus> completedOrdersStatus = List.of(OrderStatus.COMPLETED);
        List<Order> completedOrders = orderRepository.findByOrderStatusIn(completedOrdersStatus);
        Map<String, List<Order>> ordersByNumber = new LinkedHashMap<>();
        for (Order order : completedOrders) {
            ordersByNumber.computeIfAbsent(order.getOrderNumber(), k -> new ArrayList<>()).add(order);
        }
        return ordersByNumber;
    }

    // Смена статуса заказа (для всех товаров с указанным orderNumber)
    // change status for order
    @Transactional
    public void updateOrderStatus(String orderNumber, OrderStatus newStatus) {
        List<Order> orders = orderRepository.findByOrderNumber(orderNumber);
        for (Order order : orders) {
            order.setOrderStatus(newStatus);
            orderRepository.save(order);
        }
    }

    // Удаление заказа
    // delete the order
    @Transactional
    public void deleteByOrderNumber(String orderNumber) {
        orderRepository.deleteByOrderNumber(orderNumber);
    }
}