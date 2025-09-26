package com.gtalent.commerce.service.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //自動生成ID
    private int id;

    @Column(name = "image_url", length = 250)
    private String imageUrl;  //產品圖片 URL

    @Column(name = "thumbnail_url", length = 250)
    private String thumbnailUrl;  //縮圖 URL

    @Column(name = "reference", nullable = false, length = 50)
    private String reference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false)
    private Category category;   //直接關聯到 Category

    @Column(name = "width", nullable = false, precision = 6, scale = 2)
    private BigDecimal width;

    @Column(name = "height", nullable = false, precision = 6, scale = 2)
    private BigDecimal height;

    @Column(name = "depth", nullable = false, precision = 6, scale = 2)
    private BigDecimal depth;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "stock", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int stock;

    @Column(name = "sales", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int sales;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp  //自動填充實體的建立時間
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
/* columnDefinition 的作用
   columnDefinition 是 @Column 的一個屬性，用來 直接指定資料庫欄位的完整 SQL 定義。
   注意! SQL 語法必須符合目前使用的資料庫!
   整數欄位：stock、sales = 0
   小數欄位：width、height、price = 0.00*/
/* referencedColumnName 的作用
   指定 Product 表的外鍵欄位 (category_id) 參考 Category 表的哪個欄位。
   一般情況 (對應主鍵) -> referencedColumnName 可以省略，
   特殊情況 (對應非主鍵欄位) -> 需要指定 referencedColumnName。*/
