package com.example.qualificationwork.repository;

import com.example.qualificationwork.models.Order;
import com.example.qualificationwork.models.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByOrderNumber(String orderNumber);
    List<Order> findByOrderStatusIn(List<OrderStatus> orderStatus);

    // Аннотация @Transactional обязательна для операций удаления (delete/remove)
    @Transactional
    @Modifying // Говорит Spring JPA, что метод меняет данные
    void deleteByOrderNumber(String orderNumber);
}
