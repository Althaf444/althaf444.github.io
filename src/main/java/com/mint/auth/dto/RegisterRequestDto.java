package com.mint.auth.dto;

import lombok.Data;

@Data
public class RegisterRequestDto {
    private String username;
    private String email;
    private String password;
}
