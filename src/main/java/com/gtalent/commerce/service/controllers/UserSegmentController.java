package com.gtalent.commerce.service.controllers;

import com.gtalent.commerce.service.requests.UserSegmentRequest;
import com.gtalent.commerce.service.responses.UserResponse;
import com.gtalent.commerce.service.responses.UserSegmentResponse;
import com.gtalent.commerce.service.services.UserSegmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "User-Segment 功能-第一版", description = "提供使用者與 Segment 之間的查詢、分配與移除 API")
@RestController  //物件轉成 JSON
@RequestMapping("/commerce-service/user-segments")
public class UserSegmentController {
    @Autowired
    private UserSegmentService userSegmentService;

    //1.查詢某使用者所屬的 Segment
    @GetMapping("/{userId}/segments")
    @Operation(summary = "查詢某使用者的 Segment", description = "依使用者 ID 查詢其所屬的 Segment 清單")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功取得使用者的 Segment 清單"),
            @ApiResponse(responseCode = "404", description = "使用者不存在"),
            @ApiResponse(responseCode = "500", description = "伺服器內部錯誤")
    })
    public ResponseEntity<UserResponse> getSegmentsWithUser(@PathVariable int userId) {
        //先從 Service 取得 UserResponse
        Optional<UserResponse> optionalResponse = userSegmentService.getSegmentsWithUser(userId);
        // 傳統判斷
        if (optionalResponse.isPresent()) {
            return ResponseEntity.ok(optionalResponse.get()); // 使用者存在 → 回傳 200 + DTO
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 使用者不存在 → 回傳 404
        }
    }

    //2.查詢某 Segment 下的使用者
    @GetMapping("/segments/{segmentId}/users")
    @Operation(summary = "查詢某 Segment 下的使用者", description = "依 Segment ID 查詢所有屬於該 Segment 的使用者")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功取得使用者清單"),
            @ApiResponse(responseCode = "404", description = "Segment 不存在或沒有使用者"),
            @ApiResponse(responseCode = "500", description = "伺服器內部錯誤")
    })
    public ResponseEntity<List<UserResponse>> getUsersBySegment(@PathVariable int segmentId) {
        List<UserResponse> responses = userSegmentService.getUsersBySegment(segmentId);
        if (responses.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); //沒有使用者或 Segment 不存在 → 404
        } else {
            return ResponseEntity.ok(responses);  //回傳 DTO 清單 → 200
        }
    }

    //3.新增 user<->Segment 關聯
    //如果要傳多個欄位或 JSON 結構，建議改成 @RequestBody!
    @PostMapping("/assign")
    @Operation(summary = "分配 Segment 給使用者", description = "建立使用者與 Segment 的關聯")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功建立關聯"),
            @ApiResponse(responseCode = "404", description = "使用者或 Segment 不存在"),
            @ApiResponse(responseCode = "500", description = "伺服器內部錯誤")
    })
    public ResponseEntity<UserSegmentResponse> assignSegmentToUser(
            @RequestBody UserSegmentRequest request) {

        UserSegmentResponse response = userSegmentService
                .assignUserToSegment(request.getUserId(), request.getSegmentId());

        return ResponseEntity.ok(response);
    }

    //4.移除 user<->segment 關聯
    @DeleteMapping("/remove")
    @Operation(summary = "移除使用者的 Segment", description = "刪除使用者與 Segment 的關聯")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "成功刪除關聯"),
            @ApiResponse(responseCode = "404", description = "找不到使用者或 Segment"),
            @ApiResponse(responseCode = "500", description = "伺服器內部錯誤")
    })
    public ResponseEntity<Void> removeSegmentFromUser(@RequestBody UserSegmentRequest request) {
        UserSegmentResponse response = userSegmentService.removeUserFromSegment(
                request.getUserId(), request.getSegmentId());
        if (response != null) {
            return ResponseEntity.noContent().build();  // 刪除成功 → 回傳 204
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 找不到 → 回傳 404
        }
    }
}
