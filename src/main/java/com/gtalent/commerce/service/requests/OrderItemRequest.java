package com.gtalent.commerce.service.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {
    private int productId;   //商品編號
    private int quantity;    //訂購數量

}
