package com.mint.entity;

import jakarta.persistence.*;

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

    // TODO: Chúng ta sẽ thêm phần code thiết lập Quan hệ (Relations) với BankConnection ở các bước sau

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
}
