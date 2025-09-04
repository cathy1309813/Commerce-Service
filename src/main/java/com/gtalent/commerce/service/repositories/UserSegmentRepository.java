package com.gtalent.commerce.service.repositories;


import com.gtalent.commerce.service.models.User;
import com.gtalent.commerce.service.models.UserSegment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserSegmentRepository extends JpaRepository<UserSegment, Integer> {
    List<UserSegment> findByUserId(int userId); // 取得某個使用者對應的 segment
}
