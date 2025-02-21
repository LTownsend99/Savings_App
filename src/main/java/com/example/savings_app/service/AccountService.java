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

/** Service class responsible for managing the account-related business logic. */
@Service
@Transactional
public class AccountService {

  private final AccountRepository accountRepository;
  @PersistenceContext private EntityManager entityManager;

  private final ParentChildAccountService parentChildAccountService;

  /**
   * Constructor to inject dependencies into the AccountService.
   *
   * @param accountRepository Repository used to interact with the account data in the database.
   * @param parentChildAccountService Service used for handling child-parent account relations.
   */
  @Autowired
  public AccountService(
      AccountRepository accountRepository, ParentChildAccountService parentChildAccountService) {
    this.accountRepository = accountRepository;
    this.parentChildAccountService = parentChildAccountService;
  }

  /**
   * Creates a new account after validating the input fields and ensuring no existing account with
   * the same email. If the account is a parent account and has a childId, the child account is also
   * created.
   *
   * @param account The account to be created.
   * @return The created account.
   * @throws IllegalArgumentException if any required field is missing or invalid.
   * @throws IllegalStateException if an account with the same email already exists.
   */
  public Account createAccount(Account account) {
    int custId;

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

    if (account.getPasswordHash().length() < 6) {
      throw new IllegalArgumentException("Password must be more than 6 characters");
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
      Account savedAccount = accountRepository.save(account);

      // Only run child account creation if save is successful and childId is provided
      if (savedAccount.getUserId() != null && account.getChildId() != null) {
        custId = parentChildAccountService.createAccountWithCustomer(savedAccount);

        if (custId != 0) {
          System.out.println("CustomerId: " + custId);
        } else {
          System.out.println("Failed to create account");
        }
      }

      return savedAccount;

    } catch (org.springframework.dao.DataIntegrityViolationException e) {
      throw new IllegalStateException("Failed to create account due to data integrity issues", e);
    }
  }

  /**
   * Retrieves an account by its user ID.
   *
   * @param userId The user ID of the account to be retrieved.
   * @return An Optional containing the account if found, otherwise an empty Optional.
   */
  public Optional<Account> getAccountByUserId(int userId) {
    return accountRepository.findById(userId);
  }

  /**
   * Deletes an account by its user ID.
   *
   * @param userId The user ID of the account to be deleted.
   */
  public void deleteAccount(int userId) {
    accountRepository.deleteById(userId);
  }

  /**
   * Retrieves an account by its email address.
   *
   * @param email The email address of the account to be retrieved.
   * @return An Optional containing the account if found, otherwise an empty Optional.
   * @throws RuntimeException if the account retrieval fails.
   */
  public Optional<Account> getAccountByEmail(String email) {
    try {
      return accountRepository.findByEmail(email);
    } catch (RuntimeException e) {
      throw new RuntimeException("Failed to retrieve account with email", e);
    }
  }

  /**
   * Updates the details of an existing account. Only the fields that have changed will be updated
   * in the database.
   *
   * @param userId The user ID of the account to be updated.
   * @param updatedAccount The new account data to update.
   * @return An Optional containing the updated account if it exists, otherwise an empty Optional.
   */
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
