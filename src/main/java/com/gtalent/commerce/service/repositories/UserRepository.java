package com.gtalent.commerce.service.repositories;

import com.gtalent.commerce.service.models.Segment;
import com.gtalent.commerce.service.models.User;
import com.gtalent.commerce.service.models.UserSegment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository  //直接操作資料庫 (CRUD)
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByEmail(String email);  //為了POST方法只是要檢查 Email 是否重複 (Service)
    boolean existsByEmailAndIdNot(String email, int id);  //為了PUT方法只是要檢查 Email 是否重複 (Service)
    Optional<User> findByEmail(String email);  //用"信箱"與"密碼"登入
}
