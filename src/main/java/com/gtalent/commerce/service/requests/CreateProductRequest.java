package com.gtalent.commerce.service.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductRequest {
    @NotNull(message = "Reference 必填")
    private String reference;

    @NotNull(message = "Width 必填")
    private Double width;

    @NotNull(message = "Height 必填")
    private Double height;

    @NotNull(message = "Price 必填")
    private BigDecimal price;

    @NotNull(message = "Stock 必填")
    private Integer stock;

    @NotNull(message = "Sales 必填")
    private Integer sales;

    @NotNull(message = "Description 必填")
    private String description;

    @NotNull(message = "Image 必填")
    private String images;

    @NotNull(message = "Thumbnail 必填")
    private String thumbnail;

    @NotNull(message = "CategoryId 必填")
    private Integer categoryId;  //新增欄位
}
