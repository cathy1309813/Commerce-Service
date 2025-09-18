package com.gtalent.commerce.service.controllers;

import com.gtalent.commerce.service.models.Product;
import com.gtalent.commerce.service.requests.CreateProductRequest;
import com.gtalent.commerce.service.responses.*;
import com.gtalent.commerce.service.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@Tag(name = "Product 功能-第一版", description = "提供產品相關 API")
@RestController
@RequestMapping("/commerce-service/products")
@SecurityRequirement(name = "bearerAuth")
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

    //1.1 查詢所有產品 (分頁)
    @GetMapping("/page/{page}")
    @Operation(summary = "取得產品清單（分頁 + 搜尋）",
            description = "可依產品名稱、分類、庫存篩選、銷售狀態過濾，並支援分頁與排序")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功取得分頁產品清單"),
            @ApiResponse(responseCode = "400", description = "輸入錯誤"),
            @ApiResponse(responseCode = "500", description = "伺服器內部錯誤")
    })
    public Page<ProductListResponse> getAllProductPages(
            @RequestParam(defaultValue = "0") int page,  //要查詢的頁碼
            @RequestParam(defaultValue = "10") int size,  //每頁要顯示的筆數
            @RequestParam(defaultValue = "") String query,  //搜尋 名稱關鍵字
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer stockFrom,
            @RequestParam(required = false) Integer stockTo
    ) {
        //建立 Pageable 物件，用於分頁查詢
        Pageable pageable = PageRequest.of(page, size);
        //呼叫 service，返回 Page<Product>
        Page<Product> productsPage = productService.getAllProductPages(query, categoryId, stockFrom, stockTo, pageable);
        //將 Page<Product> 轉成 Page<ProductListResponse>
        return productsPage.map(this::toResponse);  //直接用 map 轉 DTO
    }
    private ProductListResponse toResponse(Product product) {
        CategoryResponse category = new CategoryResponse();
        category.setId(product.getCategory().getId());
        category.setName(product.getCategory().getName());

        return ProductListResponse.builder()
                .id(product.getId())
                .reference(product.getReference())
                .imageUrl(product.getImageUrl())
                .thumbnailUrl(product.getThumbnailUrl())
                .price(product.getPrice())
                .stock(product.getStock())
                .category(category)
                .build();
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
