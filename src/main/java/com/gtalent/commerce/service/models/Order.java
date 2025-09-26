package com.gtalent.commerce.service.models;

import com.gtalent.commerce.service.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
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

    @Column(name = "order_reference", length = 20, nullable = true)
    private String orderReference;  //訂單編號

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  //關聯 User -> 訂購者

    @Enumerated(EnumType.STRING)
    @Column
    private OrderStatus status;  //訂單狀態 -> ordered, delivered, cancelled

    @Column(name = "shipping_address", length = 255, nullable = false)
    private String shippingAddress;  //收貨地址

    @Column(name = "returned", nullable = false)
    private boolean returned = false;  //是否退貨

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;  //商品總金額

    @Column(name = "delivery_fee", nullable = false)
    private BigDecimal deliveryFee = BigDecimal.ZERO;  //運費

    @Column(name = "tax_amount", nullable = false)
    private BigDecimal taxAmount = BigDecimal.ZERO;  //稅金

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<OrderItem> items = new ArrayList<>();  //關聯訂單明細

    @CreationTimestamp  //自動填充實體的建立時間
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;  //下單完整時間

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public BigDecimal calculateTotal() {
        return totalAmount.add(deliveryFee).add(taxAmount);  //計算整筆訂單的總額（含運費、稅金）
    }

    /* @Enumerated(EnumType.STRING) 的作用
       用途：指定 Enum 在資料庫中的儲存方式。
       作用：將 Enum 以「字串」(名稱) 儲存到資料庫。
       優點：
         1.資料庫可讀性高，方便查詢與除錯。
         2.Enum 順序改變時，舊資料不會對應錯誤。*/

}
