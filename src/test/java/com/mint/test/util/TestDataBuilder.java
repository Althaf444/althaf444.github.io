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
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword("password123");
        return user;
    }
    
    public static User buildUser() {
        return buildUser(1L, "testuser", "test@example.com");
    }
    
    public static Bank buildBank(Long id, String name, String apiEndpoint) {
        Bank bank = new Bank();
        bank.setId(id);
        bank.setName(name);
        bank.setApiEndpoint(apiEndpoint);
        return bank;
    }
    
    public static Bank buildBank() {
        return buildBank(1L, "Test Bank", "https://api.testbank.com");
    }
    
    public static BankConnection buildBankConnection(User user, Bank bank, String accountId, ConnectionStatus status) {
        BankConnection connection = new BankConnection();
        connection.setId(1L);
        connection.setUser(user);
        connection.setBank(bank);
        connection.setAccountId(accountId);
        connection.setStatus(status);
        connection.setSyncStatus(SyncStatus.IDLE);
        connection.setSyncFailureCount(0);
        connection.setCreatedAt(LocalDateTime.now());
        connection.setUpdatedAt(LocalDateTime.now());
        return connection;
    }
    
    public static BankConnection buildBankConnection() {
        return buildBankConnection(buildUser(), buildBank(), "ACC_123456", ConnectionStatus.LINKED);
    }
    
    public static BankConnection buildPendingConnection() {
        return buildBankConnection(buildUser(), buildBank(), "ACC_789012", ConnectionStatus.PENDING);
    }
    
    public static BankConnection buildFailedConnection() {
        BankConnection connection = buildBankConnection(buildUser(), buildBank(), "ACC_345678", ConnectionStatus.FAILED);
        connection.setSyncFailureCount(5);
        connection.setSyncErrorMessage("Connection failed after 5 attempts");
        return connection;
    }
}

