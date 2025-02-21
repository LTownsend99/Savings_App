package com.example.savings_app.controller;

import com.example.savings_app.exception.MilestoneException;
import com.example.savings_app.model.Account;
import com.example.savings_app.model.Milestone;
import com.example.savings_app.service.AccountService;
import com.example.savings_app.service.MilestoneService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * MilestoneController is a REST controller that handles HTTP requests related to milestone
 * operations. It includes methods for creating, updating, fetching, and deleting milestones.
 */
@RestController
public class MilestoneController {

  private final MilestoneService milestoneService;
  private final AccountService accountService;

  /**
   * Constructor to initialize MilestoneService and AccountService.
   *
   * @param milestoneService The service that handles milestone-related operations.
   * @param accountService The service that handles account-related operations.
   */
  @Autowired
  public MilestoneController(MilestoneService milestoneService, AccountService accountService) {
    this.milestoneService = milestoneService;
    this.accountService = accountService;
  }

  /**
   * Retrieves a milestone by its unique milestone ID.
   *
   * @param milestoneId The unique ID of the milestone to retrieve.
   * @return A ResponseEntity containing the milestone if found, or 404 if not found.
   */
  @GetMapping("/milestone/{milestoneId}")
  public ResponseEntity<Milestone> getMilestoneByMilestoneId(@PathVariable int milestoneId) {
    try {
      // Attempt to find the milestone by its ID
      Optional<Milestone> milestone = milestoneService.getMilestoneByMilestoneId(milestoneId);
      return milestone.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(null);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  /**
   * Retrieves a milestone by its name.
   *
   * @param name The name of the milestone to retrieve.
   * @return A ResponseEntity containing the milestone if found, or 404 if not found.
   */
  @GetMapping("/milestone/name/{name}")
  public ResponseEntity<Milestone> getMilestoneByName(@PathVariable String name) {
    try {
      Optional<Milestone> milestone = milestoneService.getMilestoneByName(name);
      return milestone.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(null);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  /**
   * Retrieves milestones based on the provided start date.
   *
   * @param startDate The start date to filter the milestones.
   * @return A ResponseEntity containing a list of milestones for the provided start date.
   */
  @GetMapping("/milestone/startDate/{startDate}")
  public ResponseEntity<List<Milestone>> getMilestoneByStartDate(@PathVariable String startDate) {
    LocalDate parsedDate = LocalDate.parse(startDate);
    try {
      List<Milestone> milestones = milestoneService.getMilestoneByStartDate(parsedDate);
      return milestones.isEmpty()
          ? ResponseEntity.notFound().build()
          : ResponseEntity.ok(milestones);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(null);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  /**
   * Retrieves milestones based on the provided completion date.
   *
   * @param completionDate The completion date to filter the milestones.
   * @return A ResponseEntity containing a list of milestones for the provided completion date.
   */
  @GetMapping("/milestone/completionDate/{completionDate}")
  public ResponseEntity<List<Milestone>> getMilestoneByCompletionDate(
      @PathVariable String completionDate) {
    LocalDate parsedDate = LocalDate.parse(completionDate);
    try {
      List<Milestone> milestones = milestoneService.getMilestoneByCompletionDate(parsedDate);
      return milestones.isEmpty()
          ? ResponseEntity.notFound().build()
          : ResponseEntity.ok(milestones);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(null);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  /**
   * Retrieves milestones based on the provided status.
   *
   * @param status The status of the milestones to retrieve (e.g., "COMPLETED", "IN_PROGRESS").
   * @return A ResponseEntity containing a list of milestones with the given status.
   */
  @GetMapping("/milestone/status/{status}")
  public ResponseEntity<List<Milestone>> getMilestoneStatus(@PathVariable String status) {
    try {
      Milestone.Status milestoneStatus = Milestone.Status.valueOf(status);
      List<Milestone> milestones = milestoneService.getMilestoneByStatus(milestoneStatus);
      return milestones.isEmpty()
          ? ResponseEntity.notFound().build()
          : ResponseEntity.ok(milestones);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(null);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  /**
   * Deletes a milestone using its unique milestone ID.
   *
   * @param milestoneId The unique ID of the milestone to delete.
   * @return A ResponseEntity with a message indicating the result of the deletion.
   */
  @DeleteMapping("/milestone/{milestoneId}")
  public ResponseEntity<String> deleteMilestone(@PathVariable int milestoneId) {
    try {
      milestoneService.deleteMilestone(milestoneId);
      return ResponseEntity.ok("Milestone with ID " + milestoneId + " deleted successfully.");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An unexpected error occurred: " + e.getMessage());
    }
  }

  /**
   * Creates a new milestone.
   *
   * @param milestone The milestone object to be created.
   * @return A ResponseEntity with the result of the milestone creation.
   */
  @PostMapping("/milestone/create")
  public ResponseEntity<String> createMilestone(@RequestBody Milestone milestone) {
    try {
      milestoneService.createMilestone(milestone);
      return ResponseEntity.status(HttpStatus.CREATED).body("Milestone created successfully.");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An unexpected error occurred: " + e.getMessage());
    }
  }

  /**
   * Marks a milestone as completed.
   *
   * @param milestoneId The unique ID of the milestone to mark as completed.
   * @return A ResponseEntity with the updated milestone or an error message.
   */
  @PatchMapping("/milestone/{milestoneId}/complete")
  public ResponseEntity<Milestone> markMilestoneAsCompleted(@PathVariable Integer milestoneId) {
    try {
      Milestone updatedMilestone = milestoneService.markMilestoneAsCompleted(milestoneId);
      return ResponseEntity.ok(updatedMilestone);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    } catch (IllegalStateException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
    }
  }

  /**
   * Updates the saved amount for a milestone.
   *
   * @param milestoneId The unique ID of the milestone to update.
   * @param body A map containing the updated saved amount.
   * @return A ResponseEntity with the updated milestone or an error message.
   */
  @PatchMapping("/milestone/{milestoneId}/updateSavedAmount")
  public ResponseEntity<Milestone> updateSavedAmount(
      @PathVariable Integer milestoneId, @RequestBody Map<String, Object> body) {
    try {
      Object addedAmountObject = body.get("addedAmount");
      if (addedAmountObject == null) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
      }

      BigDecimal addedAmountBigDecimal;
      if (addedAmountObject instanceof String) {
        addedAmountBigDecimal = new BigDecimal((String) addedAmountObject);
      } else if (addedAmountObject instanceof Double) {
        addedAmountBigDecimal = BigDecimal.valueOf((Double) addedAmountObject);
      } else {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
      }

      Milestone updatedMilestone =
          milestoneService.updateSavedAmountAndCheckCompletion(milestoneId, addedAmountBigDecimal);
      return ResponseEntity.ok(updatedMilestone);
    } catch (MilestoneException.MilestoneNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    } catch (MilestoneException.InvalidAmountException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  /**
   * Retrieves all milestones for a specific user based on the user ID.
   *
   * @param userId The unique ID of the user to fetch milestones for.
   * @return A ResponseEntity containing a list of milestones for the user.
   */
  @GetMapping("/milestone/user/{userId}")
  public ResponseEntity<List<Milestone>> getAllMilestonesForUser(@PathVariable String userId) {
    try {
      Account user =
          accountService
              .getAccountByUserId(Integer.parseInt(userId))
              .orElseThrow(() -> new IllegalArgumentException("Invalid Account Provided"));

      List<Milestone> milestones = milestoneService.getAllMilestonesForUser(user);

      if (milestones.isEmpty()) {
        return ResponseEntity.noContent().build();
      }

      return ResponseEntity.ok(milestones);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }
}
