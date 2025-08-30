package com.outseer.tms.service;

import com.outseer.tms.dto.AccountDto;
import com.outseer.tms.dto.Response;
import com.outseer.tms.dto.TransactionRequestDto;
import com.outseer.tms.entity.TransactionEntity;
import com.outseer.tms.exception.DuplicateTransactionIdException;
import com.outseer.tms.exception.InsufficientBalanceException;
import com.outseer.tms.exception.UserNotFoundException;
import com.outseer.tms.repo.TransactionRepository;
import com.outseer.tms.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    private TransactionRepository transactionRepository;
    private UserRepository userRepository;
    private AccountService accountService;
    private TransactionService transactionService;

    private static final String VALID_USER_ID = "sathish1234";
    private static final String INVALID_USER_ID = "unknownUser";

    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        userRepository = mock(UserRepository.class);
        accountService = mock(AccountService.class);

        transactionService = new TransactionService(
                transactionRepository,
                userRepository,
                accountService
        );
    }

    @Test
    void testSaveTransaction_Credit_Success() {
        TransactionRequestDto request = new TransactionRequestDto("t1", VALID_USER_ID, 200.0, "2025-08-29T12:00:00");

        when(userRepository.existsById(VALID_USER_ID)).thenReturn(true);

        Response response = transactionService.saveTransactions(request);

        assertTrue(response.isSuccess());
        assertEquals("Transaction created successfully.", response.getMessage());
        verify(transactionRepository, times(1)).save(any(TransactionEntity.class));
    }

    @Test
    void testSaveTransaction_Debit_SufficientBalance() {
        TransactionRequestDto request = new TransactionRequestDto("t2", VALID_USER_ID, -50.0, "2025-08-29T12:00:00");

        when(userRepository.existsById(VALID_USER_ID)).thenReturn(true);
        when(accountService.findById(VALID_USER_ID)).thenReturn(new AccountDto(VALID_USER_ID, 200.0, 3L));

        Response response = transactionService.saveTransactions(request);

        assertTrue(response.isSuccess());
        assertEquals("Transaction created successfully.", response.getMessage());
        verify(transactionRepository, times(1)).save(any(TransactionEntity.class));
    }

    @Test
    void testSaveTransaction_Debit_InsufficientBalance() {
        TransactionRequestDto request = new TransactionRequestDto("t3", VALID_USER_ID, -500.0, "2025-08-29T12:00:00");

        when(userRepository.existsById(VALID_USER_ID)).thenReturn(true);
        when(accountService.findById(VALID_USER_ID)).thenReturn(new AccountDto(VALID_USER_ID, 100.0, 2L));

        assertThrows(InsufficientBalanceException.class,
                () -> transactionService.saveTransactions(request));

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void testSaveTransaction_Duplicate_Transaction() {
        TransactionRequestDto request = new TransactionRequestDto("t3", VALID_USER_ID, -500.0, "2025-08-29T12:00:00");

        when(userRepository.existsById(VALID_USER_ID)).thenReturn(true);
        when(transactionRepository.existsById("t3")).thenReturn(true);
        when(accountService.findById(VALID_USER_ID)).thenReturn(new AccountDto(VALID_USER_ID, 100.0, 2L));

        assertThrows(DuplicateTransactionIdException.class,
                () -> transactionService.saveTransactions(request));

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void testSaveTransaction_UserNotFound() {
        TransactionRequestDto request = new TransactionRequestDto("t4", INVALID_USER_ID, 100.0, "2025-08-29T12:00:00");

        when(userRepository.existsById(INVALID_USER_ID)).thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> transactionService.saveTransactions(request));

        verify(transactionRepository, never()).save(any());
    }

    @Test
    void testSaveTransaction_UnexpectedException() {
        TransactionRequestDto request = new TransactionRequestDto("t5", VALID_USER_ID, 100.0, "2025-08-29T12:00:00");

        when(userRepository.existsById(VALID_USER_ID)).thenReturn(true);
        doThrow(new RuntimeException("DB failure"))
                .when(transactionRepository).save(any(TransactionEntity.class));

        Response response = transactionService.saveTransactions(request);

        assertFalse(response.isSuccess());
        assertEquals("Transaction creation failed.", response.getMessage());
    }
}
