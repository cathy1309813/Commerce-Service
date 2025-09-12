package com.gtalent.commerce.service.repositories;

import com.gtalent.commerce.service.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

//提供最基本的 CRUD 操作（查詢、儲存、刪除）
public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {
}
