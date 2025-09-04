package com.gtalent.commerce.service.requests;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    //可選欄位，如果傳 null → 不更新
    private String firstName;
    private String lastName;

    @Email(message = "格式必須正確")
    private String email;

    private LocalDate birthday;

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

    private String address;
    private String city;
    private String state;
    private String zipcode;
    private boolean hasNewsletter;  // true=Yes, false=No
}
