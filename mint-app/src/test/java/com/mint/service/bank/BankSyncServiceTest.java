package com.mint.service.bank;

import com.mint.dto.BankAccountDto;
import com.mint.dto.BankTransactionDto;
import com.mint.entity.BankConnection;
import com.mint.entity.ConnectionStatus;
import com.mint.entity.SyncStatus;
import com.mint.exception.ConnectionTimeoutException;
import com.mint.exception.RateLimitException;
import com.mint.service.bank.api.BankApiClient;
import com.mint.test.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

/**
 * Unit tests for BankSyncService
 * Tests synchronization logic, error handling, and edge cases
 */
@ExtendWith(MockitoExtension.class)
class BankSyncServiceTest {
    
    @Mock
    private BankApiClient bankApiClient;
    
    @Mock
    private BankConnectionService bankConnectionService;
    
    private BankSyncService bankSyncService;
    private BankConnection testConnection;
    
    @BeforeEach
    void setUp() {
        bankSyncService = new BankSyncService(bankApiClient, bankConnectionService);
        testConnection = TestDataBuilder.buildBankConnection();
    }
    
    @Test
    void testSuccessfulSynchronization() {
        // Arrange
        String credentials = "test_credentials";
        String mockToken = "mock_token_12345";
        
        BankAccountDto mockAccount = BankAccountDto.builder()
            .accountId(testConnection.getAccountId())
            .accountType("CHECKING")
            .currency("USD")
            .balance(new BigDecimal("5000.00"))
            .lastUpdated(LocalDateTime.now())
            .build();

        List<BankTransactionDto> mockTransactions = new ArrayList<>();
        mockTransactions.add(BankTransactionDto.builder()
            .transactionId("TXN_001")
            .accountId(testConnection.getAccountId())
            .amount(new BigDecimal("100.00"))
            .currency("USD")
            .description("Test transaction")
            .transactionDate(LocalDateTime.now())
            .status("COMPLETED")
            .build());

        // Mock API calls
        when(bankApiClient.authenticate(credentials)).thenReturn(mockToken);
        when(bankApiClient.fetchAccount(testConnection.getAccountId(), mockToken)).thenReturn(mockAccount);
        when(bankApiClient.fetchTransactions(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), 
            ArgumentMatchers.any(LocalDateTime.class), ArgumentMatchers.any(LocalDateTime.class)))
            .thenReturn(mockTransactions);
        when(bankConnectionService.updateSyncStatus(ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.any()))
            .thenReturn(testConnection);
        // Only mock updateConnectionStatus if it's actually called (PENDING -> LINKED transition)
        lenient().when(bankConnectionService.updateConnectionStatus(ArgumentMatchers.anyLong(), ArgumentMatchers.any()))
            .thenReturn(testConnection);
        
        // Act
        boolean result = bankSyncService.synchronizeConnection(testConnection, credentials);
        
        // Assert
        assertTrue(result, "Synchronization should succeed");
        verify(bankApiClient).authenticate(credentials);
        verify(bankApiClient).fetchAccount(testConnection.getAccountId(), mockToken);
    }
    
    @Test
    void testSynchronizationWithConnectionTimeout() {
        // Arrange
        String credentials = "test_credentials";
        when(bankApiClient.authenticate(credentials))
            .thenThrow(new ConnectionTimeoutException("API timeout"));
        when(bankConnectionService.updateSyncStatus(ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.any()))
            .thenReturn(testConnection);
        
        // Act
        boolean result = bankSyncService.synchronizeConnection(testConnection, credentials);
        
        // Assert
        assertFalse(result, "Synchronization should fail on timeout");
        verify(bankConnectionService).updateSyncStatus(ArgumentMatchers.anyLong(), ArgumentMatchers.eq(SyncStatus.FAILED), ArgumentMatchers.anyString());
    }
    
    @Test
    void testSynchronizationWithRateLimit() {
        // Arrange
        String credentials = "test_credentials";
        when(bankApiClient.authenticate(credentials))
            .thenThrow(new RateLimitException("Rate limit exceeded", 60));
        when(bankConnectionService.updateSyncStatus(ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.any()))
            .thenReturn(testConnection);
        
        // Act
        boolean result = bankSyncService.synchronizeConnection(testConnection, credentials);
        
        // Assert
        assertFalse(result, "Synchronization should fail on rate limit");
        verify(bankConnectionService).updateSyncStatus(ArgumentMatchers.anyLong(), ArgumentMatchers.eq(SyncStatus.FAILED), ArgumentMatchers.anyString());
    }
    
    @Test
    void testSynchronizationWithInvalidAccount() {
        // Arrange
        String credentials = "test_credentials";
        String mockToken = "mock_token_12345";
        
        // Account with missing ID
        BankAccountDto invalidAccount = BankAccountDto.builder()
            .accountId(null) // Missing account ID
            .accountType("CHECKING")
            .currency("USD")
            .balance(new java.math.BigDecimal("5000.00"))
            .lastUpdated(java.time.LocalDateTime.now())
            .build();

        when(bankApiClient.authenticate(credentials)).thenReturn(mockToken);
        when(bankApiClient.fetchAccount(testConnection.getAccountId(), mockToken)).thenReturn(invalidAccount);
        when(bankConnectionService.updateSyncStatus(ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.any()))
            .thenReturn(testConnection);
        
        // Act
        boolean result = bankSyncService.synchronizeConnection(testConnection, credentials);
        
        // Assert
        assertFalse(result, "Synchronization should fail with invalid account");
        verify(bankConnectionService).updateSyncStatus(ArgumentMatchers.anyLong(), ArgumentMatchers.eq(SyncStatus.FAILED), ArgumentMatchers.anyString());
    }
    
    @Test
    void testSynchronizationWithNegativeBalance() {
        // Arrange
        String credentials = "test_credentials";
        String mockToken = "mock_token_12345";
        
        // Account with negative balance (not a credit account)
        BankAccountDto invalidAccount = BankAccountDto.builder()
            .accountId(testConnection.getAccountId())
            .accountType("CHECKING")
            .currency("USD")
            .balance(new java.math.BigDecimal("-5000.00")) // Negative balance
            .lastUpdated(java.time.LocalDateTime.now())
            .build();

        when(bankApiClient.authenticate(credentials)).thenReturn(mockToken);
        when(bankApiClient.fetchAccount(testConnection.getAccountId(), mockToken)).thenReturn(invalidAccount);
        when(bankConnectionService.updateSyncStatus(ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.any()))
            .thenReturn(testConnection);
        
        // Act
        boolean result = bankSyncService.synchronizeConnection(testConnection, credentials);
        
        // Assert
        assertFalse(result, "Synchronization should fail with negative balance on checking account");
        verify(bankConnectionService).updateSyncStatus(ArgumentMatchers.anyLong(), ArgumentMatchers.eq(SyncStatus.FAILED), ArgumentMatchers.anyString());
    }
    
    @Test
    void testSynchronizationWithInvalidTransactions() {
        // Arrange
        String credentials = "test_credentials";
        String mockToken = "mock_token_12345";
        
        BankAccountDto mockAccount = BankAccountDto.builder()
            .accountId(testConnection.getAccountId())
            .accountType("CHECKING")
            .currency("USD")
            .balance(new java.math.BigDecimal("5000.00"))
            .lastUpdated(java.time.LocalDateTime.now())
            .build();

        // Transaction with negative amount
        java.util.List<BankTransactionDto> invalidTransactions = new java.util.ArrayList<>();
        invalidTransactions.add(BankTransactionDto.builder()
            .transactionId("TXN_001")
            .accountId(testConnection.getAccountId())
            .amount(new java.math.BigDecimal("-100.00")) // Negative amount
            .currency("USD")
            .description("Invalid transaction")
            .transactionDate(java.time.LocalDateTime.now())
            .status("COMPLETED")
            .build());

        when(bankApiClient.authenticate(credentials)).thenReturn(mockToken);
        when(bankApiClient.fetchAccount(testConnection.getAccountId(), mockToken)).thenReturn(mockAccount);
        when(bankApiClient.fetchTransactions(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), 
            ArgumentMatchers.any(java.time.LocalDateTime.class), ArgumentMatchers.any(java.time.LocalDateTime.class)))
            .thenReturn(invalidTransactions);
        when(bankConnectionService.updateSyncStatus(ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.any()))
            .thenReturn(testConnection);
        
        // Act
        boolean result = bankSyncService.synchronizeConnection(testConnection, credentials);
        
        // Assert
        assertFalse(result, "Synchronization should fail with invalid transaction");
        verify(bankConnectionService).updateSyncStatus(ArgumentMatchers.anyLong(), ArgumentMatchers.eq(SyncStatus.FAILED), ArgumentMatchers.anyString());
    }

    @Test
    void testSynchronizationWithNullConnectionReturnsFalse() {
        boolean result = bankSyncService.synchronizeConnection(null, "credentials");
        assertFalse(result, "Null connection should fail fast and return false");
        verifyNoInteractions(bankConnectionService);
    }

    @Test
    void testSynchronizationWithMissingCredentials() {
        when(bankConnectionService.updateSyncStatus(ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.any()))
            .thenReturn(testConnection);

        boolean result = bankSyncService.synchronizeConnection(testConnection, "   ");

        assertFalse(result, "Missing credentials should fail synchronization");
        verify(bankConnectionService).updateSyncStatus(
            ArgumentMatchers.eq(testConnection.getId()),
            ArgumentMatchers.eq(SyncStatus.FAILED),
            ArgumentMatchers.contains("Credentials are missing")
        );
    }

    @Test
    void testSynchronizationDisablesConnectionAfterMaxFailures() {
        String credentials = "test_credentials";
        when(bankApiClient.authenticate(credentials)).thenThrow(new ConnectionTimeoutException("API timeout"));
        when(bankConnectionService.updateSyncStatus(ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.any()))
            .thenReturn(testConnection);

        BankConnection refreshedConnection = TestDataBuilder.buildBankConnection();
        refreshedConnection.setId(testConnection.getId());
        refreshedConnection.setSyncFailureCount(5);
        when(bankConnectionService.getConnection(testConnection.getId())).thenReturn(Optional.of(refreshedConnection));

        boolean result = bankSyncService.synchronizeConnection(testConnection, credentials);

        assertFalse(result, "Synchronization should fail due to timeout");
        verify(bankConnectionService).updateConnectionStatus(testConnection.getId(), ConnectionStatus.FAILED);
    }
    
    @Test
    void testSynchronizationConcurrencyCheck() {
        // Note: The concurrency check in BankSyncService checks if sync is already in progress
        // by verifying getSyncStatus() == SYNCING before the service updates it to SYNCING.
        // For a real concurrency scenario, the connection object would need to be modified 
        // by another thread between the check and the update.
        // This test instead verifies the logic path that would catch concurrency issues
        BankConnection syncingConnection = TestDataBuilder.buildBankConnection();
        syncingConnection.setId(1L);
        syncingConnection.setSyncStatus(SyncStatus.SYNCING);
        String credentials = "test_credentials";
        when(bankConnectionService.updateSyncStatus(ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.any()))
            .thenReturn(syncingConnection);
        
        // The service handles BankSyncException internally and returns false.
        boolean result = bankSyncService.synchronizeConnection(syncingConnection, credentials);

        assertFalse(result, "Should prevent concurrent synchronization attempts");
        verify(bankConnectionService).updateSyncStatus(
            ArgumentMatchers.eq(1L),
            ArgumentMatchers.eq(SyncStatus.FAILED),
            ArgumentMatchers.contains("already in progress")
        );
    }
    
    @Test
    void testBankConnectionValidation() {
        // Arrange
        when(bankApiClient.validateConnection()).thenReturn(true);
        
        // Act
        boolean isValid = bankSyncService.validateBankConnection();
        
        // Assert
        assertTrue(isValid, "Bank connection should be valid");
        verify(bankApiClient).validateConnection();
    }
    
    @Test
    void testBankConnectionValidationFailure() {
        // Arrange
        when(bankApiClient.validateConnection()).thenReturn(false);
        
        // Act
        boolean isValid = bankSyncService.validateBankConnection();
        
        // Assert
        assertFalse(isValid, "Bank connection should be invalid");
        verify(bankApiClient).validateConnection();
    }
}

