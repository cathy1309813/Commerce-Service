package com.gtalent.commerce.service.controllers;

import com.gtalent.commerce.service.models.Product;
import com.gtalent.commerce.service.requests.CreateProductRequest;
import com.gtalent.commerce.service.responses.CategoryResponse;
import com.gtalent.commerce.service.responses.ProductDetailResponse;
import com.gtalent.commerce.service.responses.ProductListResponse;
import com.gtalent.commerce.service.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "Product 功能-第一版", description = "提供產品相關 API")
@RestController
@RequestMapping("/commerce-service/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    //1.查詢所有產品
    @GetMapping
    @Operation(summary = "取得所有產品列表", description = "回傳產品簡略資訊，包含圖片、縮圖、參考號、價格、庫存及分類")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功取得產品列表"),
            @ApiResponse(responseCode = "500", description = "系統錯誤")
    })
    public ResponseEntity<List<ProductListResponse>> getAllProducts() {
        //從 Service 取得
        List<ProductListResponse> productList = productService.getAllProducts();
        return ResponseEntity.ok(productList);
    }

    //2.查詢單一產品
    @GetMapping("/{id}")
    @Operation(summary = "取得單一產品", description = "依照產品 ID 回傳完整產品資訊")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功取得產品資訊"),
            @ApiResponse(responseCode = "404", description = "產品不存在"),
            @ApiResponse(responseCode = "500", description = "系統錯誤")
    })
    public ResponseEntity<ProductDetailResponse> getProductById(@PathVariable int id) {
        ProductDetailResponse response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    //3.新增產品
    @PostMapping
    @Operation(summary = "新增產品", description = "建立新的產品，需提供完整產品資訊")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "產品新增成功"),
            @ApiResponse(responseCode = "400", description = "欄位驗證失敗"),
            @ApiResponse(responseCode = "500", description = "系統錯誤")
    })
    public ResponseEntity<ProductDetailResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        ProductDetailResponse createdProduct = productService.createProduct(request);

        ProductDetailResponse response = new ProductDetailResponse();
        response.setId(createdProduct.getId());
        response.setReference(createdProduct.getReference());
        response.setPrice(createdProduct.getPrice());
        response.setStock(createdProduct.getStock());
        response.setSales(createdProduct.getSales());
        response.setDescription(createdProduct.getDescription());
        response.setThumbnailUrl(createdProduct.getThumbnailUrl());
        response.setWidth(createdProduct.getWidth());
        response.setHeight(createdProduct.getHeight());

        if (createdProduct.getCategory() != null) {
            CategoryResponse categoryResponse = new CategoryResponse();
            categoryResponse.setId(createdProduct.getCategory().getId());
            categoryResponse.setName(createdProduct.getCategory().getName());
            response.setCategory(categoryResponse);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //4.更新產品
    @PutMapping("/{id}")
    @Operation(summary = "更新產品", description = "更新指定產品資訊")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "產品更新成功"),
            @ApiResponse(responseCode = "400", description = "欄位驗證失敗"),
            @ApiResponse(responseCode = "404", description = "找不到產品"),
            @ApiResponse(responseCode = "500", description = "系統錯誤")
    })
    public ResponseEntity<ProductDetailResponse> updateProduct(@PathVariable int id,
                                                 @Valid @RequestBody CreateProductRequest request) {
        ProductDetailResponse updatedProduct = productService.updateProduct(id, request);

        ProductDetailResponse response = new ProductDetailResponse();
        response.setId(updatedProduct.getId());
        response.setReference(updatedProduct.getReference());
        response.setPrice(updatedProduct.getPrice());
        response.setStock(updatedProduct.getStock());
        response.setSales(updatedProduct.getSales());
        response.setDescription(updatedProduct.getDescription());
        response.setThumbnailUrl(updatedProduct.getThumbnailUrl());
        response.setWidth(updatedProduct.getWidth());
        response.setHeight(updatedProduct.getHeight());

        if (updatedProduct.getCategory() != null) {
            CategoryResponse categoryResponse = new CategoryResponse();
            categoryResponse.setId(updatedProduct.getCategory().getId());
            categoryResponse.setName(updatedProduct.getCategory().getName());
            response.setCategory(categoryResponse);
        }

        return ResponseEntity.ok(response);
    }

    //5.刪除產品
    @DeleteMapping("/{id}")
    @Operation(summary = "刪除產品", description = "刪除指定產品")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "產品刪除成功"),
            @ApiResponse(responseCode = "404", description = "找不到產品"),
            @ApiResponse(responseCode = "500", description = "系統錯誤")
    })
    public ResponseEntity<Void> deleteProduct(@PathVariable int id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();  //204 No Content
    }
}
