package com.gtalent.commerce.service.services;

import com.gtalent.commerce.service.models.Category;
import com.gtalent.commerce.service.repositories.CategoryRepository;
import com.gtalent.commerce.service.requests.CreateCategoryRequest;
import com.gtalent.commerce.service.responses.CategoryResponse;
import com.gtalent.commerce.service.responses.ProductListResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    //1.查詢所有分類 (包含分類下的產品列表)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .filter(category -> category.getDeletedAt() == null)  //過濾掉已經被刪除的資料
                .map(category -> {
                    CategoryResponse response = new CategoryResponse();
                    response.setId(category.getId());
                    response.setName(category.getName());

                    //轉換分類下產品為 DTO
                    List<ProductListResponse> productList = category.getProductList()
                            .stream()
                            .map(product -> {
                                ProductListResponse products = new ProductListResponse();
                                products.setId(product.getId());
                                products.setImageUrl(product.getImageUrl());
                                products.setThumbnailUrl(product.getThumbnailUrl());
                                products.setReference(product.getReference());
                                products.setPrice(product.getPrice());
                                products.setStock(product.getStock());
                                return products;
                            })
                            .toList();
                    response.setProducts(productList);
                    return response;
                })
                .toList();
    }

    //2.建立分類
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        //必填欄位檢查
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "分類名稱必填");
        }
        //名稱不可重複
        if (categoryRepository.existsByName(request.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "分類名稱已存在");
        }
        Category category = new Category();
        category.setName(request.getName());
        categoryRepository.save(category);  //存進資料庫

        //回傳 DTO
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setProducts(Collections.emptyList());
        /* response.setProducts(Collections.emptyList());
           目的 -> 預設一個空列表，就算沒有產品，也可以保證 products 一定是陣列*/
        return response;
    }

    //3.軟刪除分類
    public void softDeleteCategory(int id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "分類不存在"));
        //判斷是否已經刪除
        if (category.getDeletedAt() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "分類已被刪除");
        }

        category.setDeletedAt(LocalDateTime.now());
        categoryRepository.save(category);
    }

}
