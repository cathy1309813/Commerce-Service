package com.gtalent.commerce.service.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    @NotBlank(message = "必須填寫")
    private String firstName;

    @NotBlank(message = "必須填寫")
    private String lastName;

    @Email(message = "格式必須正確")
    @NotBlank(message = "必須填寫")
    private String email;

    @NotNull(message = "必須填寫")
    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDate birthday;

    @NotBlank(message = "必須填寫")
    private String address;

    @NotBlank(message = "必須填寫")
    private String city;

    @NotBlank(message = "必須填寫")
    private String state;

    @NotBlank(message = "必須填寫")
    private String zipcode;

    @NotBlank(message = "必須填寫")
    @Size(min = 8, message = "密碼必須至少 8 個字元")
    @Pattern(
            regexp = "^(?=.*[a-z])" +         //至少有一個小寫字母
                     "(?=.*[A-Z])" +          //至少有一個大寫字母
                     "(?=.*\\d)" +            //至少有一個數字
                     "(?=.*[^a-zA-Z0-9])" +   //至少有一個特殊符號
                     ".{8,}$",                //長度至少 8
            message = "密碼必須包含大小寫字母、數字和特殊符號"
    )
    private String password;

    private Boolean hasNewsletter = false;  //確保新增使用者時默認為未訂閱 -> false
}
