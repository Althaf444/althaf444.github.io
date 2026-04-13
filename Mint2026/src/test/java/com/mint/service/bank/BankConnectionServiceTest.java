package com.mint.service.bank;

import com.mint.entity.Bank;
import com.mint.entity.BankConnection;
import com.mint.entity.ConnectionStatus;
import com.mint.entity.SyncStatus;
import com.mint.entity.User;
import com.mint.exception.BankConnectionException;
import com.mint.repository.BankConnectionRepository;
import com.mint.test.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for BankConnectionService
 * Tests connection management, state transitions, and error handling
 */
@ExtendWith(MockitoExtension.class)
class BankConnectionServiceTest {
    
    @Mock
    private BankConnectionRepository bankConnectionRepository;
    
    private BankConnectionService service;
    private User testUser;
    private Bank testBank;
    
    @BeforeEach
    void setUp() {
        service = new BankConnectionService(bankConnectionRepository);
        testUser = TestDataBuilder.buildUser();
        testBank = TestDataBuilder.buildBank();
    }
    
    @Test
    void testCreateConnectionSuccess() {
        // Arrange
        String accountId = "ACC_123456";
        when(bankConnectionRepository.findByUserAndBank(testUser, testBank)).thenReturn(Optional.empty());
        when(bankConnectionRepository.save(any(BankConnection.class)))
            .thenAnswer(invocation -> {
                BankConnection connection = invocation.getArgument(0);
                connection.setId(1L);
                return connection;
            });
        
        // Act
        BankConnection created = service.createConnection(testUser, testBank, accountId);
        
        // Assert
        assertNotNull(created);
        assertEquals(testUser, created.getUser());
        assertEquals(testBank, created.getBank());
        assertEquals(accountId, created.getAccountId());
        assertEquals(ConnectionStatus.PENDING, created.getStatus());
        assertEquals(SyncStatus.IDLE, created.getSyncStatus());
        assertEquals(0, created.getSyncFailureCount());
        verify(bankConnectionRepository).save(any(BankConnection.class));
    }
    
    @Test
    void testCreateConnectionDuplicate() {
        // Arrange
        String accountId = "ACC_123456";
        BankConnection existing = TestDataBuilder.buildBankConnection(testUser, testBank, accountId, ConnectionStatus.LINKED);
        when(bankConnectionRepository.findByUserAndBank(testUser, testBank))
            .thenReturn(Optional.of(existing));
        
        // Act & Assert
        assertThrows(BankConnectionException.class, () -> {
            service.createConnection(testUser, testBank, accountId);
        });
    }
    
    @Test
    void testCreateConnectionWithNullUser() {
        // Act & Assert
        assertThrows(BankConnectionException.class, () -> {
            service.createConnection(null, testBank, "ACC_123456");
        });
    }
    
    @Test
    void testCreateConnectionWithNullBank() {
        // Act & Assert
        assertThrows(BankConnectionException.class, () -> {
            service.createConnection(testUser, null, "ACC_123456");
        });
    }
    
    @Test
    void testCreateConnectionWithEmptyAccountId() {
        // Act & Assert
        assertThrows(BankConnectionException.class, () -> {
            service.createConnection(testUser, testBank, "");
        });
    }
    
    @Test
    void testUpdateConnectionStatus() {
        // Arrange
        BankConnection connection = TestDataBuilder.buildBankConnection();
        when(bankConnectionRepository.findById(1L)).thenReturn(Optional.of(connection));
        when(bankConnectionRepository.save(connection)).thenReturn(connection);
        
        // Act
        BankConnection updated = service.updateConnectionStatus(1L, ConnectionStatus.LINKED);
        
        // Assert
        assertEquals(ConnectionStatus.LINKED, updated.getStatus());
        verify(bankConnectionRepository).findById(1L);
        verify(bankConnectionRepository).save(connection);
    }
    
    @Test
    void testUpdateSyncStatusSuccess() {
        // Arrange
        BankConnection connection = TestDataBuilder.buildBankConnection();
        connection.setSyncFailureCount(2);
        when(bankConnectionRepository.findById(1L)).thenReturn(Optional.of(connection));
        when(bankConnectionRepository.save(connection)).thenReturn(connection);
        
        // Act
        BankConnection updated = service.updateSyncStatus(1L, SyncStatus.SUCCESS, null);
        
        // Assert
        assertEquals(SyncStatus.SUCCESS, updated.getSyncStatus());
        assertEquals(0, updated.getSyncFailureCount()); // Should reset on success
        assertNull(updated.getSyncErrorMessage());
        assertNotNull(updated.getLastSyncDate());
        verify(bankConnectionRepository).save(connection);
    }
    
    @Test
    void testUpdateSyncStatusFailed() {
        // Arrange
        BankConnection connection = TestDataBuilder.buildBankConnection();
        connection.setSyncFailureCount(1);
        when(bankConnectionRepository.findById(1L)).thenReturn(Optional.of(connection));
        when(bankConnectionRepository.save(connection)).thenReturn(connection);
        
        // Act
        String errorMsg = "Connection timeout";
        BankConnection updated = service.updateSyncStatus(1L, SyncStatus.FAILED, errorMsg);
        
        // Assert
        assertEquals(SyncStatus.FAILED, updated.getSyncStatus());
        assertEquals(2, updated.getSyncFailureCount()); // Should increment
        assertEquals(errorMsg, updated.getSyncErrorMessage());
        verify(bankConnectionRepository).save(connection);
    }
    
    @Test
    void testGetUserConnections() {
        // Arrange
        List<BankConnection> connections = new ArrayList<>();
        connections.add(TestDataBuilder.buildBankConnection());
        when(bankConnectionRepository.findByUser(testUser)).thenReturn(connections);
        
        // Act
        List<BankConnection> result = service.getUserConnections(testUser);
        
        // Assert
        assertEquals(1, result.size());
        verify(bankConnectionRepository).findByUser(testUser);
    }
    
    @Test
    void testGetConnection() {
        // Arrange
        BankConnection connection = TestDataBuilder.buildBankConnection();
        when(bankConnectionRepository.findByUserAndBank(testUser, testBank))
            .thenReturn(Optional.of(connection));
        
        // Act
        Optional<BankConnection> result = service.getConnection(testUser, testBank);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals(connection, result.get());
        verify(bankConnectionRepository).findByUserAndBank(testUser, testBank);
    }
    
    @Test
    void testDeleteConnection() {
        // Arrange
        BankConnection connection = TestDataBuilder.buildBankConnection();
        when(bankConnectionRepository.findById(1L)).thenReturn(Optional.of(connection));
        
        // Act
        service.deleteConnection(1L);
        
        // Assert
        verify(bankConnectionRepository).findById(1L);
        verify(bankConnectionRepository).delete(connection);
    }
    
    @Test
    void testDeleteNonexistentConnection() {
        // Arrange
        when(bankConnectionRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(BankConnectionException.class, () -> {
            service.deleteConnection(999L);
        });
    }
    
    @Test
    void testIsConnectionValid() throws BankConnectionException {
        // Arrange
        BankConnection connection = TestDataBuilder.buildBankConnection();
        connection.setStatus(ConnectionStatus.LINKED);
        when(bankConnectionRepository.findById(1L)).thenReturn(Optional.of(connection));
        
        // Act
        boolean isValid = service.isConnectionValid(1L);
        
        // Assert
        assertTrue(isValid);
    }
    
    @Test
    void testIsConnectionInvalid() throws BankConnectionException {
        // Arrange
        BankConnection connection = TestDataBuilder.buildBankConnection();
        connection.setStatus(ConnectionStatus.FAILED);
        when(bankConnectionRepository.findById(1L)).thenReturn(Optional.of(connection));
        
        // Act
        boolean isValid = service.isConnectionValid(1L);
        
        // Assert
        assertFalse(isValid);
    }
    
    @Test
    void testGetConnectionsNeedingSync() {
        // Arrange
        List<BankConnection> connections = new ArrayList<>();
        connections.add(TestDataBuilder.buildBankConnection());
        when(bankConnectionRepository.findByStatusAndLastSyncDateBefore(any(), any()))
            .thenReturn(connections);
        
        // Act
        List<BankConnection> result = service.getConnectionsNeedingSync(24);
        
        // Assert
        assertEquals(1, result.size());
        verify(bankConnectionRepository).findByStatusAndLastSyncDateBefore(any(), any());
    }
}

