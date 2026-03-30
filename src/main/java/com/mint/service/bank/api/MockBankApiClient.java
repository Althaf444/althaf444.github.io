package com.mint.service.bank.api;

import com.mint.dto.BankAccountDto;
import com.mint.dto.BankTransactionDto;
import com.mint.exception.ConnectionTimeoutException;
import com.mint.exception.RateLimitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Mock implementation of BankApiClient for testing purposes
 * Simulates real bank API behavior including failures, delays, and rate limiting
 */
@Service
public class MockBankApiClient implements BankApiClient {
    private static final Logger logger = LoggerFactory.getLogger(MockBankApiClient.class);
    
    private final Random random = new Random();
    private int callCount = 0;
    private LocalDateTime lastAuthTime;
    
    // Configuration for failure simulation (percentages)
    private static final int TIMEOUT_FAILURE_RATE = 2;      // 2% chance of timeout
    private static final int RATE_LIMIT_FAILURE_RATE = 1;   // 1% chance of rate limit
    private static final int CONNECTION_FAILURE_RATE = 2;   // 2% chance of connection failure
    private static final int SIMULATED_DELAY_MS = 50;       // Simulate network delay
    
    @Override
    public String authenticate(String credentials) throws ConnectionTimeoutException, RateLimitException {
        callCount++;
        logger.info("Authenticating with credentials: {}", maskCredentials(credentials));
        
        simulateNetworkDelay();
        
        // Simulate failures
        if (shouldFail(TIMEOUT_FAILURE_RATE)) {
            throw new ConnectionTimeoutException("Bank API authentication timeout");
        }
        if (shouldFail(RATE_LIMIT_FAILURE_RATE)) {
            throw new RateLimitException("Rate limit exceeded on authentication", 60);
        }
        if (shouldFail(CONNECTION_FAILURE_RATE)) {
            throw new ConnectionTimeoutException("Failed to connect to bank API");
        }
        
        lastAuthTime = LocalDateTime.now();
        String token = generateMockToken();
        logger.info("Authentication successful. Token: {}", maskToken(token));
        return token;
    }
    
    @Override
    public BankAccountDto fetchAccount(String accountId, String accessToken) throws ConnectionTimeoutException, RateLimitException {
        callCount++;
        logger.info("Fetching account details for account: {}", accountId);
        
        // Validate token
        if (!isValidToken(accessToken)) {
            throw new ConnectionTimeoutException("Invalid or expired access token");
        }
        
        simulateNetworkDelay();
        
        // Simulate failures
        if (shouldFail(TIMEOUT_FAILURE_RATE)) {
            throw new ConnectionTimeoutException("Account fetch timeout");
        }
        if (shouldFail(RATE_LIMIT_FAILURE_RATE)) {
            throw new RateLimitException("Rate limit exceeded on account fetch", 30);
        }
        
        // Validate account ID format
        if (accountId == null || accountId.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid account ID");
        }
        
        BankAccountDto account = BankAccountDto.builder()
            .accountId(accountId)
            .accountType("CHECKING")
            .currency("USD")
            .balance(new BigDecimal(random.nextInt(100000)).setScale(2, BigDecimal.ROUND_HALF_UP))
            .lastUpdated(LocalDateTime.now())
            .build();
        
        logger.info("Account details retrieved: accountId={}, balance={}", accountId, account.getBalance());
        return account;
    }
    
    @Override
    public List<BankTransactionDto> fetchTransactions(String accountId, String accessToken, LocalDateTime from, LocalDateTime to) 
            throws ConnectionTimeoutException, RateLimitException {
        callCount++;
        logger.info("Fetching transactions for account: {} from {} to {}", accountId, from, to);
        
        // Validate token
        if (!isValidToken(accessToken)) {
            throw new ConnectionTimeoutException("Invalid or expired access token");
        }
        
        // Validate parameters
        if (accountId == null || accountId.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid account ID");
        }
        if (from == null || to == null) {
            throw new IllegalArgumentException("Invalid date range");
        }
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("From date must be before to date");
        }
        
        simulateNetworkDelay();
        
        // Simulate failures
        if (shouldFail(TIMEOUT_FAILURE_RATE)) {
            throw new ConnectionTimeoutException("Transaction fetch timeout");
        }
        if (shouldFail(RATE_LIMIT_FAILURE_RATE)) {
            throw new RateLimitException("Rate limit exceeded on transaction fetch", 45);
        }
        
        List<BankTransactionDto> transactions = generateMockTransactions(accountId);
        logger.info("Retrieved {} transactions for account: {}", transactions.size(), accountId);
        return transactions;
    }
    
    @Override
    public boolean validateConnection() {
        logger.info("Validating bank API connection");
        try {
            simulateNetworkDelay();
            boolean isValid = !shouldFail(CONNECTION_FAILURE_RATE);
            logger.info("Connection validation result: {}", isValid);
            return isValid;
        } catch (Exception e) {
            logger.error("Connection validation failed", e);
            return false;
        }
    }
    
    // Private helper methods
    
    private void simulateNetworkDelay() {
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private boolean shouldFail(int failurePercentage) {
        return random.nextInt(100) < failurePercentage;
    }
    
    private String generateMockToken() {
        return "mock_token_" + System.currentTimeMillis() + "_" + random.nextInt(10000);
    }
    
    private boolean isValidToken(String token) {
        return token != null && token.startsWith("mock_token_");
    }
    
    private String maskCredentials(String credentials) {
        if (credentials == null || credentials.length() < 4) {
            return "***";
        }
        return credentials.substring(0, 2) + "***";
    }
    
    private String maskToken(String token) {
        if (token == null || token.length() < 10) {
            return "***";
        }
        return token.substring(0, 10) + "***";
    }
    
    private List<BankTransactionDto> generateMockTransactions(String accountId) {
        List<BankTransactionDto> transactions = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (int i = 0; i < 5; i++) {
            transactions.add(BankTransactionDto.builder()
                .transactionId("TXN_" + System.currentTimeMillis() + "_" + i)
                .accountId(accountId)
                .amount(new BigDecimal(random.nextInt(5000)).setScale(2, BigDecimal.ROUND_HALF_UP))
                .currency("USD")
                .description("Mock transaction " + (i + 1))
                .transactionDate(now.minusDays(i))
                .status("COMPLETED")
                .build());
        }
        
        return transactions;
    }
    
    // Getters for testing purposes
    public int getCallCount() {
        return callCount;
    }
    
    public void resetCallCount() {
        callCount = 0;
    }
    
    public LocalDateTime getLastAuthTime() {
        return lastAuthTime;
    }
}

