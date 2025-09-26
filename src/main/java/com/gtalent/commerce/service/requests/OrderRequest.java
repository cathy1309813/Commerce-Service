package com.gtalent.commerce.service.requests;

import com.gtalent.commerce.service.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private int userId;                //訂購者 ID
    private String shippingAddress;        //運送地址
    private List<OrderItemRequest> items;  //訂單商品明細
}
