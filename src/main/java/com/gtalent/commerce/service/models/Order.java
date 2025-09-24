package com.gtalent.commerce.service.models;

import com.gtalent.commerce.service.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "order_reference", length = 20, nullable = false)
    private String orderReference;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  //關聯 User

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(nullable = false)
    private OrderStatus status;  //ordered, delivered, cancelled

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();  //關聯訂單明細

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
