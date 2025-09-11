package com.gtalent.commerce.service.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private int status;               //HTTP 狀態碼
    private String message;           //錯誤訊息
    private LocalDateTime timestamp;  //發生時間
}
