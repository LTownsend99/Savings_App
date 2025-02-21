package com.example.savings_app.controller;

import com.example.savings_app.model.Account;
import com.example.savings_app.model.LoginRequest;
import com.example.savings_app.service.AccountService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller to handle account-related operations. Provides endpoints for account creation,
 * retrieval, login, and deletion.
 */
@RestController
public class AccountController {

  private final AccountService accountService;

  @Autowired
  public AccountController(AccountService accountService) {
    this.accountService = accountService;
  }

  /**
   * Endpoint to create a new account.
   *
   * @param account the account details provided in the request body.
   * @return the created account.
   */
  @PostMapping("/account/create")
  public Account createAccount(@RequestBody Account account) {
    // Delegates the creation of the account to the service layer
    return accountService.createAccount(account);
  }

  /**
   * Endpoint to retrieve an account by its user ID.
   *
   * @param userId the user ID of the account to be retrieved.
   * @return a ResponseEntity containing the account if found, or a 404 Not Found if not found.
   */
  @GetMapping("/account/id/{userId}")
  public ResponseEntity<Account> getAccountByUserId(@PathVariable int userId) {
    // Attempts to retrieve the account by user ID
    Optional<Account> accountOptional = accountService.getAccountByUserId(userId);

    // If account exists, return 200 OK status with account details
    if (accountOptional.isPresent()) {
      return ResponseEntity.ok(accountOptional.get());
    } else {
      // If no account found, return 404 Not Found status
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Endpoint to handle account login. Verifies the email and password and returns the account if
   * valid.
   *
   * @param loginRequest contains the email and password provided by the user.
   * @return a ResponseEntity containing the account if login is successful, or a 403 Forbidden
   *     status if the password is incorrect.
   */
  @PostMapping("/account/login")
  public ResponseEntity<Account> login(@RequestBody LoginRequest loginRequest) {
    // Retrieves account by email
    Optional<Account> accountOptional = accountService.getAccountByEmail(loginRequest.getEmail());

    // If the account is found
    if (accountOptional.isPresent()) {
      Account account = accountOptional.get();

      // Validate the password using a regex match against the stored hash
      boolean isPasswordValid = loginRequest.getPassword().matches(account.getPasswordHash());

      // If the password matches, return the account details
      if (isPasswordValid) {
        return ResponseEntity.ok(account);
      } else {
        // If password doesn't match, return 403 Forbidden status
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
      }
    } else {
      // If no account is found with the provided email, return 404 Not Found status
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Endpoint to delete an account by its user ID.
   *
   * @param userId the user ID of the account to be deleted.
   * @return a ResponseEntity with a success message or error message.
   */
  @DeleteMapping("/account/id/{userId}")
  public ResponseEntity<String> deleteAccount(@PathVariable int userId) {
    try {
      // Calls the service to delete the account
      accountService.deleteAccount(userId);
      return ResponseEntity.ok("Account with ID " + userId + " deleted successfully.");
    } catch (IllegalArgumentException e) {
      // If an invalid argument error occurs, return a 400 Bad Request status with the error message
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      // Catch any other unexpected exceptions and return a 500 Internal Server Error status
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An unexpected error occurred: " + e.getMessage());
    }
  }
}
