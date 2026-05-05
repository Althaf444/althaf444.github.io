package com.mint.auth.dto;

public class VerifyMfaRequestDto {
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
