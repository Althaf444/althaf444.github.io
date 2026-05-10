package com.mint.entity;

import javax.persistence.*;
import java.util.List;

@Entity // Đánh dấu class này là một Thực thể kết nối với Database
@Table(name = "users") // Tên bảng lưu trong Database (thường để số nhiều)
public class User {

    @Id // Đánh dấu đây là Khóa chính (Primary Key)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tự động tăng ID (1, 2, 3...)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column
    private String mfaSecret;

    @Column(nullable = false)
    private boolean mfaEnabled = false;

    // QUAN HỆ 1-NHIỀU: 1 Người dùng có thể có nhiều lượt liên kết tài khoản ngân hàng
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BankConnection> bankConnections;

    // Required by JPA
    protected User() {}

    private User(Builder builder) {
        this.id = builder.id;
        this.username = builder.username;
        this.email = builder.email;
        this.password = builder.password;
        this.mfaSecret = builder.mfaSecret;
        this.mfaEnabled = builder.mfaEnabled;
        this.bankConnections = builder.bankConnections;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String username;
        private String email;
        private String password;
        private String mfaSecret;
        private boolean mfaEnabled;
        private List<BankConnection> bankConnections;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder mfaSecret(String mfaSecret) { this.mfaSecret = mfaSecret; return this; }
        public Builder mfaEnabled(boolean mfaEnabled) { this.mfaEnabled = mfaEnabled; return this; }
        public Builder bankConnections(List<BankConnection> bankConnections) { this.bankConnections = bankConnections; return this; }

        public User build() { return new User(this); }
    }

    // --- Getters và Setters ---
    // (Bắt buộc phải có để Spring Boot đọc/ghi dữ liệu)

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getMfaSecret() { return mfaSecret; }
    public void setMfaSecret(String mfaSecret) { this.mfaSecret = mfaSecret; }

    public boolean isMfaEnabled() { return mfaEnabled; }
    public void setMfaEnabled(boolean mfaEnabled) { this.mfaEnabled = mfaEnabled; }

    public List<BankConnection> getBankConnections() { return bankConnections; }
    public void setBankConnections(List<BankConnection> bankConnections) { this.bankConnections = bankConnections; }
}
