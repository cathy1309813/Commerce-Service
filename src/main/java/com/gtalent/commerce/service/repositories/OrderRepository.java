package com.gtalent.commerce.service.repositories;

import com.gtalent.commerce.service.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Integer> {
}
