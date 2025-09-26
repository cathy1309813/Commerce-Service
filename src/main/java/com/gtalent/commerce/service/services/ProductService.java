package com.gtalent.commerce.service.services;

import com.gtalent.commerce.service.models.Category;
import com.gtalent.commerce.service.models.Product;

import com.gtalent.commerce.service.repositories.CategoryRepository;
import com.gtalent.commerce.service.repositories.ProductRepository;
import com.gtalent.commerce.service.requests.CreateProductRequest;
import com.gtalent.commerce.service.responses.CategoryResponse;
import com.gtalent.commerce.service.responses.ProductDetailResponse;
import com.gtalent.commerce.service.responses.ProductListResponse;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
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

    //1.1 查詢所有產品 (分頁)
    public Page<Product> getAllProductPages(String query, Integer categoryId, Integer stockFrom, Integer stockTo,
                                            Pageable pageable) {
        Specification<Product> spec = productSpecification(query, categoryId, stockFrom, stockTo);
        return productRepository.findAll(spec, pageable);
    }

    private Specification<Product> productSpecification(String queryName, Integer categoryId, Integer stockFrom,
                                                        Integer stockTo) {
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(queryName != null && !queryName.isEmpty()) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("reference")), "%"+ queryName.toLowerCase()+"%")
                ));
            }
            //分類 ID 篩選
            if(categoryId != null) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), categoryId));
            }
            //庫存區間
            if (stockFrom != null && stockTo != null) {
                predicates.add(criteriaBuilder.between(root.get("stock"), stockFrom, stockTo));
            } else if (stockFrom != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("stock"), stockFrom));
            } else if (stockTo != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("stock"), stockTo));
            }

            Predicate[] predicateArray = predicates.toArray(new Predicate[0]);
            return criteriaBuilder.and(predicateArray);
        });
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
    public ProductDetailResponse createProduct(CreateProductRequest request) {
        Product product = new Product();
        product.setReference(request.getReference());
        product.setWidth(BigDecimal.valueOf(request.getWidth()));
        product.setHeight(BigDecimal.valueOf(request.getHeight()));
        product.setDepth(BigDecimal.valueOf(request.getDepth()));
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setSales(request.getSales());
        product.setDescription(request.getDescription());
        product.setThumbnailUrl(request.getThumbnailUrl());

        if(request.getCategory() != null) {
            Category category = categoryRepository.findById(request.getCategory())
                    .orElseThrow(() -> new RuntimeException("無此分類"));
            product.setCategory(category);
        }

        Product createdProduct = productRepository.save(product);

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
        response.setHeight(createdProduct.getDepth());

        if (createdProduct.getCategory() != null) {
            CategoryResponse categoryResponse = new CategoryResponse();
            categoryResponse.setId(createdProduct.getCategory().getId());
            categoryResponse.setName(createdProduct.getCategory().getName());
            response.setCategory(categoryResponse);
        }
        return response;
    }

    //4.更新產品
    public ProductDetailResponse updateProduct(int id, CreateProductRequest request) {
        //1.根據 ID 取得現有產品(若不存在則拋出例外)
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("無此產品"));
        //2.更新產品欄位
        product.setReference(request.getReference());
        product.setWidth(BigDecimal.valueOf(request.getWidth()));
        product.setHeight(BigDecimal.valueOf(request.getHeight()));
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setSales(request.getSales());
        product.setDescription(request.getDescription());
        product.setThumbnailUrl(request.getThumbnailUrl());

        //3.更新分類
        if (request.getCategory() != null) {
            Category category = categoryRepository.findById(request.getCategory())
                    .orElseThrow(() -> new RuntimeException("無此分類"));
            product.setCategory(category);
        }
        //4.儲存更新後的產品
        Product updatedProduct = productRepository.save(product);

        //5.將更新後的產品轉成 DTO 回傳，而不是直接回傳 Entity
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

        //6.設定分類 DTO
        if (updatedProduct.getCategory() != null) {
            CategoryResponse categoryResponse = new CategoryResponse();
            categoryResponse.setId(updatedProduct.getCategory().getId());
            categoryResponse.setName(updatedProduct.getCategory().getName());
            response.setCategory(categoryResponse);
        }
        return response;
    }

    //5.刪除產品
    public void deleteProduct(int id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty()) {
            throw new RuntimeException("產品不存在");
        }
        Product product = optionalProduct.get();
        productRepository.delete(product);
    }


}
