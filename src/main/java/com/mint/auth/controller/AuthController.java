package com.mint.auth.controller;

import com.mint.auth.dto.*;
import com.mint.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDto request) {
        authService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto request) {
        try {
            LoginResponseDto response = authService.login(request);
            return ResponseEntity.ok("Token: " + response.getToken() + ", requiresMfa: " + response.isRequiresMfa());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/mfa/setup")
    public ResponseEntity<MfaSetupResponseDto> setupMfa(@RequestParam Long userId) {
        MfaSetupResponseDto response = authService.setupMfa(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/mfa/verify")
    public ResponseEntity<String> verifyMfa(@RequestParam Long userId, @RequestBody VerifyMfaRequestDto request) {
        authService.verifyMfa(userId, request);
        return ResponseEntity.ok("MFA enabled successfully");
    }
}
