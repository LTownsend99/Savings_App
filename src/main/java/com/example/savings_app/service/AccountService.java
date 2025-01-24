package com.example.savings_app.service;

import com.example.savings_app.model.Account;
import com.example.savings_app.repository.AccountRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccountService {

  private final AccountRepository accountRepository;

  @PersistenceContext private EntityManager entityManager;

  ParentChildAccountService parentChildAccountService;

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

    account.setCreatedAt(LocalDate.now());
    account.setUserId(null); // Ensure it is null before saving

    try {
      // Save the account to the repository
      return accountRepository.save(account);
    } catch (org.springframework.dao.DataIntegrityViolationException e) {
      throw new IllegalStateException("Failed to create account due to data integrity issues", e);
    }
  }

  // Method to get an account by userId
  public Optional<Account> getAccountByUserId(int userId) {
    return accountRepository.findById(userId);
  }

  // Method to delete an account
  public void deleteAccount(int userId) {
    accountRepository.deleteById(userId);
  }

  // Method to get an account by email
  public Optional<Account> getAccountByEmail(String email) {
    try {
      return accountRepository.findByEmail(email);
    } catch (RuntimeException e) {
      throw new RuntimeException("Failed to retrieve account with email", e);
    }
  }

  // Method to update an account
  public Optional<Account> updateAccount(int userId, Account updatedAccount) {
    Optional<Account> existingAccountOpt = accountRepository.findById(userId);
    if (existingAccountOpt.isEmpty()) {
      return Optional.empty();
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
