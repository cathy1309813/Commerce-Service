package com.gtalent.commerce.service.controllers;

import com.gtalent.commerce.service.requests.CreateReviewRequest;
import com.gtalent.commerce.service.requests.UpdateReviewRequest;
import com.gtalent.commerce.service.responses.ReviewResponse;
import com.gtalent.commerce.service.services.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Review 功能-第一版", description = "提供評論的 CRUD 及分頁查詢功能")
@RestController
@RequestMapping("/commerce-service/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    //1.取得某產品的所有評論 (分頁 + 排序 + 過濾)
    @Operation(summary = "取得某產品的所有評論",
            description = "可依評分、狀態、評論內容關鍵字篩選，並支援分頁與排序")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功取得評論列表"),
            @ApiResponse(responseCode = "404", description = "產品不存在")
    })
    @GetMapping("/products/{productId}")
    public ResponseEntity<Page<ReviewResponse>> getProductReviewPages(
            @Parameter(description = "產品ID", required = true) @PathVariable Integer productId,
            @Parameter(description = "頁碼，從0開始") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁筆數") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序欄位，例如 date/rating/comment/status/customer/product")
                    @RequestParam(required = false) String sort,  //排序欄位
            @Parameter(description = "排序方向：asc/desc") @RequestParam(required = false) String direction, // asc / desc
            @Parameter(description = "評論狀態") @RequestParam(required = false) String status,
            @Parameter(description = "最低評分") @RequestParam(required = false) Integer ratingMin,
            @Parameter(description = "最高評分") @RequestParam(required = false) Integer ratingMax,
            @Parameter(description = "評論內容關鍵字") @RequestParam(required = false) String q
    ) {
        Page<ReviewResponse> reviews = reviewService.getProductReviews(
                productId, page, size, sort, direction, status, ratingMin, ratingMax, q);
        return ResponseEntity.ok(reviews);
    }

    //2.新增評論
    @Operation(summary = "新增評論", description = "建立新的產品評論")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功建立評論"),
            @ApiResponse(responseCode = "404", description = "產品或使用者不存在")
    })
    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@Parameter(description = "新增評論資料", required = true)
                                                       @RequestBody CreateReviewRequest request) {
        ReviewResponse response = reviewService.createReview(request);
        return ResponseEntity.ok(response);
    }

    //3.編輯評論 (更新狀態、內容、評分)
    @Operation(summary = "更新評論", description = "可更新評論內容、評分與狀態")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功更新評論"),
            @ApiResponse(responseCode = "404", description = "評論不存在或已刪除")
    })
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @Parameter(description = "評論ID", required = true) @PathVariable int reviewId,
            @Parameter(description = "更新評論資料", required = true) @RequestBody UpdateReviewRequest request) {
        ReviewResponse response = reviewService.updateReview(reviewId, request);
        return ResponseEntity.ok(response);
    }

    //4.刪除評論
    @Operation(summary = "刪除評論", description = "軟刪除評論，設定 deletedAt 時間")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "成功刪除評論"),
            @ApiResponse(responseCode = "404", description = "評論不存在或已刪除")
    })
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @Parameter(description = "評論ID", required = true) @PathVariable int reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();  //204
    }
}
