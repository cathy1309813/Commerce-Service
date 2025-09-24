package com.gtalent.commerce.service.services;

import com.gtalent.commerce.service.dto.CustomerDto;
import com.gtalent.commerce.service.dto.ProductDto;
import com.gtalent.commerce.service.enums.ReviewStatus;
import com.gtalent.commerce.service.models.Product;
import com.gtalent.commerce.service.models.Review;
import com.gtalent.commerce.service.models.User;
import com.gtalent.commerce.service.repositories.ProductRepository;
import com.gtalent.commerce.service.repositories.ReviewRepository;
import com.gtalent.commerce.service.repositories.UserRepository;
import com.gtalent.commerce.service.requests.CreateReviewRequest;
import com.gtalent.commerce.service.requests.UpdateReviewRequest;
import com.gtalent.commerce.service.responses.ReviewResponse;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;


    //1.取得某產品的所有評論 (分頁)
    public Page<ReviewResponse> getProductReviews(int productId, int page, int size, String sort, String direction,
            String status, Integer ratingMin, Integer ratingMax, String q
    ) {
        //1.設定排序
        Sort sortObj = Sort.by("createdAt").descending(); // 預設
        if (sort != null) {
            Sort.Direction dir = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
            switch (sort) {
                case "date":
                    sortObj = Sort.by(dir, "date");
                    break;
                case "rating":
                    sortObj = Sort.by(dir, "rating");
                    break;
                case "comment":
                    sortObj = Sort.by(dir, "comment");
                    break;
                case "status":
                    sortObj = Sort.by(dir, "status");
                    break;
                case "customer":
                    sortObj = Sort.by(dir, "customer.name"); //關聯欄位排序
                    break;
                case "product":
                    sortObj = Sort.by(dir, "product.name"); //關聯欄位排序
                    break;
            }
        }
        Pageable pageable = PageRequest.of(page, size, sortObj);

        //2.建立過濾條件 (Specification)
        Specification<Review> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("product").get("id"), productId));
            predicates.add(cb.isNull(root.get("deletedAt")));  //軟刪除過濾

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), ReviewStatus.valueOf(status)));
            }
            if (ratingMin != null) {
                predicates.add(cb.ge(root.get("rating"), ratingMin));
            }
            if (ratingMax != null) {
                predicates.add(cb.le(root.get("rating"), ratingMax));
            }
            if (q != null && !q.isEmpty()) {
                predicates.add(cb.like(root.get("comment"), "%" + q + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        //3.查詢
        Page<Review> reviewsPage = reviewRepository.findAll(spec, pageable);
        //4.轉成 DTO
        return reviewsPage.map(this::mapToResponse);
    }

    //2.新增評論
    public ReviewResponse createReview(CreateReviewRequest request) {
        //1.先確認產品
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("無此產品"));
        //2.再確認用戶
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("無此用戶"));
        //3.建立評論
        Review review = new Review();
        review.setProduct(product);
        review.setCustomer(user);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review.setStatus(ReviewStatus.PENDING);  //預設待審核
        review.setDate(LocalDate.now());        //系統自動設定評論日期

        //4.儲存
        Review createdReview = reviewRepository.save(review);

        //5.回傳 Response DTO
        return mapToResponse(createdReview);
    }

    //3.編輯評論 (更新狀態、內容、評分)
    public ReviewResponse updateReview(int id, UpdateReviewRequest request) {
        //1.根據 ID 取得現有評論(若不存在則拋出例外)
        Review review = reviewRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("評論不存在"));
        //2.更新非 null 欄位
        if (request.getRating() != null) {
            review.setRating(request.getRating());
        }
        if (request.getComment() != null) {
            review.setComment(request.getComment());
        }
        if (request.getStatus() != null) {
            review.setStatus(request.getStatus());
        }
        //3.儲存更新後的評論
        Review updatedReview = reviewRepository.save(review);
        //4.將更新後的產品轉成 DTO 回傳，而不是直接回傳 Entity
        return mapToResponse(updatedReview);
    }

    //4.刪除評論
    public void deleteReview(int id) {
        //1.找到未刪除的評論
        Optional<Review> optionalReview = reviewRepository.findByIdAndDeletedAtIsNull(id);
        if (optionalReview.isPresent()) {
            Review review = optionalReview.get();
            review.setDeletedAt(LocalDateTime.now());
            reviewRepository.save(review);
        } else {
            throw new RuntimeException("評論不存在或已刪除");
        }

    }
    //5.取得單筆評論詳細
    public Optional<Review> findById(int reviewId) {
        return reviewRepository.findById(reviewId);
    }


    private ReviewResponse mapToResponse(Review review) {
        // 建立 CustomerDto
        CustomerDto customerDto = new CustomerDto(
                review.getCustomer().getId(),
                review.getCustomer().getFirstName(),
                review.getCustomer().getLastName()
        );

        // 建立 ProductDto（只包含 ID）
        ProductDto productDto = new ProductDto(review.getProduct().getId());

        // 建立 ReviewResponse
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setCustomer(customerDto);   // 改成設定 DTO
        response.setProduct(productDto);     // 改成設定 DTO
        response.setStatus(review.getStatus().name());
        response.setDate(review.getDate());
        response.setCreatedAt(review.getCreatedAt());

        return response;
    }
    /* Method Extraction 方法抽取
       把長方法裡的一段程式碼 (例如建立 ReviewResponse 的那部分) 抽取成 獨立方法，
       讓程式更清晰、可讀性更高，也更容易重複使用。*/
}
