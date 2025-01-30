package com.example.savings_app.service;

import com.example.savings_app.exception.MilestoneException;
import com.example.savings_app.model.Account;
import com.example.savings_app.model.Milestone;
import com.example.savings_app.repository.MilestoneRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MilestoneService {

  private final MilestoneRepository milestoneRepository;
  private final AccountService accountService;

  @Autowired
  public MilestoneService(MilestoneRepository milestoneRepository, AccountService accountService) {
    this.milestoneRepository = milestoneRepository;
    this.accountService = accountService;
  }

  public Optional<Milestone> getMilestoneByMilestoneId(int milestoneId) {

    try {
      return milestoneRepository.findById(milestoneId);
    } catch (IllegalArgumentException e) {
      // Handle the case where the provided ID is invalid
      throw new IllegalArgumentException("Invalid Milestone milestoneId: " + milestoneId, e);
    } catch (Exception e) {
      // Catch any unexpected exceptions
      throw new RuntimeException(
          "Failed to retrieve Milestone with milestoneId: " + milestoneId, e);
    }
  }

  public Optional<Milestone> getMilestoneByName(String name) {
    try {
      return milestoneRepository.findByMilestoneName(name);
    } catch (IllegalArgumentException e) {
      // Handle the case where the provided name is invalid
      throw new IllegalArgumentException("Invalid milestone name: " + name, e);
    } catch (Exception e) {
      // Catch any unexpected exceptions
      throw new RuntimeException("Failed to retrieve Milestone with name: " + name, e);
    }
  }

  public List<Milestone> getMilestoneByStartDate(LocalDate startDate) {
    try {
      return milestoneRepository.findByStartDate(startDate);
    } catch (IllegalArgumentException e) {
      // Handle the case where the provided start date is invalid
      throw new IllegalArgumentException("Invalid start date: " + startDate, e);
    } catch (Exception e) {
      // Catch any unexpected exceptions
      throw new RuntimeException("Failed to retrieve Milestones with start date: " + startDate, e);
    }
  }

  public List<Milestone> getMilestoneByCompletionDate(LocalDate completionDate) {
    try {
      return milestoneRepository.findByCompletionDate(completionDate);
    } catch (IllegalArgumentException e) {
      // Handle the case where the provided completion date is invalid
      throw new IllegalArgumentException("Invalid completion date: " + completionDate, e);
    } catch (Exception e) {
      // Catch any unexpected exceptions
      throw new RuntimeException(
          "Failed to retrieve Milestones with completion date: " + completionDate, e);
    }
  }

  public List<Milestone> getMilestoneByStatus(Enum status) {
    try {
      return milestoneRepository.findByStatus(status);
    } catch (IllegalArgumentException e) {
      // Handle the case where the provided status is invalid
      throw new IllegalArgumentException("Invalid status: " + status, e);
    } catch (Exception e) {
      // Catch any unexpected exceptions
      throw new RuntimeException("Failed to retrieve Milestones with status: " + status, e);
    }
  }

  public void deleteMilestone(int milestoneId) {
    try {
      milestoneRepository.deleteById(milestoneId);
    } catch (IllegalArgumentException e) {
      // Handle the case where the provided ID is invalid
      throw new IllegalArgumentException("Invalid Milestone Id: " + milestoneId, e);
    }
  }

  @Transactional
  public Milestone createMilestone(Milestone milestone) {
    // Validate user account
    Account user = validateUser(milestone.getUser());

    // Validate milestone name
    validateMilestoneName(milestone.getMilestoneName());

    // Validate target amount
    validateTargetAmount(milestone.getTargetAmount());

    // Validate start date
    validateStartDate(milestone.getStartDate());

    // Set default status and saved amount
    milestone.setStatus(Milestone.Status.active);
    if (milestone.getSavedAmount() == null) {
      milestone.setSavedAmount(BigDecimal.ZERO);
    }

    Milestone savedMilestone = milestoneRepository.save(milestone);

    // Save the milestone to the database
    return savedMilestone;
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

  private void validateMilestoneName(String milestoneName) {
    if (milestoneName == null || milestoneName.trim().isEmpty()) {
      throw new IllegalArgumentException("Milestone name cannot be empty.");
    }
  }

  private void validateTargetAmount(BigDecimal targetAmount) {
    if (targetAmount == null || targetAmount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Target amount must be greater than zero.");
    }
  }

  private void validateStartDate(LocalDate startDate) {
    if (startDate == null) {
      throw new IllegalArgumentException("Start date cannot be null.");
    }
    if (startDate.isAfter(LocalDate.now())) {
      throw new IllegalArgumentException("Start date cannot be in the future.");
    }
  }

  public Milestone markMilestoneAsCompleted(Integer milestoneId) {
    // Find the milestone by ID
    Milestone milestone =
        milestoneRepository
            .findById(milestoneId)
            .orElseThrow(
                () -> new IllegalArgumentException("Milestone not found for ID: " + milestoneId));

    // Check if the milestone is already completed
    if (milestone.getStatus() == Milestone.Status.completed) {
      throw new IllegalStateException("Milestone is already marked as completed.");
    }

    // Mark as completed and set the completion date
    milestone.setStatus(Milestone.Status.completed);
    milestone.setCompletionDate(LocalDate.now());

    // Save and return the updated milestone
    return milestoneRepository.save(milestone);
  }

  public Milestone updateSavedAmountAndCheckCompletion(
      Integer milestoneId, BigDecimal addedAmount) {
    // Validate the added amount
    if (addedAmount == null || addedAmount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new MilestoneException.InvalidAmountException(
          "The added amount must be greater than zero.");
    }

    // Retrieve the milestone by its ID
    Milestone milestone =
        milestoneRepository
            .findById(milestoneId)
            .orElseThrow(
                () ->
                    new MilestoneException.MilestoneNotFoundException(
                        "Milestone not found for id: " + milestoneId));

    // Check if the added amount will exceed the target amount
    BigDecimal newSavedAmount = milestone.getSavedAmount().add(addedAmount);
    if (newSavedAmount.compareTo(milestone.getTargetAmount()) > 0) {
      throw new MilestoneException.InvalidAmountException(
          "The added amount exceeds the target amount.");
    }

    // Update the saved amount
    milestone.setSavedAmount(newSavedAmount);

    // Check if the saved amount has reached or exceeded the target amount
    if (milestone.getSavedAmount().compareTo(milestone.getTargetAmount()) >= 0) {
      // Update the completion date and status
      milestone.setCompletionDate(LocalDate.now()); // Set current date as completion date
      milestone.setStatus(Milestone.Status.completed); // Set status to completed
    }

    // Save the updated milestone to the database
    return milestoneRepository.save(milestone);
  }

  public List<Milestone> getAllMilestonesForUser(Account user) {

    try {
      return milestoneRepository.findAllByUser(user);
    } catch (IllegalArgumentException e) {
      // Handle the case where the provided ID is invalid
      throw new IllegalArgumentException("Invalid Account Provided", e);
    } catch (Exception e) {
      // Catch any unexpected exceptions
      throw new RuntimeException("Failed to retrieve Milestone with Account provided: ", e);
    }
  }
}
