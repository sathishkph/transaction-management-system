package com.outseer.tms.controller;

import com.outseer.tms.dto.AccountDto;
import com.outseer.tms.entity.AccountEntity;
import com.outseer.tms.entity.UserEntity;
import com.outseer.tms.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/account-summary/{userId}")
    public ResponseEntity<AccountDto> getAccountInfo(@PathVariable String userId){
        return ResponseEntity.ok(accountService.findById(userId));
    }


}
