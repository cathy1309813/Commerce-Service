package com.gtalent.commerce.service.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {
    private int id;
    private int rating;
    private String comment;
    private int userId;
    private int productId;
    private String status;  //評論狀態（PENDING / APPROVED / REJECTED）
    private LocalDate date;  //評論日期
    private LocalDateTime createdAt;  //評論建立時間
}

