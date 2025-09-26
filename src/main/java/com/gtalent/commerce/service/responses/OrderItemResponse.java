package com.gtalent.commerce.service.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    private int id;                      //商品明細ID
    private String productReference;     //商品編號或 SKU
    private String productName;          //商品名稱
    private BigDecimal unitPrice;        //單價
    private int quantity;                //數量
    private BigDecimal total;            //小計 = unitPrice * quantity
    private String date;                 //下單日期 (年/月/日)

    private BigDecimal width;            //商品寬度
    private BigDecimal height;           //商品高度
    private BigDecimal depth;            //商品深度

    //封裝 計算這一筆商品的小計金額 (單價*數量) 方法
    public void calculateTotal() {
        if (unitPrice != null) {
            total = unitPrice.multiply(BigDecimal.valueOf(quantity));
        } else {
            total = BigDecimal.ZERO;
        }
    }

    //封裝 運費計算 方法
    public BigDecimal calculateVolume() {
        if (width != null && height != null && depth != null) {
            return width.multiply(height).multiply(depth).multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }
}
