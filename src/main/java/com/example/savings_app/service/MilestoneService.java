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

/**
 * Service class responsible for handling the business logic related to milestones. It includes
 * operations such as creating, updating, retrieving, and deleting milestones.
 */
@Service
public class MilestoneService {

  private final MilestoneRepository milestoneRepository;
  private final AccountService accountService;

  /**
   * Constructor to inject the MilestoneRepository and AccountService dependencies.
   *
   * @param milestoneRepository Repository used to interact with milestone data.
   * @param accountService Service to interact with account data.
   */
  @Autowired
  public MilestoneService(MilestoneRepository milestoneRepository, AccountService accountService) {
    this.milestoneRepository = milestoneRepository;
    this.accountService = accountService;
  }

  /**
   * Retrieves a milestone by its unique milestoneId.
   *
   * @param milestoneId The ID of the milestone to retrieve.
   * @return An Optional containing the milestone if found, otherwise an empty Optional.
   * @throws IllegalArgumentException if the provided milestoneId is invalid.
   * @throws RuntimeException if there is an error while retrieving the milestone.
   */
  public Optional<Milestone> getMilestoneByMilestoneId(int milestoneId) {
    try {
      return milestoneRepository.findById(milestoneId);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid Milestone milestoneId: " + milestoneId, e);
    } catch (Exception e) {
      throw new RuntimeException(
          "Failed to retrieve Milestone with milestoneId: " + milestoneId, e);
    }
  }

  /**
   * Retrieves a milestone by its name.
   *
   * @param name The name of the milestone to retrieve.
   * @return An Optional containing the milestone if found, otherwise an empty Optional.
   * @throws IllegalArgumentException if the provided name is invalid.
   * @throws RuntimeException if there is an error while retrieving the milestone.
   */
  public Optional<Milestone> getMilestoneByName(String name) {
    try {
      return milestoneRepository.findByMilestoneName(name);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid milestone name: " + name, e);
    } catch (Exception e) {
      throw new RuntimeException("Failed to retrieve Milestone with name: " + name, e);
    }
  }

  /**
   * Retrieves milestones by their start date.
   *
   * @param startDate The start date to filter milestones by.
   * @return A list of milestones that start on the provided date.
   * @throws IllegalArgumentException if the provided startDate is invalid.
   * @throws RuntimeException if there is an error while retrieving the milestones.
   */
  public List<Milestone> getMilestoneByStartDate(LocalDate startDate) {
    try {
      return milestoneRepository.findByStartDate(startDate);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid start date: " + startDate, e);
    } catch (Exception e) {
      throw new RuntimeException("Failed to retrieve Milestones with start date: " + startDate, e);
    }
  }

  /**
   * Retrieves milestones by their completion date.
   *
   * @param completionDate The completion date to filter milestones by.
   * @return A list of milestones that completed on the provided date.
   * @throws IllegalArgumentException if the provided completionDate is invalid.
   * @throws RuntimeException if there is an error while retrieving the milestones.
   */
  public List<Milestone> getMilestoneByCompletionDate(LocalDate completionDate) {
    try {
      return milestoneRepository.findByCompletionDate(completionDate);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid completion date: " + completionDate, e);
    } catch (Exception e) {
      throw new RuntimeException(
          "Failed to retrieve Milestones with completion date: " + completionDate, e);
    }
  }

  /**
   * Retrieves milestones by their status.
   *
   * @param status The status to filter milestones by (e.g., active, completed).
   * @return A list of milestones that match the provided status.
   * @throws IllegalArgumentException if the provided status is invalid.
   * @throws RuntimeException if there is an error while retrieving the milestones.
   */
  public List<Milestone> getMilestoneByStatus(Enum status) {
    try {
      return milestoneRepository.findByStatus(status);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid status: " + status, e);
    } catch (Exception e) {
      throw new RuntimeException("Failed to retrieve Milestones with status: " + status, e);
    }
  }

  /**
   * Deletes a milestone by its milestoneId.
   *
   * @param milestoneId The ID of the milestone to be deleted.
   * @throws IllegalArgumentException if the provided milestoneId is invalid.
   */
  public void deleteMilestone(int milestoneId) {
    try {
      milestoneRepository.deleteById(milestoneId);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid Milestone Id: " + milestoneId, e);
    }
  }

  /**
   * Creates a new milestone after validating the associated user and milestone data.
   *
   * @param milestone The milestone to be created.
   * @return The created milestone.
   * @throws IllegalArgumentException if the milestone data is invalid.
   */
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

    // Save the milestone to the database
    return milestoneRepository.save(milestone);
  }

  /**
   * Validates the provided user account by checking if it exists in the database.
   *
   * @param user The user to be validated.
   * @return The validated user account.
   * @throws IllegalArgumentException if the user is invalid or not found.
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
   * Validates the milestone name to ensure it is not empty.
   *
   * @param milestoneName The milestone name to validate.
   * @throws IllegalArgumentException if the name is empty or null.
   */
  private void validateMilestoneName(String milestoneName) {
    if (milestoneName == null || milestoneName.trim().isEmpty()) {
      throw new IllegalArgumentException("Milestone name cannot be empty.");
    }
  }

  /**
   * Validates the target amount to ensure it is greater than zero.
   *
   * @param targetAmount The target amount to validate.
   * @throws IllegalArgumentException if the target amount is less than or equal to zero.
   */
  private void validateTargetAmount(BigDecimal targetAmount) {
    if (targetAmount == null || targetAmount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Target amount must be greater than zero.");
    }
  }

  /**
   * Validates the start date to ensure it is not null or in the future.
   *
   * @param startDate The start date to validate.
   * @throws IllegalArgumentException if the start date is invalid.
   */
  private void validateStartDate(LocalDate startDate) {
    if (startDate == null) {
      throw new IllegalArgumentException("Start date cannot be null.");
    }
    if (startDate.isAfter(LocalDate.now())) {
      throw new IllegalArgumentException("Start date cannot be in the future.");
    }
  }

  /**
   * Marks a milestone as completed by updating its status and setting the completion date.
   *
   * @param milestoneId The ID of the milestone to mark as completed.
   * @return The updated milestone.
   * @throws IllegalArgumentException if the milestone is not found.
   * @throws IllegalStateException if the milestone is already completed.
   */
  public Milestone markMilestoneAsCompleted(Integer milestoneId) {
    Milestone milestone =
        milestoneRepository
            .findById(milestoneId)
            .orElseThrow(
                () -> new IllegalArgumentException("Milestone not found for ID: " + milestoneId));

    if (milestone.getStatus() == Milestone.Status.completed) {
      throw new IllegalStateException("Milestone is already marked as completed.");
    }

    milestone.setStatus(Milestone.Status.completed);
    milestone.setCompletionDate(LocalDate.now());

    return milestoneRepository.save(milestone);
  }

  /**
   * Updates the saved amount of a milestone and checks whether it has reached the target amount.
   *
   * @param milestoneId The ID of the milestone to update.
   * @param addedAmount The amount to add to the saved amount.
   * @return The updated milestone.
   * @throws MilestoneException.InvalidAmountException if the added amount is invalid.
   */
  @Transactional
  public Milestone updateSavedAmountAndCheckCompletion(
      Integer milestoneId, BigDecimal addedAmount) {
    if (addedAmount == null || addedAmount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new MilestoneException.InvalidAmountException(
          "The added amount must be greater than zero.");
    }

    Milestone milestone =
        milestoneRepository
            .findById(milestoneId)
            .orElseThrow(
                () ->
                    new MilestoneException.MilestoneNotFoundException(
                        "Milestone not found for id: " + milestoneId));

    BigDecimal newSavedAmount = milestone.getSavedAmount().add(addedAmount);
    if (newSavedAmount.compareTo(milestone.getTargetAmount()) > 0) {
      throw new MilestoneException.InvalidAmountException(
          "The added amount exceeds the target amount.");
    }

    milestone.setSavedAmount(newSavedAmount);

    if (milestone.getSavedAmount().compareTo(milestone.getTargetAmount()) >= 0) {
      milestone.setCompletionDate(LocalDate.now());
      milestone.setStatus(Milestone.Status.completed);
    }

    return milestoneRepository.save(milestone);
  }

  /**
   * Retrieves all milestones for a specific user account.
   *
   * @param user The user account whose milestones are to be retrieved.
   * @return A list of milestones associated with the given user.
   * @throws IllegalArgumentException if the provided account is invalid.
   * @throws RuntimeException if there is an error while retrieving the milestones.
   */
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
