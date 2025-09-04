package com.gtalent.commerce.service.controllers;

import com.gtalent.commerce.service.services.UserSegmentService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/commerce-service/user-segments")
public class SegmentController {
    private final UserSegmentService userSegmentService;

    public SegmentController(UserSegmentService userSegmentService) {
        this.userSegmentService = userSegmentService;
    }

    //1.取得使用者的 segment
    //2.更新使用者的 segment
}
