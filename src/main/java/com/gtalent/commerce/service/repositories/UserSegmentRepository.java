package com.gtalent.commerce.service.repositories;


import com.gtalent.commerce.service.models.User;
import com.gtalent.commerce.service.models.UserSegment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserSegmentRepository extends JpaRepository<UserSegment, Integer> {
    List<UserSegment> findBySegmentId(int segmentId);  //取得某個 Segment 對應的使用者
    Optional<UserSegment> findByUserIdAndSegmentId(int userId, int segmentId);  //取得特定使用者與 Segment 的關聯（用於新增或刪除前檢查）
}
