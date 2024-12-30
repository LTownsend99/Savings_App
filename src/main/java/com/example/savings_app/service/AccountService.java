package com.example.savings_app.service;

import com.example.savings_app.model.Account;
import com.example.savings_app.repository.AccountRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

  private final AccountRepository accountRepository;

  @Autowired
  public AccountService(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  public Account createAccount(Account account) {
    // Validate required fields

    if (account.getFirstName() == null || account.getFirstName().isEmpty()) {
      throw new IllegalArgumentException("First Name is required");
    }

    if (account.getLastName() == null || account.getLastName().isEmpty()) {
      throw new IllegalArgumentException("Last Name is required");
    }

    if (account.getEmail() == null || account.getEmail().isEmpty()) {
      throw new IllegalArgumentException("Email is required");
    }

    if (account.getPasswordHash() == null || account.getPasswordHash().isEmpty()) {
      throw new IllegalArgumentException("Password is required");
    }

    if (account.getDob() == null) {
      throw new IllegalArgumentException("DOB is required");
    }

    // Check if an account with the same email already exists
    Optional<Account> existingAccount = accountRepository.findByEmail(account.getEmail());
    if (existingAccount.isPresent()) {
      throw new IllegalStateException("An account with this email already exists");
    }

    try {
      // Save the account to the repository
      Account savedAccount = accountRepository.save(account);
      System.out.println("Account created with ID: " + savedAccount.getUserId());
      return savedAccount;
    } catch (DataIntegrityViolationException e) {
      // Handle potential database constraint violations
      throw new IllegalStateException("Failed to create account due to data integrity issues", e);
    } catch (Exception e) {
      // Handle unexpected errors
      throw new RuntimeException("Failed to create account", e);
    }
  }

  public Optional<Account> getAccountByUserId(int userId) {

    try {
      return accountRepository.findById(userId);
    } catch (IllegalArgumentException e) {
      // Handle the case where the provided ID is invalid
      throw new IllegalArgumentException("Invalid account userId: " + userId, e);
    } catch (Exception e) {
      // Catch any unexpected exceptions
      throw new RuntimeException("Failed to retrieve account with userId: " + userId, e);
    }
  }

  public void deleteAccount(int userId) {
    try {
      accountRepository.deleteById(userId);
    } catch (IllegalArgumentException e) {
      // Handle the case where the provided ID is invalid
      throw new IllegalArgumentException("Invalid account userId: " + userId, e);
    }
  }

  public Optional<Account> getAccountByEmail(String email) {

    try {
      return accountRepository.findByEmail(email);
    } catch (IllegalArgumentException e) {
      // Handle the case where the provided ID is invalid
      throw new IllegalArgumentException("Invalid account email: " + email, e);
    } catch (Exception e) {
      // Catch any unexpected exceptions
      throw new RuntimeException("Failed to retrieve account with email: " + email, e);
    }
  }

  public Optional<Account> updateAccount(int userId, Account updatedAccount) {
    // Fetch the existing account from the database
    Optional<Account> existingAccountOpt = accountRepository.findById(userId);

    if (existingAccountOpt.isEmpty()) {
      return Optional.empty(); // Return empty if account doesn't exist
    }

    Account existingAccount = existingAccountOpt.get();
    boolean hasChanges = false;

    // Check each field for changes and update if needed
    if (!existingAccount.getFirstName().equals(updatedAccount.getFirstName())) {
      existingAccount.setFirstName(updatedAccount.getFirstName());
      hasChanges = true;
    }

    if (!existingAccount.getLastName().equals(updatedAccount.getLastName())) {
      existingAccount.setLastName(updatedAccount.getLastName());
      hasChanges = true;
    }

    if (!existingAccount.getEmail().equals(updatedAccount.getEmail())) {
      existingAccount.setEmail(updatedAccount.getEmail());
      hasChanges = true;
    }

    if (!existingAccount.getPasswordHash().equals(updatedAccount.getPasswordHash())) {
      existingAccount.setPasswordHash(updatedAccount.getPasswordHash());
      hasChanges = true;
    }

    if (!existingAccount.getRole().equals(updatedAccount.getRole())) {
      existingAccount.setRole(updatedAccount.getRole());
      hasChanges = true;
    }

    if ((existingAccount.getChildId() == null && updatedAccount.getChildId() != null)
        || (existingAccount.getChildId() != null
            && !existingAccount.getChildId().equals(updatedAccount.getChildId()))) {
      existingAccount.setChildId(updatedAccount.getChildId());
      hasChanges = true;
    }

    // Only save the entity if changes were made
    if (hasChanges) {
      accountRepository.save(existingAccount);
    }

    return Optional.of(existingAccount);
  }
}
