package com.gtalent.commerce.service.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private int id;
    private String email;
    private LocalDateTime lastLoginTime;  //最後登入時間
    private String token;                 //JWT Token
}
