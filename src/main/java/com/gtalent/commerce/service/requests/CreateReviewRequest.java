package com.gtalent.commerce.service.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateReviewRequest {
    private int rating;
    private String comment;
    private int userId;
    private int productId;
}

