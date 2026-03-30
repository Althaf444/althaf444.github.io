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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConnectionStatus status; // Trạng thái: PENDING, LINKED, FAILED, EXPIRED, SYNCING

    @Enumerated(EnumType.STRING)
    private SyncStatus syncStatus; // IDLE, SYNCING, SUCCESS, FAILED

    private String syncErrorMessage; // Thông báo lỗi khi đồng bộ thất bại

    private LocalDateTime lastSyncDate; // Thời gian đồng bộ dữ liệu cuối cùng

    private LocalDateTime lastSyncAttempt; // Thời gian cố gắng đồng bộ gần nhất

    private LocalDateTime createdAt; // Thời gian tạo kết nối

    private LocalDateTime updatedAt; // Thời gian cập nhật lần cuối

    private Integer syncFailureCount; // Số lần đồng bộ thất bại liên tiếp

    // QUAN HỆ NHIỀU-1: Nhiều lượt liên kết có thể thuộc về 1 Người dùng (User)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // QUAN HỆ NHIỀU-1: Nhiều lượt liên kết có thể trỏ đến 1 Ngân hàng (Bank)
    @ManyToOne
    @JoinColumn(name = "bank_id", nullable = false)
    private Bank bank;

    // Required by JPA
    protected BankConnection() {}

    private BankConnection(Builder builder) {
        this.id = builder.id;
        this.accountId = builder.accountId;
        this.status = builder.status;
        this.syncStatus = builder.syncStatus;
        this.syncErrorMessage = builder.syncErrorMessage;
        this.lastSyncDate = builder.lastSyncDate;
        this.lastSyncAttempt = builder.lastSyncAttempt;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.syncFailureCount = builder.syncFailureCount;
        this.user = builder.user;
        this.bank = builder.bank;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String accountId;
        private ConnectionStatus status;
        private SyncStatus syncStatus;
        private String syncErrorMessage;
        private LocalDateTime lastSyncDate;
        private LocalDateTime lastSyncAttempt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Integer syncFailureCount;
        private User user;
        private Bank bank;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder accountId(String accountId) { this.accountId = accountId; return this; }
        public Builder status(ConnectionStatus status) { this.status = status; return this; }
        public Builder syncStatus(SyncStatus syncStatus) { this.syncStatus = syncStatus; return this; }
        public Builder syncErrorMessage(String syncErrorMessage) { this.syncErrorMessage = syncErrorMessage; return this; }
        public Builder lastSyncDate(LocalDateTime lastSyncDate) { this.lastSyncDate = lastSyncDate; return this; }
        public Builder lastSyncAttempt(LocalDateTime lastSyncAttempt) { this.lastSyncAttempt = lastSyncAttempt; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder syncFailureCount(Integer syncFailureCount) { this.syncFailureCount = syncFailureCount; return this; }
        public Builder user(User user) { this.user = user; return this; }
        public Builder bank(Bank bank) { this.bank = bank; return this; }

        public BankConnection build() { return new BankConnection(this); }
    }

    // --- Getters và Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }

    public ConnectionStatus getStatus() { return status; }
    public void setStatus(ConnectionStatus status) { this.status = status; }

    public SyncStatus getSyncStatus() { return syncStatus; }
    public void setSyncStatus(SyncStatus syncStatus) { this.syncStatus = syncStatus; }

    public String getSyncErrorMessage() { return syncErrorMessage; }
    public void setSyncErrorMessage(String syncErrorMessage) { this.syncErrorMessage = syncErrorMessage; }

    public LocalDateTime getLastSyncDate() { return lastSyncDate; }
    public void setLastSyncDate(LocalDateTime lastSyncDate) { this.lastSyncDate = lastSyncDate; }

    public LocalDateTime getLastSyncAttempt() { return lastSyncAttempt; }
    public void setLastSyncAttempt(LocalDateTime lastSyncAttempt) { this.lastSyncAttempt = lastSyncAttempt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Integer getSyncFailureCount() { return syncFailureCount; }
    public void setSyncFailureCount(Integer syncFailureCount) { this.syncFailureCount = syncFailureCount; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Bank getBank() { return bank; }
    public void setBank(Bank bank) { this.bank = bank; }
}
