package com.gtalent.commerce.service.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailResponse {
    private int id;
    private String imageUrl;
    private String thumbnailUrl;
    private String reference;
    private BigDecimal width;
    private BigDecimal height;
    private BigDecimal price;
    private int stock;
    private int sales;
    private String description;
    //分類資訊
    //讓前端知道該產品屬於哪個分類
    //這樣就不需要將整個 Category Entity 回傳
    private CategoryResponse category;
}
