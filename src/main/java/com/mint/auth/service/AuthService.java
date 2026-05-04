package com.mint.auth.service;

import com.mint.auth.dto.*;
import com.mint.auth.util.JwtUtil;
import com.mint.entity.User;
import com.mint.repository.UserRepository;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private final SecretGenerator secretGenerator = new DefaultSecretGenerator();
    private final QrGenerator qrGenerator = new ZxingPngQrGenerator();
    private final CodeVerifier verifier;
    private final String jwtSecret = "mySecretKey"; // In production, use env variable
    private final long jwtExpiration = 86400000; // 1 day

    public AuthService() {
        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        this.verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
    }

    public void register(RegisterRequestDto request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent() ||
            userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists");
        }
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .mfaEnabled(false)
                .build();
        userRepository.save(user);
    }

    public LoginResponseDto login(LoginRequestDto request) {
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        if (userOpt.isEmpty() || !passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        User user = userOpt.get();
        if (user.isMfaEnabled()) {
            if (request.getMfaCode() == null || !verifier.isValidCode(user.getMfaSecret(), request.getMfaCode())) {
                return new LoginResponseDto(null, true);
            }
        }
        String token = jwtUtil.generateToken(user.getUsername());
        return new LoginResponseDto(token, false);
    }

    public MfaSetupResponseDto setupMfa(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        String secret = secretGenerator.generate();
        user.setMfaSecret(secret);
        userRepository.save(user);
        try {
            QrData data = new QrData.Builder()
                    .label(user.getUsername())
                    .secret(secret)
                    .issuer("MintApp")
                    .algorithm(HashingAlgorithm.SHA1)
                    .digits(6)
                    .period(30)
                    .build();
            String qrCodeUrl = "data:image/png;base64," + Base64.getEncoder().encodeToString(qrGenerator.generate(data));
            return new MfaSetupResponseDto(secret, qrCodeUrl);
        } catch (QrGenerationException e) {
            throw new RuntimeException("Error generating QR code");
        }
    }

    public void verifyMfa(Long userId, VerifyMfaRequestDto request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        if (verifier.isValidCode(user.getMfaSecret(), request.getCode())) {
            user.setMfaEnabled(true);
            userRepository.save(user);
        } else {
            throw new RuntimeException("Invalid MFA code");
        }
    }
}
