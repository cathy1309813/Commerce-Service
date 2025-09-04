package com.gtalent.commerce.service.services;

import com.gtalent.commerce.service.repositories.SegmentRepository;
import com.gtalent.commerce.service.repositories.UserRepository;
import com.gtalent.commerce.service.repositories.UserSegmentRepository;
import org.springframework.stereotype.Service;

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

    //1.取得使用者的 segment
    //2.更新使用者的 segment




}
