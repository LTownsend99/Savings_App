package com.example.savings_app.controller;

import com.example.savings_app.model.Account;
import com.example.savings_app.model.LoginRequest;
import com.example.savings_app.service.AccountService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AccountController {

  private final AccountService accountService;

  @Autowired
  public AccountController(AccountService accountService) {
    this.accountService = accountService;
  }

  @PostMapping("/account/create")
  public Account createAccount(@RequestBody Account account) {
    return accountService.createAccount(account);
  }

  @GetMapping("/account/id/{userId}")
  public ResponseEntity<Account> getAccountByUserId(@PathVariable int userId) {
    Optional<Account> accountOptional = accountService.getAccountByUserId(userId);

    if (accountOptional.isPresent()) {
      return ResponseEntity.ok(accountOptional.get());
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @PostMapping("/account/login")
  public ResponseEntity<Account> login(@RequestBody LoginRequest loginRequest) {
    Optional<Account> accountOptional = accountService.getAccountByEmail(loginRequest.getEmail());

    if (accountOptional.isPresent()) {
      Account account = accountOptional.get();

      boolean isPasswordValid = loginRequest.getPassword().matches(account.getPasswordHash());

      if (isPasswordValid) {
        return ResponseEntity.ok(account); // Return account if login is successful
      } else {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // Password mismatch
      }
    } else {
      return ResponseEntity.notFound().build(); // Email not found
    }
  }

  @DeleteMapping("/account/id/{userId}")
  public ResponseEntity<String> deleteAccount(@PathVariable int userId) {
    try {
      accountService.deleteAccount(userId);
      return ResponseEntity.ok("Account with ID " + userId + " deleted successfully.");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An unexpected error occurred: " + e.getMessage());
    }
  }
}
