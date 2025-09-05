package com.gtalent.commerce.service.services;

import com.gtalent.commerce.service.models.Segment;
import com.gtalent.commerce.service.models.User;
import com.gtalent.commerce.service.models.UserSegment;
import com.gtalent.commerce.service.repositories.SegmentRepository;
import com.gtalent.commerce.service.repositories.UserRepository;
import com.gtalent.commerce.service.repositories.UserSegmentRepository;

import com.gtalent.commerce.service.responses.UserResponse;
import com.gtalent.commerce.service.responses.UserSegmentResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UserSegmentService {
    private final UserSegmentRepository userSegmentRepository;
    private final UserRepository userRepository;

    private final SegmentRepository segmentRepository;

    public UserSegmentService(UserSegmentRepository userSegmentRepository,
                              UserRepository userRepository,
                              SegmentRepository segmentRepository) {
        this.userSegmentRepository = userSegmentRepository;
        this.userRepository = userRepository;
        this.segmentRepository = segmentRepository;
    }

    //1.查詢某使用者所屬的 Segment (把每個使用者對應的 Segment 收集起來)
    public Optional<UserResponse> getSegmentsWithUser(int userId) {
        return userRepository.findById(userId)  //呼叫 JPA Repository 的 findById 方法查詢使用者
                .map(user -> {
                    UserResponse ur = new UserResponse();  //建立一個 DTO (UserResponse)
                    ur.setId(user.getId());
                    ur.setFirstName(user.getFirstName());
                    ur.setLastName(user.getLastName());
                    ur.setSegments(user.getUserSegments()
                            .stream()
                            //對每個 UserSegment 建立一個 UserSegmentResponse DTO
                            .map(UserSegmentResponse::new)
                            .toList());
                    return ur;
                });
    }

    //2.查詢某 Segment 下的使用者 (從橋樑表找到所有 Segment A 的橋樑 -> 從橋樑拿出對應的使用者 -> 收集起來回傳)
    public List<UserResponse> getUsersBySegment(int segmentId) {
        //先從 UserSegment 橋樑表找到所有對應這個 segment 的 User
        List<UserSegment> userSegments = userSegmentRepository.findBySegmentId(segmentId);
        List<UserResponse> responses = new ArrayList<>();
        for (UserSegment us : userSegments) {
            User user = us.getUser();  // 取得使用者

            UserResponse ur = new UserResponse();  //把 User 的基本資訊傳到 ur
            ur.setId(user.getId());
            ur.setFirstName(user.getFirstName());
            ur.setLastName(user.getLastName());
            ur.setHasNewsletter(user.getHasNewsletter());
            //如果需要也可以填充 segments
            List<UserSegmentResponse> segmentResponses = new ArrayList<>();
            for (UserSegment uSeg : user.getUserSegments()) {  //遍歷這個 User 所屬的 segments
                segmentResponses.add(new UserSegmentResponse(uSeg));
            }
            ur.setSegments(segmentResponses);

            responses.add(ur);
        }
        return responses;
    }

    //3.新增 user<->segment 關聯 (確認使用者存在 -> 確認 Segment 存在 -> 檢查是否已有關聯 -> 如果沒有 → 建立新的 UserSegment -> 存入資料庫並回傳)
    public UserSegmentResponse assignUserToSegment(int userId, int segmentId) {
        //Optional 的設計理念: 明確表示「結果可能存在，也可能不存在」，強制開發者處理「找不到的情況」，更安全也更明確。
        //找到使用者
        Optional<User> user = userRepository.findById(userId);
        if(user.isEmpty()) {
            throw new RuntimeException("使用者不存在");
        }
        //找到segment
        Optional<Segment> segment = segmentRepository.findById(segmentId);
        if(segment.isEmpty()) {
            throw new RuntimeException("segment不存在");
        }
        //確認此user<->segment彼此關聯存在
        Optional<UserSegment> existing = userSegmentRepository.findByUserIdAndSegmentId(userId, segmentId);
        if (existing.isPresent()) {
            return new UserSegmentResponse(existing.get()); // 已有關聯直接回傳 DTO
        }
        UserSegment userSegment = new UserSegment();  //建立新 userSegment
        userSegment.setUser(user.get());  //用 get()取出並存 User
        userSegment.setSegment(segment.get());  //用 get()取出並存 Segment
        UserSegment saved = userSegmentRepository.save(userSegment);  //存入資料庫
        return new UserSegmentResponse(saved);  //回傳DTO
    }

    //4.移除 user<->segment 關聯 (找到對應的 UserSegment -> 確認關聯存在 -> 刪除這個 UserSegment -> 回傳刪除結果)
    public UserSegmentResponse removeUserFromSegment(int userId, int segmentId) {
        //Optional 的設計理念: 明確表示「結果可能存在，也可能不存在」，強制開發者處理「找不到的情況」，更安全也更明確。
        //確認此user<->segment彼此關聯存在
        Optional<UserSegment> existing = userSegmentRepository.findByUserIdAndSegmentId(userId, segmentId);
        if (existing.isEmpty()) {
            return null;  //找不到關聯的話，會回傳null
        }
        UserSegment userSegment = existing.get();
        userSegmentRepository.delete(existing.get());  //資料庫有找到的話就刪除
        return new UserSegmentResponse(userSegment);  //回傳被刪掉的關聯
    }
}
