package com.gtalent.commerce.service.services;

import com.gtalent.commerce.service.models.Segment;
import com.gtalent.commerce.service.models.User;
import com.gtalent.commerce.service.models.UserSegment;
import com.gtalent.commerce.service.repositories.SegmentRepository;
import com.gtalent.commerce.service.repositories.UserRepository;
import com.gtalent.commerce.service.repositories.UserSegmentRepository;

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
    public List<Segment> getSegmentsByUser(int userId) {
        List<UserSegment> userSegments = userSegmentRepository.findByUserId(userId);  //先取得某個使用者的 UserSegment 關聯表
        List<Segment> segments = new ArrayList<>();  //再宣告一個List變數(名稱是 segments)來存放 Segment
        for (UserSegment us : userSegments) {  //對於 userSegments 清單裡的每一個 UserSegment (us)，去取得它對應的 Segment (us.getSegment())
            segments.add(us.getSegment());  //然後把取出的 Segment 加入到 segments 這個清單
        }
        return segments;
    }

    //2.查詢某 Segment 下的使用者 (從橋樑表找到所有 Segment A 的橋樑 -> 從橋樑拿出對應的使用者 -> 收集起來回傳)
    public List<User> getUsersBySegment(int segmentId) {
        List<UserSegment> userSegments = userSegmentRepository.findBySegmentId(segmentId); //先取得某個 Segment 的 UserSegment 關聯表
        List<User> users = new ArrayList<>();  //再宣告一個List變數(名稱是 users)來存放 users
        for (UserSegment s : userSegments) {
            users.add(s.getUser());
        }
        return users;
    }

    //3.新增 user<->segment 關聯 (確認使用者存在 -> 確認 Segment 存在 -> 檢查是否已有關聯 -> 如果沒有 → 建立新的 UserSegment -> 存入資料庫並回傳)
    public UserSegment assignUserToSegment(int userId, int segmentId) {
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
            return existing.get();  //如果存在就return
        }
        UserSegment userSegment = new UserSegment();  //建立新 userSegment
        userSegment.setUser(user.get());  //用 get()取出並存 User
        userSegment.setSegment(segment.get());  //用 get()取出並存 Segment
        return userSegmentRepository.save(userSegment);  //存入資料庫並回傳
    }

    //4.移除 user<->segment 關聯 (找到對應的 UserSegment -> 確認關聯存在 -> 刪除這個 UserSegment -> 回傳刪除結果)
    public UserSegment removeUserFromSegment(int userId, int segmentId) {
        //Optional 的設計理念: 明確表示「結果可能存在，也可能不存在」，強制開發者處理「找不到的情況」，更安全也更明確。
        //確認此user<->segment彼此關聯存在
        Optional<UserSegment> existing = userSegmentRepository.findByUserIdAndSegmentId(userId, segmentId);
        if (existing.isEmpty()) {
            return null;  //找不到關聯的話，會回傳null
        }
        UserSegment userSegment = existing.get();
        userSegmentRepository.delete(existing.get());  //資料庫有找到的話就刪除
        return userSegment;  //回傳被刪掉的關聯
    }
}
