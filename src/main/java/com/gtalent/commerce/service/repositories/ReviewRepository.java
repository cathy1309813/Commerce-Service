package com.gtalent.commerce.service.repositories;

import com.gtalent.commerce.service.models.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Integer>, JpaSpecificationExecutor<Review> {
    Optional<Review> findByIdAndDeletedAtIsNull(int id);
    Page<Review> findByProductIdAndDeletedAtIsNull(int productId, Pageable pageable);  //查詢某產品所有未刪除的評論
}
