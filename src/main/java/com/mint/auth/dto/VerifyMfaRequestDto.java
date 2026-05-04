package com.mint.auth.dto;

import lombok.Data;

@Data
public class VerifyMfaRequestDto {
    private String code;
}
