package com.mint.entity;

import jakarta.persistence.*;
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

    // QUAN HỆ 1-NHIỀU: 1 Người dùng có thể có nhiều lượt liên kết tài khoản ngân hàng
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BankConnection> bankConnections;

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

    public List<BankConnection> getBankConnections() { return bankConnections; }
    public void setBankConnections(List<BankConnection> bankConnections) { this.bankConnections = bankConnections; }
}
