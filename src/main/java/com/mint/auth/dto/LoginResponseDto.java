package com.mint.auth.dto;

public class LoginResponseDto {
    private String token;
    private boolean requiresMfa;

    public LoginResponseDto() {
    }

    public LoginResponseDto(String token, boolean requiresMfa) {
        this.token = token;
        this.requiresMfa = requiresMfa;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isRequiresMfa() {
        return requiresMfa;
    }

    public void setRequiresMfa(boolean requiresMfa) {
        this.requiresMfa = requiresMfa;
    }
}
