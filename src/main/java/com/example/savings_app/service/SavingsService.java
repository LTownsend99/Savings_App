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

@Service
public class SavingsService {

  private final SavingsRepository savingsRepository;
  private final AccountService accountService;


  @Autowired
  public SavingsService(SavingsRepository savingsRepository, AccountService accountService) {
    this.savingsRepository = savingsRepository;
      this.accountService = accountService;
  }

  public Optional<Savings> getSavingsById(int savingsId) {
    try {
      return savingsRepository.findById(savingsId);
    } catch (IllegalArgumentException e) {
      // Handle the case where the provided ID is invalid
      throw new IllegalArgumentException("Invalid Savings ID: " + savingsId, e);
    } catch (Exception e) {
      // Catch any unexpected exceptions
      throw new RuntimeException("Failed to retrieve Savings with ID: " + savingsId, e);
    }
  }

  public List<Savings> getSavingsByDate(LocalDate date) {
    try {
      return savingsRepository.findByDate(date);
    } catch (IllegalArgumentException e) {
      // Handle the case where the provided date is invalid
      throw new IllegalArgumentException("Invalid Savings date: " + date, e);
    } catch (Exception e) {
      // Catch any unexpected exceptions
      throw new RuntimeException("Failed to retrieve Savings with date: " + date, e);
    }
  }

  public Optional<Savings> getSavingsByMilestoneId(int milestoneId) {
    try {
      return savingsRepository.findByMilestoneId(milestoneId);
    } catch (IllegalArgumentException e) {
      // Handle the case where the provided Milestone ID is invalid
      throw new IllegalArgumentException("Invalid Milestone ID: " + milestoneId, e);
    } catch (Exception e) {
      // Catch any unexpected exceptions
      throw new RuntimeException("Failed to retrieve Savings for Milestone ID: " + milestoneId, e);
    }
  }

  public void deleteSavings(int savingsId) {
    try {
      savingsRepository.deleteById(savingsId);
    } catch (IllegalArgumentException e) {
      // Handle the case where the provided ID is invalid
      throw new IllegalArgumentException("Invalid Savings Id: " + savingsId, e);
    }
  }

  public List<Savings> getAllSavingsForUser(Account user) {

    try {
      return savingsRepository.findAllByUser(user);
    } catch (IllegalArgumentException e) {
      // Handle the case where the provided ID is invalid
      throw new IllegalArgumentException("Invalid Account Provided", e);
    } catch (Exception e) {
      // Catch any unexpected exceptions
      throw new RuntimeException("Failed to retrieve Savings with Account provided", e);
    }
  }

  @Transactional
  public Savings createSavings(Savings savings) {
    // Validate user account
    Account user = validateUser(savings.getUser());

    // Validate amount
    validateAmount(savings.getAmount());

    // Validate milestone Id
    validateMilestoneId(String.valueOf(savings.getMilestoneId()));

    // Validate start date
    validateDate(savings.getDate());


    Savings savedSavings = savingsRepository.save(savings);

    // Save the milestone to the database
    return savedSavings;
  }

  private Account validateUser(Account user) {
    if (user == null || user.getUserId() == null) {
      throw new IllegalArgumentException("Invalid user: User account is required.");
    }
    return accountService
            .getAccountByUserId(user.getUserId())
            .orElseThrow(
                    () -> new IllegalArgumentException("User not found for ID: " + user.getUserId()));
  }

  private void validateMilestoneId(String milestoneId) {
    if (milestoneId == null || milestoneId.trim().isEmpty()) {
      throw new IllegalArgumentException("Milestone Id cannot be empty.");
    }
  }

  private void validateAmount(BigDecimal amount) {
    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Amount must be greater than zero.");
    }
  }

  private void validateDate(LocalDate startDate) {
    if (startDate == null) {
      throw new IllegalArgumentException("Date cannot be null.");
    }
    if (startDate.isAfter(LocalDate.now())) {
      throw new IllegalArgumentException("Date cannot be in the future.");
    }
  }

}
