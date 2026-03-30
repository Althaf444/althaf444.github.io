package com.mint.test.util;

import com.mint.entity.Bank;
import com.mint.entity.BankConnection;
import com.mint.entity.ConnectionStatus;
import com.mint.entity.SyncStatus;
import com.mint.entity.User;

import java.time.LocalDateTime;

/**
 * Test data builders and fixtures for bank connection tests
 */
public class TestDataBuilder {

    public static User buildUser(Long id, String username, String email) {
        return User.builder()
                .id(id)
                .username(username)
                .email(email)
                .password("password123")
                .build();
    }

    public static User buildUser() {
        return buildUser(1L, "testuser", "test@example.com");
    }

    public static Bank buildBank(Long id, String name, String apiEndpoint) {
        return Bank.builder()
                .id(id)
                .name(name)
                .apiEndpoint(apiEndpoint)
                .build();
    }

    public static Bank buildBank() {
        return buildBank(1L, "Test Bank", "https://api.testbank.com");
    }

    public static BankConnection buildBankConnection(User user, Bank bank, String accountId, ConnectionStatus status) {
        return BankConnection.builder()
                .id(1L)
                .user(user)
                .bank(bank)
                .accountId(accountId)
                .status(status)
                .syncStatus(SyncStatus.IDLE)
                .syncFailureCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static BankConnection buildBankConnection() {
        return buildBankConnection(buildUser(), buildBank(), "ACC_123456", ConnectionStatus.LINKED);
    }

    public static BankConnection buildPendingConnection() {
        return buildBankConnection(buildUser(), buildBank(), "ACC_789012", ConnectionStatus.PENDING);
    }

    public static BankConnection buildFailedConnection() {
        return BankConnection.builder()
                .id(1L)
                .user(buildUser())
                .bank(buildBank())
                .accountId("ACC_345678")
                .status(ConnectionStatus.FAILED)
                .syncStatus(SyncStatus.IDLE)
                .syncFailureCount(5)
                .syncErrorMessage("Connection failed after 5 attempts")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
