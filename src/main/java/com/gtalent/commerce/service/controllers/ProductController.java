package com.gtalent.commerce.service.controllers;

import com.gtalent.commerce.service.models.Product;
import com.gtalent.commerce.service.requests.CreateProductRequest;
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
    public ProductDetailResponse getProductById(@PathVariable int id) {
        return productService.getProductById(id);
    }

    //3.新增產品
    @PostMapping
    @Operation(summary = "新增產品", description = "建立新的產品，需提供完整產品資訊")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "產品新增成功"),
            @ApiResponse(responseCode = "400", description = "欄位驗證失敗"),
            @ApiResponse(responseCode = "500", description = "系統錯誤")
    })
    public ResponseEntity<Product> createProduct(@Valid @RequestBody CreateProductRequest request) {
        Product createdProduct = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
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
    public ResponseEntity<Product> updateProduct(@PathVariable int id,
                                                 @Valid @RequestBody CreateProductRequest request) {
        Product updatedProduct = productService.updateProduct(id, request);
        return ResponseEntity.ok(updatedProduct);
    }

    //5.刪除產品
}
