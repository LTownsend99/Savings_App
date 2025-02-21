package com.example.savings_app.service;

import com.example.savings_app.model.Account;
import com.example.savings_app.model.Savings;
import com.example.savings_app.repository.SavingsRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing savings operations. This includes creating, retrieving, and deleting
 * savings records, as well as validating user inputs and relationships.
 */
@Service
public class SavingsService {

  private final SavingsRepository savingsRepository;
  private final AccountService accountService;

  /**
   * Constructor for injecting the necessary dependencies into the SavingsService class.
   *
   * @param savingsRepository the repository used for saving and retrieving savings data
   * @param accountService the service used for managing user accounts
   */
  @Autowired
  public SavingsService(SavingsRepository savingsRepository, AccountService accountService) {
    this.savingsRepository = savingsRepository;
    this.accountService = accountService;
  }

  /**
   * Retrieves a savings record by its unique ID.
   *
   * @param savingsId the ID of the savings record
   * @return an Optional containing the savings record if found, or empty if not found
   * @throws IllegalArgumentException if the provided ID is invalid
   * @throws RuntimeException if any other exception occurs during retrieval
   */
  public Optional<Savings> getSavingsById(int savingsId) {
    try {
      return savingsRepository.findById(savingsId);
    } catch (IllegalArgumentException e) {
      // Handle invalid savings ID
      throw new IllegalArgumentException("Invalid Savings ID: " + savingsId, e);
    } catch (Exception e) {
      // Catch other exceptions
      throw new RuntimeException("Failed to retrieve Savings with ID: " + savingsId, e);
    }
  }

  /**
   * Retrieves all savings records associated with a specific date.
   *
   * @param date the date for filtering the savings records
   * @return a list of savings records for the given date
   * @throws IllegalArgumentException if the provided date is invalid
   * @throws RuntimeException if any other exception occurs during retrieval
   */
  public List<Savings> getSavingsByDate(LocalDate date) {
    try {
      return savingsRepository.findByDate(date);
    } catch (IllegalArgumentException e) {
      // Handle invalid date
      throw new IllegalArgumentException("Invalid Savings date: " + date, e);
    } catch (Exception e) {
      // Catch other exceptions
      throw new RuntimeException("Failed to retrieve Savings with date: " + date, e);
    }
  }

  /**
   * Retrieves a savings record by its associated milestone ID.
   *
   * @param milestoneId the milestone ID associated with the savings record
   * @return an Optional containing the savings record if found, or empty if not found
   * @throws IllegalArgumentException if the provided milestone ID is invalid
   * @throws RuntimeException if any other exception occurs during retrieval
   */
  public Optional<Savings> getSavingsByMilestoneId(int milestoneId) {
    try {
      return savingsRepository.findByMilestoneId(milestoneId);
    } catch (IllegalArgumentException e) {
      // Handle invalid milestone ID
      throw new IllegalArgumentException("Invalid Milestone ID: " + milestoneId, e);
    } catch (Exception e) {
      // Catch other exceptions
      throw new RuntimeException("Failed to retrieve Savings for Milestone ID: " + milestoneId, e);
    }
  }

  /**
   * Deletes a savings record by its ID.
   *
   * @param savingsId the ID of the savings record to be deleted
   * @throws IllegalArgumentException if the provided savings ID is invalid
   */
  public void deleteSavings(int savingsId) {
    try {
      savingsRepository.deleteById(savingsId);
    } catch (IllegalArgumentException e) {
      // Handle invalid savings ID
      throw new IllegalArgumentException("Invalid Savings Id: " + savingsId, e);
    }
  }

  /**
   * Retrieves all savings records for a specific user account.
   *
   * @param user the account of the user for which savings records are retrieved
   * @return a list of savings records associated with the given user account
   * @throws IllegalArgumentException if the provided account is invalid
   * @throws RuntimeException if any other exception occurs during retrieval
   */
  public List<Savings> getAllSavingsForUser(Account user) {
    try {
      return savingsRepository.findAllByUser(user);
    } catch (IllegalArgumentException e) {
      // Handle invalid account
      throw new IllegalArgumentException("Invalid Account Provided", e);
    } catch (Exception e) {
      // Catch other exceptions
      throw new RuntimeException("Failed to retrieve Savings with Account provided", e);
    }
  }

  /**
   * Creates a new savings record after validating all relevant data, including the user, amount,
   * milestone ID, and date. This method is transactional to ensure that all operations succeed or
   * fail together.
   *
   * @param savings the savings record to be created
   * @return the created savings record
   * @throws IllegalArgumentException if any validation fails (user, amount, milestone ID, or date)
   */
  @Transactional
  public Savings createSavings(Savings savings) {
    // Validate user account
    Account user = validateUser(savings.getUser());

    // Validate amount to ensure it's a positive value
    validateAmount(savings.getAmount());

    // Validate milestone ID to ensure it's not null or empty
    validateMilestoneId(String.valueOf(savings.getMilestoneId()));

    // Validate the date to ensure it's not in the future
    validateDate(savings.getDate());

    // Save the savings record in the repository and return the saved entity
    Savings savedSavings = savingsRepository.save(savings);

    return savedSavings;
  }

  /**
   * Validates the user associated with the savings record. Ensures that the user is not null and
   * that the user exists in the account service.
   *
   * @param user the user account to be validated
   * @return the validated user account
   * @throws IllegalArgumentException if the user is invalid or not found
   */
  private Account validateUser(Account user) {
    if (user == null || user.getUserId() == null) {
      throw new IllegalArgumentException("Invalid user: User account is required.");
    }
    return accountService
        .getAccountByUserId(user.getUserId())
        .orElseThrow(
            () -> new IllegalArgumentException("User not found for ID: " + user.getUserId()));
  }

  /**
   * Validates the milestone ID associated with the savings record.
   *
   * @param milestoneId the milestone ID to be validated
   * @throws IllegalArgumentException if the milestone ID is null or empty
   */
  private void validateMilestoneId(String milestoneId) {
    if (milestoneId == null || milestoneId.trim().isEmpty()) {
      throw new IllegalArgumentException("Milestone Id cannot be empty.");
    }
  }

  /**
   * Validates the amount to ensure it's a positive value.
   *
   * @param amount the amount to be validated
   * @throws IllegalArgumentException if the amount is null or less than or equal to zero
   */
  private void validateAmount(BigDecimal amount) {
    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Amount must be greater than zero.");
    }
  }

  /**
   * Validates the start date to ensure it is not null and not in the future.
   *
   * @param startDate the start date to be validated
   * @throws IllegalArgumentException if the date is null or in the future
   */
  private void validateDate(LocalDate startDate) {
    if (startDate == null) {
      throw new IllegalArgumentException("Date cannot be null.");
    }
    if (startDate.isAfter(LocalDate.now())) {
      throw new IllegalArgumentException("Date cannot be in the future.");
    }
  }
}
