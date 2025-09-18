package com.gtalent.commerce.service.controllers;

import com.gtalent.commerce.service.requests.CreateCategoryRequest;
import com.gtalent.commerce.service.responses.CategoryResponse;
import com.gtalent.commerce.service.services.CategoryService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Category 功能-第一版", description = "提供分類列表查詢、新增與軟刪除功能")
@RestController
@RequestMapping("/commerce-service/categories")
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    //1.查詢所有分類 (包含分類下的產品列表)
    @Operation(summary = "取得所有分類", description = "回傳分類及分類底下產品資訊",
               security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功取得分類列表"),
            @ApiResponse(responseCode = "500", description = "系統錯誤")
    })
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    //2.建立分類
    @PostMapping
    @Operation(summary = "建立分類", description = "新增一個分類")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "成功建立分類"),
            @ApiResponse(responseCode = "400", description = "欄位驗證失敗"),
            @ApiResponse(responseCode = "500", description = "系統錯誤")
    })
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        CategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);  //201 Created
    }

    //3.軟刪除分類
    @DeleteMapping("/{id}")
    @Operation(summary = "刪除分類", description = "軟刪除指定分類")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "分類刪除成功"),
            @ApiResponse(responseCode = "404", description = "找不到分類"),
            @ApiResponse(responseCode = "500", description = "系統錯誤")
    })
    public ResponseEntity<Void> softDeleteCategory(@PathVariable int id) {
        categoryService.softDeleteCategory(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();  //204 No Content
    }


}
