package com.outseer.tms.service;

import com.outseer.tms.dto.AccountDto;
import com.outseer.tms.dto.Response;
import com.outseer.tms.entity.AccountEntity;
import com.outseer.tms.entity.TransactionEntity;
import com.outseer.tms.exception.UserNotFoundException;
import com.outseer.tms.repo.AccountRepository;
import com.outseer.tms.repo.TransactionRepository;
import com.outseer.tms.repo.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class AccountService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public AccountDto findById(String userId) {
        try {
            if (!userRepository.existsById(userId)) {
                throw new UserNotFoundException("User not found with id: " + userId);
            }

            List<TransactionEntity> transactionEntityList = transactionRepository.findTransactionsBetween(userId, null, null);

            double currentBalance = transactionEntityList.stream()
                    .mapToDouble(TransactionEntity::getAmount)
                    .sum();
            int transactionCount = transactionEntityList.size();
            AccountDto dto = new AccountDto();
            dto.setUserId(userId);
            dto.setCurrentBalance(currentBalance);
            dto.setTransactionCount(transactionCount);

            return dto;
        } catch ( UserNotFoundException e){
            log.info("User not found for this userId:{},transactionId : {}", userId );
            throw e;
        } catch (Exception e){
            log.info("Account summary retrival failed this userId:{}", userId );
            throw e;
        }

    }
}
