package com.gtalent.commerce.service.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Table(name = "orders_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @ToString.Exclude  //避免循環引用
    private Order order;  //對應 orders.id

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;  //對應 products.id

    @Column(name = "product_name", length = 100, nullable = false)
    private String productName;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "date", nullable = false)
    private LocalDate date;  // 僅存下單日期，用於列表顯示

    public BigDecimal getTotal() {
        return price.multiply(BigDecimal.valueOf(quantity));  //計算單個商品明細的小計
    }
}
