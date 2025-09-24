package com.gtalent.commerce.service.controllers;

import com.gtalent.commerce.service.models.Order;
import com.gtalent.commerce.service.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/commerce-service/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        return ResponseEntity.ok(orderService.createOrder(order));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Integer id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateStatus(@PathVariable Integer id, @RequestParam OrderStatus status) {
        return orderService.updateStatus(id, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
