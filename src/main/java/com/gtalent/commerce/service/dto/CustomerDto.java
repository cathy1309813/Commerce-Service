package com.gtalent.commerce.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDto {
    private int id;
    private String firstName;
    private String lastName;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
