package com.mint.auth.dto;

public class MfaSetupResponseDto {
    private String secret;
    private String qrCodeUrl;

    public MfaSetupResponseDto() {
    }

    public MfaSetupResponseDto(String secret, String qrCodeUrl) {
        this.secret = secret;
        this.qrCodeUrl = qrCodeUrl;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }
}
