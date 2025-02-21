package com.example.savings_app.controller;

import com.example.savings_app.model.Account;
import com.example.savings_app.model.Savings;
import com.example.savings_app.service.AccountService;
import com.example.savings_app.service.SavingsService;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * This is the controller class responsible for handling API requests related to Savings. It
 * provides CRUD operations to interact with Savings data.
 */
@RestController
public class SavingsController {

  private final SavingsService savingsService;
  private final AccountService
      accountService; // Injecting AccountService for account-related operations

  /**
   * Constructor for injecting dependencies (SavingsService and AccountService).
   *
   * @param savingsService The service responsible for operations related to Savings.
   * @param accountService The service responsible for operations related to Account.
   */
  @Autowired
  public SavingsController(SavingsService savingsService, AccountService accountService) {
    this.savingsService = savingsService;
    this.accountService = accountService;
  }

  /**
   * Get a specific Savings object by its ID.
   *
   * @param savingsId The ID of the savings to retrieve.
   * @return ResponseEntity containing the Savings object or a 404 if not found.
   */
  @GetMapping("/savings/{savingsId}")
  public ResponseEntity<Savings> getSavingsById(@PathVariable int savingsId) {
    try {
      Optional<Savings> savings = savingsService.getSavingsById(savingsId);

      // If savings is found, return it; otherwise, return 404 not found
      return savings.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(null); // Return 400 for invalid arguments
    } catch (Exception e) {
      return ResponseEntity.status(500).body(null); // Return 500 for any unexpected error
    }
  }

  /**
   * Get a list of Savings objects for a given date.
   *
   * @param date The date in string format to filter savings by.
   * @return ResponseEntity containing the list of Savings for the given date or a 404 if not found.
   * @throws ParseException If the date format is invalid.
   */
  @GetMapping("/savings/date/{date}")
  public ResponseEntity<List<Savings>> getSavingsByDate(@PathVariable String date)
      throws ParseException {

    // Parse the date string into LocalDate
    LocalDate parsedDate = LocalDate.parse(date);

    try {
      List<Savings> savings = savingsService.getSavingsByDate(parsedDate);

      // If no savings are found for the given date, return 404
      if (savings.isEmpty()) {
        return ResponseEntity.notFound().build();
      }
      return ResponseEntity.ok(savings); // Return the list of savings for the date
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(null); // Return 400 for invalid date
    } catch (Exception e) {
      return ResponseEntity.status(500).body(null); // Return 500 for unexpected errors
    }
  }

  /**
   * Get a specific Savings object by its milestone ID.
   *
   * @param milestoneId The milestone ID to filter savings by.
   * @return ResponseEntity containing the Savings object or a 404 if not found.
   */
  @GetMapping("/savings/milestone/{milestoneId}")
  public ResponseEntity<Savings> getSavingsByMilestoneId(@PathVariable int milestoneId) {
    try {
      Optional<Savings> savings = savingsService.getSavingsByMilestoneId(milestoneId);

      // If savings are found for the milestone, return it; otherwise, return 404
      return savings.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(null); // Return 400 for invalid milestone ID
    } catch (Exception e) {
      return ResponseEntity.status(500).body(null); // Return 500 for unexpected errors
    }
  }

  /**
   * Delete a Savings record by its ID.
   *
   * @param savingsId The ID of the savings to delete.
   * @return ResponseEntity indicating the success or failure of the operation.
   */
  @DeleteMapping("/savings/{savingsId}")
  public ResponseEntity<Void> deleteSavings(@PathVariable int savingsId) {
    try {
      savingsService.deleteSavings(savingsId);
      return ResponseEntity.noContent().build(); // Return 204 on successful deletion
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().build(); // Return 400 for invalid savings ID
    }
  }

  /**
   * Get all Savings records associated with a user.
   *
   * @param userId The user ID to retrieve associated Savings.
   * @return ResponseEntity containing the list of Savings for the user or a 204 if no records
   *     exist.
   */
  @GetMapping("/savings/user/{userId}")
  public ResponseEntity<List<Savings>> getAllMilestonesForUser(@PathVariable String userId) {
    try {
      // Retrieve user by ID, throw exception if not found
      Account user =
          accountService
              .getAccountByUserId(Integer.parseInt(userId))
              .orElseThrow(() -> new IllegalArgumentException("Invalid Account Provided"));

      List<Savings> savings = savingsService.getAllSavingsForUser(user);

      // If no savings exist for the user, return 204
      if (savings.isEmpty()) {
        return ResponseEntity.noContent().build();
      }

      return ResponseEntity.ok(savings); // Return the list of savings for the user
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(null); // Return 400 for invalid user ID
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(null); // Return 500 for unexpected errors
    }
  }

  /**
   * Create a new Savings record.
   *
   * @param savings The Savings object to be created.
   * @return ResponseEntity with a success message or an error message.
   */
  @PostMapping("/savings/create")
  public ResponseEntity<String> createSavings(@RequestBody Savings savings) {
    try {
      savingsService.createSavings(savings);
      return ResponseEntity.status(HttpStatus.CREATED)
          .body("Savings created successfully."); // Return 201 on success
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest()
          .body(e.getMessage()); // Return 400 with error message for invalid data
    } catch (Exception e) {
      // Return 500 for any unexpected errors
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An unexpected error occurred: " + e.getMessage());
    }
  }
}
