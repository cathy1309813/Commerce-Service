package com.gtalent.commerce.service.repositories;

import com.gtalent.commerce.service.models.Segment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SegmentRepository extends JpaRepository<Segment, Integer> {

    boolean existsByName(String name);  //判斷資料庫中是否已存在該 segment
}
