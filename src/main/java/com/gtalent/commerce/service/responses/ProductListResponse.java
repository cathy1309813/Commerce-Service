package com.gtalent.commerce.service.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductListResponse {
    private int id;
    private String imageUrl;
    private String thumbnailUrl;
    private String reference;
    private BigDecimal price;
    private int stock;
    private CategoryResponse category; //分類名稱 + 圖示
}
