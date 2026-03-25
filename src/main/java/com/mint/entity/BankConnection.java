package com.mint.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bank_connections")
public class BankConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String accountId; // ID tài khoản phía ngân hàng trả về

    private String status; // Trạng thái: PENDING, LINKED, FAILED
    
    private LocalDateTime lastSyncDate; // Thời gian đồng bộ dữ liệu cuối cùng

    // QUAN HỆ NHIỀU-1: Nhiều lượt liên kết có thể thuộc về 1 Người dùng (User)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // QUAN HỆ NHIỀU-1: Nhiều lượt liên kết có thể trỏ đến 1 Ngân hàng (Bank)
    @ManyToOne
    @JoinColumn(name = "bank_id")
    private Bank bank;

    // --- Getters và Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getLastSyncDate() { return lastSyncDate; }
    public void setLastSyncDate(LocalDateTime lastSyncDate) { this.lastSyncDate = lastSyncDate; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Bank getBank() { return bank; }
    public void setBank(Bank bank) { this.bank = bank; }
}
