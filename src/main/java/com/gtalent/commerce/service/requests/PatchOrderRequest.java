package com.gtalent.commerce.service.requests;

import com.gtalent.commerce.service.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatchOrderRequest {
    private String shippingAddress;   //使用者更新收貨地址
    private OrderStatus status;       //訂單狀態
    private Boolean returned;         //使用者申請退貨
}
