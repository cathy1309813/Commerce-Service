package com.gtalent.commerce.service.services;

import com.gtalent.commerce.service.models.OrderItem;
import com.gtalent.commerce.service.responses.OrderItemResponse;
import com.gtalent.commerce.service.responses.OrderResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class OrderItemService {

    //DTO 轉換
    //mapToDto -> 將資料庫實體 (OrderItem) 轉換成 API 回傳格式 (OrderItemResponse)，並填入必要欄位 (例如商品名稱、價格、數量、小計、下單日期等)
    public OrderItemResponse mapToDto(OrderItem item, LocalDateTime orderCreatedAt) {
        OrderItemResponse response = new OrderItemResponse();  //創建一個 response 物件
        response.setProductReference(item.getProduct().getReference());  //將資訊存入
        response.setProductName(item.getProductName());
        response.setUnitPrice(item.getPrice());
        response.setQuantity(item.getQuantity());
        response.setTotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        response.setDate(orderCreatedAt.toLocalDate().toString()); //年/月/日
        response.setWidth(item.getProduct().getWidth());
        response.setHeight(item.getProduct().getHeight());
        response.setDepth(item.getProduct().getDepth());
        return response;  //儲存後回傳 response 物件
    }

    //計算金額
    public void calculateTotals(OrderResponse response) {
        final BigDecimal TAX_RATE = BigDecimal.valueOf(0.2);               //20% 稅率
        final BigDecimal DELIVERY_RATE_PER_UNIT = BigDecimal.valueOf(10);  //假設每單位體積 10 美元

        BigDecimal sum = BigDecimal.ZERO;           //商品總額
        BigDecimal totalVolume = BigDecimal.ZERO;   //累計體積，用於運費

        //1.計算商品總額 & 累計體積
        for (OrderItemResponse item : response.getItems()) {
            //計算商品小計
            item.calculateTotal();
            sum = sum.add(item.getTotal());
            //計算商品體積
            totalVolume = totalVolume.add(item.calculateVolume());
        }
        response.setSum(sum);

        //2.計算運費
        BigDecimal delivery = totalVolume.multiply(DELIVERY_RATE_PER_UNIT)
                .setScale(2, RoundingMode.HALF_UP);
        response.setDelivery(delivery);

        //3.計算稅金 (Sum + Delivery) * 稅率
        BigDecimal tax = sum.add(delivery)
                .multiply(TAX_RATE)
                .setScale(2, RoundingMode.HALF_UP);
        response.setTax(tax);

        //4.計算最終總額
        BigDecimal total = sum.add(delivery).add(tax);
        response.setTotal(total);
    }
}
