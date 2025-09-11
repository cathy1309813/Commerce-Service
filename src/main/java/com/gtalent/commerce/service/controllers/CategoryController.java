package com.gtalent.commerce.service.controllers;

import com.gtalent.commerce.service.requests.CreateCategoryRequest;
import com.gtalent.commerce.service.responses.CategoryResponse;
import com.gtalent.commerce.service.services.CategoryService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "分類功能-第一版", description = "提供分類列表查詢、新增與軟刪除功能")
@RestController
@RequestMapping("/commerce-service/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    //1.查詢所有分類
    @GetMapping
    @Operation(summary = "取得所有分類",
            description = "回傳所有分類清單，不包含已軟刪除的分類")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功取得分類清單"),
            @ApiResponse(responseCode = "400", description = "請求參數錯誤")
    })
    public List<CategoryResponse> getAllCategories() {
        return categoryService.getAllCategories();
    }

    //2.建立分類
    @PostMapping
    @Operation(summary = "建立新分類",
            description = "依據 CreateCategoryRequest 建立新的分類，名稱不可重複")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "成功建立分類"),
            @ApiResponse(responseCode = "400", description = "參數錯誤或名稱重複")
    })
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        CategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);  //201 Created
    }

    //3.軟刪除分類
    @DeleteMapping("/{id}")
    @Operation(summary = "軟刪除分類",
            description = "依分類 ID 將分類標記為已刪除，不會實際移除資料庫資料")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "成功軟刪除分類"),
            @ApiResponse(responseCode = "404", description = "分類不存在")
    })
    public ResponseEntity<Void> softDeleteCategory(@PathVariable int id) {
        categoryService.softDeleteCategory(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();  //204 No Content
    }


}
