package com.gtalent.commerce.service.controllers;

import com.gtalent.commerce.service.requests.CreateCategoryRequest;
import com.gtalent.commerce.service.responses.CategoryResponse;
import com.gtalent.commerce.service.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/commerce-service/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    //1.查詢所有分類
    @GetMapping
    public List<CategoryResponse> getAllCategories() {
        return categoryService.getAllCategories();
    }

    //2.建立分類
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        CategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);  //201 Created
    }

    //3.軟刪除分類
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteCategory(@PathVariable int id) {
        categoryService.softDeleteCategory(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();  //204 No Content
    }


}
