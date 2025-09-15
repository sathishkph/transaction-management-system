package com.outseer.tms.service;

import com.outseer.tms.dto.AccountDto;
import com.outseer.tms.exception.UserNotFoundException;
import com.outseer.tms.repo.TransactionRepository;
import com.outseer.tms.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccountServiceTest {

    private TransactionRepository transactionRepository;
    private UserRepository userRepository;
    private AccountService accountService;

    private static final String VALID_USER_ID = "sathish1234";
    private static final String INVALID_USER_ID = "unknown";

    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        userRepository = mock(UserRepository.class);
        accountService = new AccountService(transactionRepository, userRepository);
    }

    @Test
    void testFindById_Success() {
        AccountDto dto = new AccountDto(VALID_USER_ID, BigDecimal.valueOf(1000.0), 3L);

        when(userRepository.existsById(VALID_USER_ID)).thenReturn(true);
        when(transactionRepository.findBalanceAndCountByUserId(VALID_USER_ID)).thenReturn(dto);

        AccountDto result = accountService.findById(VALID_USER_ID);

        assertNotNull(result);
        assertEquals(VALID_USER_ID, result.getUserId());
        assertEquals(BigDecimal.valueOf(1000.0), result.getCurrentBalance());
        assertEquals(3, result.getTransactionCount());
    }

    @Test
    void testFindById_UserNotFound() {
        when(userRepository.existsById(INVALID_USER_ID)).thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> accountService.findById(INVALID_USER_ID));

        verify(transactionRepository, never()).findBalanceAndCountByUserId(anyString());
    }
}
