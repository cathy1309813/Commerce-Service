package com.gtalent.commerce.service.services;

import com.gtalent.commerce.service.models.Order;
import com.gtalent.commerce.service.repositories.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(Order order) {
        order.setStatus(OrderStatus.ORDERED);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    public Optional<Order> updateStatus(Integer orderId, OrderStatus status) {
        return orderRepository.findById(orderId).map(order -> {
            order.setStatus(status);
            order.setUpdatedAt(LocalDateTime.now());
            return orderRepository.save(order);
        });
    }

    public Optional<Order> getOrderById(Integer orderId) {
        return orderRepository.findById(orderId);
    }
}
