package com.gtalent.commerce.service.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PendingOrderResponse {
    private int orderId;
    private BigDecimal totalAmount;   //訂單總金額
    private LocalDateTime createdAt;  //下單時間
    private int itemCount;            //商品數量
}
