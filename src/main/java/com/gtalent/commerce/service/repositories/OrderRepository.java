package com.gtalent.commerce.service.repositories;

import com.gtalent.commerce.service.enums.OrderStatus;
import com.gtalent.commerce.service.models.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByStatus(OrderStatus status);  //根據訂單狀態查詢，如取得 Pending Orders 功能
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

}
