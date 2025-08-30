package com.outseer.tms.service;

import com.outseer.tms.dto.AccountDto;
import com.outseer.tms.dto.Response;
import com.outseer.tms.dto.TransactionRequestDto;
import com.outseer.tms.entity.TransactionEntity;
import com.outseer.tms.exception.InsufficientBalanceException;
import com.outseer.tms.exception.UserNotFoundException;
import com.outseer.tms.helper.DateUtil;
import com.outseer.tms.repo.TransactionRepository;
import com.outseer.tms.repo.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
@Slf4j
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    private final AccountService accountService;
    private final Map<String, Object> userLocks = new ConcurrentHashMap<>();

    @Transactional
    @Retryable(
            value = {DataAccessException.class}, // only retry db failures
            maxAttempts = 3,
            backoff = @Backoff(delay = 200, multiplier = 2)
    )
    public Response saveTransactions(TransactionRequestDto transactionRequestDto) {
        Response response;
        String userId = transactionRequestDto.getUserId();
        Object lock = userLocks.computeIfAbsent(userId, k -> new Object());
        synchronized (lock) {//to prevent multiple txn with same user at given time.
            try {
                if (!userRepository.existsById(userId)) {
                    throw new UserNotFoundException("User not found with id: " + userId);
                }

                //validate debit transactions;
                if (transactionRequestDto.getAmount() < 0) {
                    AccountDto accountDto = accountService.findById(userId);
                    if (accountDto.getCurrentBalance() < Math.abs(transactionRequestDto.getAmount())) {
                        throw new InsufficientBalanceException("Insufficient Balance");
                    }
                }
                TransactionEntity transactionEntity = new TransactionEntity();
                transactionEntity.setTransactionId(transactionRequestDto.getTransactionId());
                transactionEntity.setUserId(transactionRequestDto.getUserId());
                transactionEntity.setAmount(transactionRequestDto.getAmount());
                transactionEntity.setTimestamp(DateUtil.parseIsoDateTime(transactionRequestDto.getTimeStamp()));
                transactionRepository.save(transactionEntity);
                response = new Response(true, "Transaction created successfully.");
                return response;
            } catch (UserNotFoundException e) {
                log.info("User not found for this userId:{},transactionId : {}", userId, transactionRequestDto.getTransactionId());
                throw e;
            } catch (InsufficientBalanceException e) {
                log.info("Insufficient Balance Exception for this userId:{} ,transactionId : {}", userId, transactionRequestDto.getTransactionId());
                throw e;
            } catch (Exception e) {
                log.info("Transactionfor this userId:{},transactionId : {} ,Exception:{}", userId, transactionRequestDto.getTransactionId(), e.getMessage());
                return new Response(false, "Transaction creation failed.");
            }
        }
    }


    @Retryable(
            value = {DataAccessException.class}, // only retry db failures
            maxAttempts = 3,
            backoff = @Backoff(delay = 200, multiplier = 2)
    )
    public Page<TransactionRequestDto> findByUserId(String userId, String startDate, String endDate, int page, int size) {
        try {
            if (!userRepository.existsById(userId)) {
                throw new UserNotFoundException("User not found with id: " + userId);
            }
            LocalDateTime startTime = DateUtil.parseIsoDateTime(startDate);
            LocalDateTime endTime = DateUtil.parseIsoDateTime(endDate);
            Pageable pageable = PageRequest.of(page, size);
            Page<TransactionEntity> transactionPage = transactionRepository.findTransactionsBetween(userId, startTime, endTime, pageable);
            return transactionPage.map(t -> {
                TransactionRequestDto transactionRequestDto = new TransactionRequestDto();
                transactionRequestDto.setTransactionId(t.getTransactionId());
                transactionRequestDto.setUserId(t.getUserId());
                transactionRequestDto.setAmount(t.getAmount());
                transactionRequestDto.setTimeStamp(String.valueOf(t.getTimestamp()));
                return transactionRequestDto;
            });
        } catch (UserNotFoundException e) {
            log.info("User not found for this userId:{}", userId);
            throw e;
        } catch (Exception e) {
            log.info("Transaction summary retrieval failed this userId:{},Exception:{}", userId, e.getMessage());
            throw e;
        }

    }

}
