package com.gtalent.commerce.service.services;


import com.gtalent.commerce.service.models.UserSegment;
import com.gtalent.commerce.service.responses.LoginResponse;
import com.gtalent.commerce.service.responses.UserResponse;
import com.gtalent.commerce.service.models.User;
import com.gtalent.commerce.service.repositories.UserRepository;

import com.gtalent.commerce.service.responses.UserSegmentResponse;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service  //處理商業邏輯 → 呼叫 Repository
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;  //1.在"pom.xml"中注入依賴 2.在configs中加入SecurityConfig並新增@Bean
    private JwtService jwtService;

    //注入建構子
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    //1.取得所有使用者
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> {
                    UserResponse ur = new UserResponse();
                    ur.setId(user.getId());
                    ur.setFirstName(user.getFirstName());
                    ur.setLastName(user.getLastName());
                    ur.setHasNewsletter(user.getHasNewsletter());

                    // 建立 segments 清單
                    List<UserSegmentResponse> segmentResponses = new ArrayList<>();
                    for (UserSegment us : user.getUserSegments()) {
                        segmentResponses.add(new UserSegmentResponse(us));
                    }  //遍歷使用者的每個 UserSegment → 轉成 DTO，加入 List
                    ur.setSegments(segmentResponses);  //把 List 放入 UserResponse 的 segments 欄位
                    return ur;
                })
                .toList();
    }

    //1.1 取得使用者(分頁)
    public Page<User> getAllUserPages(String query, Boolean hasNewsletter, Integer segmentId, Pageable pageable) {
        Specification<User> spec = userSpecification(query, hasNewsletter, segmentId);
        return userRepository.findAll(spec, pageable);
    }
    /* query：
       關鍵字 (可為 null 或空字串 "")，用來模糊搜尋 firstName/lastName。*/
    /* hasNewsletter：
       布林過濾 (true / false / null)。null 表示不套用此條件。*/
    /* segmentId：
       數字過濾 (null 表示不套用)。通常會 JOIN userSegments 去比對 segment.id。*/
    /* pageable：
       分頁與排序資訊 (由 Controller 產生，通常 PageRequest.of(page, size, sort))。*/
    /* 建立 Specification：
       Specification<User> spec = userSpecification(...) 呼叫 userSpecification，
       回傳一個 Lambda（或匿名類別），其核心為 toPredicate(root, query, criteriaBuilder)。
       這個 Specification 在被傳給 userRepository.findAll(spec, pageable) 時，
       會被 JPA（Hibernate）用來建立 CriteriaQuery。*/

    private Specification<User> userSpecification(String queryName, Boolean hasNewsletter, Integer segmentId) {
        /* 回傳值：
           Specification<User> —— 一個可以被 Spring Data JPA 用來產生 WHERE 條件的物件（實作為 lambda）。
           參數：
           queryName -> 文字關鍵字，用來模糊搜尋（例如名字、姓氏、Email 等）。
           hasNewsletter -> Boolean，若為 null 表示不過濾；true/false 則添加對應過濾。
           segmentId -> Integer，若為 null 表示不過濾；有值則加入 JOIN 篩選。*/
        return ((root, query, criteriaBuilder) -> {
            //建立一個 predicates 列表
            List<Predicate> predicates = new ArrayList<>();
            //if predicates.size() = 3 how many "AND"? => 2
            //if predicates.size() = 8  how many "AND"? => 7

            if(queryName != null && !queryName.isEmpty()) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" +
                                queryName.toLowerCase() + "%"),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" +
                                queryName.toLowerCase() + "%")
                /* criteriaBuilder.lower(...)：
                   將欄位轉成小寫（DB 層面），配合 queryName.toLowerCase()，達到不分大小寫的搜尋。*/
                /* like(..., "%...%")：
                   左右都加 %，代表模糊搜尋（Anywhere match）。*/
                /* criteriaBuilder.or(...)：
                   把 firstName LIKE ... 與 lastName LIKE ... 用 OR 結合（符合任一即可）。*/
                ));
            }
            if(hasNewsletter != null) {
                predicates.add(criteriaBuilder.equal(root.get("hasNewsletter"), hasNewsletter));
                /* criteriaBuilder.equal(...)：
                   它是 Criteria API 提供的方法，用來產生 SQL 裡的 等號比對條件 (=)。*/
                /* root.get("hasNewsletter") 代表什麼？
                   root 代表查詢的主實體（這裡是 User）。
                   root.get("hasNewsletter") → 此表達式 = 指向 User 物件中的 hasNewsletter 欄位。*/
                /* hasNewsletter 參數代表什麼？
                   這是從方法裡傳進來的參數（Boolean 型別）。
                   hasNewsletter = true → 代表要找「有訂閱電子報」的使用者。
                   hasNewsletter = false → 代表要找「沒有訂閱電子報」的使用者。*/
            }
            if(segmentId != null) {
                //把 User 跟 UserSegment 做 join
                Join<User, UserSegment> userUserSegmentJoin = root.join("userSegments");
                predicates.add(criteriaBuilder.equal(userUserSegmentJoin.get("segment").get("id"), segmentId));
                /* criteriaBuilder.equal(...)：
                   產生 SQL 裡的 = 條件。*/
                /* userUserSegmentJoin.get("segment").get("id")：
                   此表達式 = 取出 segment 的 id。*/

                //如果 userSegment有 屬性segmentId 則可以直接使用
                //predicates.add(criteriaBuilder.equal(userUserSegmentJoin.get("segmentId"), segmentId));

                //如果欲查詢Segment參數為字串（name）=> segmentName
                //predicates.add(criteriaBuilder.equal(userUserSegmentJoin.get("segment").get("name"), segmentName)
            }
            Predicate[] predicatesArray = predicates.toArray(new Predicate[0]);
            //toArray(new Predicate[0]) → 把 List 轉成陣列
            return criteriaBuilder.and(predicatesArray);
            //criteriaBuilder.and(...) → 把多個條件用 AND 連接起來。*/
        });
    }



    //2.依照 ID 取得單一使用者
    public Optional<User> getUserById(int id) {  //Optional<> :回傳值可能不存在
        return userRepository.findById(id);
    }

    //3.新增使用者
    public User createUser(User user) {
        //所有欄位一定要填!
        if (user.getFirstName() == null || user.getLastName() == null || user.getEmail() == null ||
                user.getPassword() == null || user.getBirthday() == null || user.getAddress() == null ||
                user.getCity() == null || user.getState() == null || user.getZipcode() == null) {
            throw new IllegalArgumentException("所有欄位必填");
        }
        //Email是否重複
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER"); //預設為一般使用者
        return userRepository.save(user);
    }

    //4.更新使用者
    public Optional<User> updateUser(int id, User updatedUser) {
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()) {
            User existingUser = user.get(); //取得資料
            //更新欄位(不為空)
            if (updatedUser.getFirstName() != null) existingUser.setFirstName(updatedUser.getFirstName());
            if (updatedUser.getLastName() != null) existingUser.setLastName(updatedUser.getLastName());
            if (updatedUser.getEmail() != null) {
                if (userRepository.existsByEmailAndIdNot(updatedUser.getEmail(), id)) {
                    throw new IllegalArgumentException("Email 已存在");
                } existingUser.setEmail(updatedUser.getEmail());
            }
            if (updatedUser.getPassword() != null)
                existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            if (updatedUser.getBirthday() != null) existingUser.setBirthday(updatedUser.getBirthday());
            if (updatedUser.getAddress() != null) existingUser.setAddress(updatedUser.getAddress());
            if (updatedUser.getCity() != null) existingUser.setCity(updatedUser.getCity());
            if (updatedUser.getState() != null) existingUser.setState(updatedUser.getState());
            if (updatedUser.getZipcode() != null) existingUser.setZipcode(updatedUser.getZipcode());
            //使用者自己可以修改訂閱狀態
            if (updatedUser.getHasNewsletter() != null) existingUser.setHasNewsletter(updatedUser.getHasNewsletter());

            //儲存回資料庫
            User savedUser = userRepository.save(existingUser);
            return Optional.of(savedUser);
        } else {
            return Optional.empty();  //找不到使用者
        }
    }

    //5.刪除使用者
    public boolean deleteUserById(int id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;  //刪除成功
        } else {
            return false;  //使用者不存在
        }
    }

    //6.使用 email + password 登入並更新最後登入時間
    public Optional<LoginResponse> login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);  //從資料庫查找對應 Email 的使用者
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            User existingUser = user.get();
            existingUser.setUpdateLoginTime(LocalDateTime.now());  //更新登入時間
            userRepository.save(existingUser);  //存回資料庫
            String token = jwtService.generateToken(existingUser);  //產生 JWT

            //將使用者資訊與 JWT 包成 LoginResponse 回傳
            return Optional.of(new LoginResponse(
                    existingUser.getId(),
                    existingUser.getEmail(),
                    existingUser.getUpdateLoginTime(),
                    token
            ));
        }
        return Optional.empty();  //帳號不存在或密碼錯誤
    }
}