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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    private final AccountService accountService;

    public Response saveTransactions(TransactionRequestDto transactionRequestDto) {
        Response response;
        String userId = transactionRequestDto.getUserId();
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
            log.info("Transactionfor this userId:{},transactionId : {}", userId, transactionRequestDto.getTransactionId());
            return new Response(false, "Transaction creation failed.");
        }
    }

    public List<TransactionRequestDto> findByUserId(String userId, String startDate, String endDate) {
        try {
            if (!userRepository.existsById(userId)) {
                throw new UserNotFoundException("User not found with id: " + userId);
            }
            LocalDateTime startTime = DateUtil.parseIsoDateTime(startDate);
            LocalDateTime endTime = DateUtil.parseIsoDateTime(endDate);
            List<TransactionEntity> transactionEntity = transactionRepository.findTransactionsBetween(userId, startTime, endTime);
            return transactionEntity.stream().map(t -> {
                TransactionRequestDto transactionRequestDto = new TransactionRequestDto();
                transactionRequestDto.setTransactionId(t.getTransactionId());
                transactionRequestDto.setUserId(t.getUserId());
                transactionRequestDto.setAmount(t.getAmount());
                transactionRequestDto.setTimeStamp(String.valueOf(t.getTimestamp()));
                return transactionRequestDto;
            }).toList();
        } catch ( UserNotFoundException e){
            log.info("User not found for this userId:{}", userId );
            throw e;
        } catch (Exception e){
            log.info("Transaction summary retrieval failed this userId:{}", userId );
            throw e;
        }

    }

}
