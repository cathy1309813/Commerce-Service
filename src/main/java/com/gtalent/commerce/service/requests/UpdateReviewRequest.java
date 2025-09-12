package com.gtalent.commerce.service.requests;

import com.gtalent.commerce.service.enums.ReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateReviewRequest {
    private Integer rating;            //更新評分
    private String comment;        //更新內容
    private ReviewStatus status;   //更新狀態
}
