package com.outseer.tms.controller;

import com.outseer.tms.dto.Response;
import com.outseer.tms.dto.TransactionRequestDto;
import com.outseer.tms.dto.UserRequestDto;
import com.outseer.tms.entity.TransactionEntity;
import com.outseer.tms.entity.UserEntity;
import com.outseer.tms.service.TransactionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@AllArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transactions")
    public ResponseEntity<Response> saveTransaction(@RequestBody TransactionRequestDto transactionRequestDto){
        return ResponseEntity.ok(transactionService.saveTransactions(transactionRequestDto));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionRequestDto>> getTransaction(@RequestParam String userId,
                                                                  @RequestParam(required = false) String startDate,
                                                                  @RequestParam(required = false) String endDate){
        return ResponseEntity.ok(transactionService.findByUserId(userId,startDate,endDate));
    }
}
