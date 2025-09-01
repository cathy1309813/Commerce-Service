package com.gtalent.commerce.service.Responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
