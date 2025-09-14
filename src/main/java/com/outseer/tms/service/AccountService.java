package com.outseer.tms.service;

import com.outseer.tms.dto.AccountDto;
import com.outseer.tms.exception.UserNotFoundException;
import com.outseer.tms.repo.TransactionRepository;
import com.outseer.tms.repo.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
@Slf4j
public class AccountService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Retryable(
            value = {DataAccessException.class}, // only retry db failures
            maxAttempts = 3,
            backoff = @Backoff(delay = 200, multiplier = 2)
    )
    public AccountDto findById(String userId) {
        try {
            if (!userRepository.existsById(userId)) {
                throw new UserNotFoundException("User not found with id: " + userId);
            }
            AccountDto accountDto = transactionRepository.findBalanceAndCountByUserId(userId);
            if (accountDto == null){
                return new AccountDto(userId,0.0,0L);
            }
            return accountDto;
        } catch (UserNotFoundException e) {
            log.info("User not found for this userId:{},transactionId : {}", userId);
            throw e;
        } catch (Exception e) {
            log.info("Account summary retrival failed this userId:{} Exception :{}", userId, e.getMessage());
            throw e;
        }

    }
}
