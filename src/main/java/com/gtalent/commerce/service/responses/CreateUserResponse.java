package com.gtalent.commerce.service.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "新增使用者回傳 DTO")
public class CreateUserResponse {
    private int id;
    private String firstName;
    private String lastName;
    private Boolean hasNewsletter;
}
