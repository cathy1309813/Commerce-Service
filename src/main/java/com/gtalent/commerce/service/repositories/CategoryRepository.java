package com.gtalent.commerce.service.repositories;

import com.gtalent.commerce.service.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

//提供最基本的 CRUD 操作（查詢、儲存、刪除）
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    boolean existsByName(String name);
}
