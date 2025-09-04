package com.gtalent.commerce.service.controllers;

import com.gtalent.commerce.service.models.Segment;
import com.gtalent.commerce.service.repositories.SegmentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Segment 功能", description = "提供 Segment 相關 API。")
@RestController  //物件轉成 JSON
@RequestMapping("/commerce-service/segments")
public class SegmentController {
    @Autowired
    private SegmentRepository segmentRepository;

    @GetMapping
    @Operation(
            summary = "取得所有 Segment",
            description = "查詢資料庫中的所有 Segment 分類，回傳 JSON 清單。"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功取得 Segment 清單"),
            @ApiResponse(responseCode = "500", description = "伺服器內部錯誤")
    })
    public ResponseEntity<List<Segment>> getAllSegments() {
        List<Segment> segments = segmentRepository.findAll();
        return ResponseEntity.ok(segments);
    }
}
