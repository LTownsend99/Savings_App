package com.example.savings_app.controller;

import com.example.savings_app.service.AccountService;
import com.example.savings_app.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class AccountController {


    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public Account createAccount(@RequestBody Account account) {
        return accountService.createAccount(account);
    }

    @GetMapping("/account/{userId}")
    public ResponseEntity<Account> getAccountByUserId(@PathVariable int userId) {
        Optional<Account> accountOptional = accountService.getAccountByUserId(userId);

        if (accountOptional.isPresent()) {
            return ResponseEntity.ok(accountOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
