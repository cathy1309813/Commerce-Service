package com.gtalent.commerce.service.services;

import com.gtalent.commerce.service.models.Category;
import com.gtalent.commerce.service.models.Product;
import com.gtalent.commerce.service.models.User;
import com.gtalent.commerce.service.repositories.CategoryRepository;
import com.gtalent.commerce.service.repositories.ProductRepository;
import com.gtalent.commerce.service.requests.CreateProductRequest;
import com.gtalent.commerce.service.responses.CategoryResponse;
import com.gtalent.commerce.service.responses.ProductDetailResponse;
import com.gtalent.commerce.service.responses.ProductListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    public ProductService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    //1.查詢所有產品 (列表用)
    public List<ProductListResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(product -> {
                    // 宣告 DTO
                    ProductListResponse response = new ProductListResponse();
                    response.setId(product.getId());
                    response.setImageUrl(product.getImageUrl());
                    response.setThumbnailUrl(product.getThumbnailUrl());
                    response.setReference(product.getReference());
                    response.setPrice(product.getPrice());
                    response.setStock(product.getStock());

                    //設定分類 DTO
                    if (product.getCategory() != null) {
                        CategoryResponse categoryResponse = new CategoryResponse();
                        categoryResponse.setId(product.getCategory().getId());
                        categoryResponse.setName(product.getCategory().getName());
                        response.setCategory(categoryResponse);
                    }

                    return response;
                })
                .toList();
    }

    //2.查詢單一產品 (詳細產品資訊)
    public ProductDetailResponse getProductById(int id) {  //Optional<> :回傳值可能不存在
        Optional<Product> optionalProduct = productRepository.findById(id);
        //若不存在先回傳Product not found
        if (optionalProduct.isEmpty()) {
            throw new RuntimeException("Product not found");
        }
        Product product = optionalProduct.get();  // 取出 Product

        //將 Product 轉成 ProductDetailResponse
        ProductDetailResponse response = new ProductDetailResponse();
        response.setId(product.getId());
        response.setImageUrl(product.getImageUrl());
        response.setThumbnailUrl(product.getThumbnailUrl());
        response.setReference(product.getReference());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());
        response.setDescription(product.getDescription());
        response.setWidth(product.getWidth());
        response.setHeight(product.getHeight());

        //設定分類資訊
        if (product.getCategory() != null) {
            //建立一個新的 CategoryResponse 物件
            CategoryResponse categoryResponse = new CategoryResponse();
            //設定 id 和 name
            categoryResponse.setId(product.getCategory().getId());
            categoryResponse.setName(product.getCategory().getName());
            //把這個分類 DTO 設定到 ProductDetailResponse 的 category 欄位
            response.setCategory(categoryResponse);
        }

        return response;
    }

    //3.新增產品
    public Product createProduct(CreateProductRequest request) {
        Product product = new Product();
        product.setReference(request.getReference());
        product.setWidth(BigDecimal.valueOf(request.getWidth()));
        product.setHeight(BigDecimal.valueOf(request.getHeight()));
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setSales(request.getSales());
        product.setDescription(request.getDescription());
        product.setThumbnailUrl(request.getThumbnail());

        if(request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("無此分類"));
            product.setCategory(category);
        }

        return productRepository.save(product);
    }

    //4.更新產品
    public Product updateProduct(int id, CreateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("無此產品"));
        product.setReference(request.getReference());
        product.setWidth(BigDecimal.valueOf(request.getWidth()));
        product.setHeight(BigDecimal.valueOf(request.getHeight()));
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setSales(request.getSales());
        product.setDescription(request.getDescription());
        product.setThumbnailUrl(request.getThumbnail());

        //更新分類
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("無此分類"));
            product.setCategory(category);
        }
        return productRepository.save(product);
    }

    //5.刪除產品

}
