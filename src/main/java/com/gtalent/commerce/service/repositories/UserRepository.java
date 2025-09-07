package com.gtalent.commerce.service.repositories;

import com.gtalent.commerce.service.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository  //直接操作資料庫 (CRUD)
public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    boolean existsByEmail(String email);  //為了POST方法只是要檢查 Email 是否重複 (Service)
    boolean existsByEmailAndIdNot(String email, int id);  //為了PUT方法只是要檢查 Email 是否重複 (Service)
    Optional<User> findByEmail(String email);  //用"信箱"與"密碼"登入

    Page<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName, String lastName, Pageable pageable);
    Page<User> findByHasNewsletter(Boolean hasNewsletter, Pageable pageable);
    Page<User> findByUserSegments_Segment_Id(Integer segmentId, Pageable pageable);

    /* Page<T>
       是 Spring Data JPA 提供的介面，用來表示"User資料的分頁結果"，內容包括：
       1.當前頁資料 → getContent()
       2.總筆數 → getTotalElements()
       3.總頁數 → getTotalPages()
       4.分頁資訊 → getPageable()*/
    /* 方法名稱 findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase
       1.findBy ->代表這是一個查詢方法，後面接的字串為查詢條件。
       2.FirstNameContainingIgnoreCase -> FirstName:對應 User entity 欄位 firstName;
                                          Containing:SQL 的 LIKE %keyword%，代表模糊匹配;
                                          IgnoreCase → 忽略大小寫。
       3.Or -> 代表 SQL 中的 OR。
       4.LastNameContainingIgnoreCase -> 同FirstNameContainingIgnoreCase。*/
}
