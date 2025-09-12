package com.gtalent.commerce.service.models;

import com.gtalent.commerce.service.enums.ReviewStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private LocalDate date;  //評論日期

    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private int rating;  //評分 (1~5)

    @Size(max = 1000)
    private String comment;  //評論內容

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;  //客戶

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;  //產品

    @CreationTimestamp  //自動填充實體的建立時間
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;  //建立時間

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;  //更新時間

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;  //軟刪除時間
}
