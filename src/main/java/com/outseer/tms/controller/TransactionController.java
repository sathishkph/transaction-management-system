package com.outseer.tms.controller;

import com.outseer.tms.dto.Response;
import com.outseer.tms.dto.TransactionRequestDto;
import com.outseer.tms.service.TransactionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transactions")
    public ResponseEntity<Response> saveTransaction(@Valid @RequestBody TransactionRequestDto transactionRequestDto){
        return ResponseEntity.ok(transactionService.saveTransactions(transactionRequestDto));
    }

    @GetMapping("/transactions")
    public ResponseEntity<Page<TransactionRequestDto>> getTransaction(@RequestParam String userId,
                                                                      @RequestParam(required = false) String startDate,
                                                                      @RequestParam(required = false) String endDate,
                                                                      @RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "10") int size)
    {
        return ResponseEntity.ok(transactionService.findByUserId(userId,startDate,endDate,page,size));
    }
}
