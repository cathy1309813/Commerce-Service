package com.gtalent.commerce.service.responses;

import com.gtalent.commerce.service.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {

    private int id;                        // 訂單ID
    private String orderReference;         // 訂單編號
    private String userName;               // 訂購者姓名
    private String userEmail;              // 訂購者信箱
    private LocalDateTime createdAt;       // 系統下單完整時間 (含時分秒)
    private OrderStatus status;            // 訂單狀態
    private boolean isReturned;            // 是否退貨
    private String shippingAddress;        // 運送地址

    private List<OrderItemResponse> items; // 商品明細列表


    //訂單統計金額
    private BigDecimal sum;                //商品總金額
    private BigDecimal delivery;           //運費
    private BigDecimal tax;                //稅金 (20%)
    private BigDecimal total;              //含運費與稅金總金額
}
