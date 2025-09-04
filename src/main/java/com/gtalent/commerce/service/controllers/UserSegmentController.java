package com.gtalent.commerce.service.controllers;

import com.gtalent.commerce.service.models.Segment;
import com.gtalent.commerce.service.models.User;
import com.gtalent.commerce.service.models.UserSegment;
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

@Tag(name = "Segment 功能", description = "提供 Segment 相關 API。")
@RestController  //物件轉成 JSON
@RequestMapping("/commerce-service/segments")
public class UserSegmentController {
    @Autowired
    private UserSegmentService userSegmentService;

    //1.查詢某使用者所屬的 Segment
    @GetMapping("/user/{userId}")
    @Operation(summary = "查詢某使用者的 Segment", description = "依使用者 ID 查詢其所屬的 Segment 清單")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功取得使用者的 Segment 清單"),
            @ApiResponse(responseCode = "404", description = "使用者不存在"),
            @ApiResponse(responseCode = "500", description = "伺服器內部錯誤")
    })
    public ResponseEntity<List<Segment>> getSegmentsByUser(@PathVariable int userId) {
        List<Segment> segments = userSegmentService.getSegmentsByUser(userId);
        if (segments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  //404 找不到
        }
        return ResponseEntity.ok(segments);
    }

    //2.查詢某 Segment 下的使用者
    @GetMapping("/{segmentId}/users")
    @Operation(summary = "查詢某 Segment 下的使用者", description = "依 Segment ID 查詢所有屬於該 Segment 的使用者")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功取得使用者清單"),
            @ApiResponse(responseCode = "404", description = "Segment 不存在"),
            @ApiResponse(responseCode = "500", description = "伺服器內部錯誤")
    })
    public ResponseEntity<List<User>> getUsersBySegment(@PathVariable int segmentId) {
        List<User> users = userSegmentService.getUsersBySegment(segmentId);
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  //404 找不到
        }
        return ResponseEntity.ok(users);
    }

    //3.新增 user<->Segment 關聯
    @PostMapping("/assign/{userId}/{segmentId}")
    @Operation(summary = "分配 Segment 給使用者", description = "建立使用者與 Segment 的關聯")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功建立關聯"),
            @ApiResponse(responseCode = "404", description = "使用者或 Segment 不存在"),
            @ApiResponse(responseCode = "500", description = "伺服器內部錯誤")
    })
    public ResponseEntity<UserSegment> assignSegmentToUser(@PathVariable int userId, @PathVariable int segmentId) {
        UserSegment userSegment = userSegmentService.assignUserToSegment(userId, segmentId);
        if (userSegment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  //404 找不到
        }
        return ResponseEntity.ok(userSegment);
    }

    //4.移除 user<->segment 關聯
    @DeleteMapping("/remove/{userId}/{segmentId}")
    @Operation(summary = "移除使用者的 Segment 關聯", description = "刪除使用者與 Segment 的關聯")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功刪除關聯"),
            @ApiResponse(responseCode = "404", description = "使用者與 Segment 的關聯不存在"),
            @ApiResponse(responseCode = "500", description = "伺服器內部錯誤")
    })
    public ResponseEntity<UserSegment> removeSegmentFromUser(@PathVariable int userId, @PathVariable int segmentId) {
        UserSegment deleted = userSegmentService.removeUserFromSegment(userId, segmentId);
        if (deleted == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  //404 找不到
        }
        return ResponseEntity.ok(deleted);  //200 刪除成功
    }
}
