package com.gtalent.commerce.service.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSegmentRequest {
    private int userId;
    private int segmentId;
}
