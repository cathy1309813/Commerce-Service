package com.gtalent.commerce.service.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private Integer id;
    private String orderReference;
    private Long userId;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
