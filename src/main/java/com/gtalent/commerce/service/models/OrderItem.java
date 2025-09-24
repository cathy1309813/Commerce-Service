package com.gtalent.commerce.service.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "orders_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;  //對應 orders id

    @Column(name = "product_id", nullable = false)
    private Long productId;  //對應 products id

    @Column(name = "product_name", length = 100, nullable = false)
    private String productName;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private BigDecimal price;

    public BigDecimal getTotal() {
        return price.multiply(BigDecimal.valueOf(quantity));  //計算此明細的總金額
    }
}
