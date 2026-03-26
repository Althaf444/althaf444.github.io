package com.mint.integration;

import com.mint.entity.Bank;
import com.mint.entity.BankConnection;
import com.mint.entity.ConnectionStatus;
import com.mint.entity.SyncStatus;
import com.mint.entity.User;
import com.mint.exception.BankConnectionException;
import com.mint.repository.BankConnectionRepository;
import com.mint.service.bank.BankConnectionService;
import com.mint.service.bank.BankSyncService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for bank connection and synchronization functionality
 * Tests the complete workflow with real repository interactions
 */
@SpringBootTest
@Transactional
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class BankConnectionIntegrationTest {
    
    @Autowired
    private BankConnectionRepository bankConnectionRepository;
    
    @Autowired
    private BankConnectionService bankConnectionService;
    
    @Autowired
    private BankSyncService bankSyncService;

    @PersistenceContext
    private EntityManager entityManager;
    
    private User testUser;
    private Bank testBank;
    
    @BeforeEach
    void setUp() {
        // Persist test user/bank so service-level methods can use real DB records.
        testUser = new User();
        testUser.setUsername("integrationtest_" + System.nanoTime());
        testUser.setEmail("integration_" + System.nanoTime() + "@test.com");
        testUser.setPassword("password123");
        entityManager.persist(testUser);

        testBank = new Bank();
        testBank.setName("Integration Test Bank");
        testBank.setApiEndpoint("https://api.testbank.com");
        entityManager.persist(testBank);
        entityManager.flush();
    }

    @Test
    void testCreateConnectionAndFetchFromRepository() throws BankConnectionException {
        String accountId = "ACC_INTEGRATION_001";
        BankConnection created = bankConnectionService.createConnection(testUser, testBank, accountId);

        assertNotNull(created.getId());
        assertEquals(ConnectionStatus.PENDING, created.getStatus());
        assertEquals(SyncStatus.IDLE, created.getSyncStatus());

        Optional<BankConnection> persisted = bankConnectionService.getConnection(testUser, testBank);
        assertTrue(persisted.isPresent());
        assertEquals(accountId, persisted.get().getAccountId());
    }

    @Test
    void testUpdateSyncStatusPersistsFailureDetails() throws BankConnectionException {
        BankConnection created = bankConnectionService.createConnection(testUser, testBank, "ACC_INTEGRATION_002");

        bankConnectionService.updateSyncStatus(created.getId(), SyncStatus.FAILED, "timeout");
        BankConnection refreshed = bankConnectionRepository.findById(created.getId()).orElseThrow();

        assertEquals(SyncStatus.FAILED, refreshed.getSyncStatus());
        assertEquals("timeout", refreshed.getSyncErrorMessage());
        assertNotNull(refreshed.getLastSyncAttempt());
        assertEquals(1, refreshed.getSyncFailureCount());
    }

    @Test
    void testValidateBankConnectionEndpointIsReachable() {
        // Smoke test: service call should execute without throwing.
        assertDoesNotThrow(() -> bankSyncService.validateBankConnection());
    }
}

