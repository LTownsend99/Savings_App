package com.example.savings_app.controller;

import com.example.savings_app.exception.MilestoneException;
import com.example.savings_app.model.Milestone;
import com.example.savings_app.service.MilestoneService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class MilestoneController {

  private final MilestoneService milestoneService;

  @Autowired
  public MilestoneController(MilestoneService milestoneService) {
    this.milestoneService = milestoneService;
  }

  @GetMapping("/milestone/{milestoneId}")
  public ResponseEntity<Milestone> getMilestoneByMilestoneId(@PathVariable int milestoneId) {
    try {
      Optional<Milestone> milestone = milestoneService.getMilestoneByMilestoneId(milestoneId);
      return milestone.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(null);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

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

  // Get Milestones by Start Date
  @GetMapping("/milestone/startDate/{startDate}")
  public ResponseEntity<List<Milestone>> getMilestoneByStartDate(@PathVariable String startDate) {

    LocalDate parsedDate = LocalDate.parse(String.valueOf(startDate));

    try {
      List<Milestone> milestones = milestoneService.getMilestoneByStartDate(parsedDate);
      if (milestones.isEmpty()) {
        return ResponseEntity.notFound().build();
      } else {
        return ResponseEntity.ok(milestones);
      }
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(null);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Get Milestones by Completion Date
  @GetMapping("/milestone/completionDate/{completionDate}")
  public ResponseEntity<List<Milestone>> getMilestoneByCompletionDate(
      @PathVariable String completionDate) {

    LocalDate parsedDate = LocalDate.parse(String.valueOf(completionDate));

    try {
      List<Milestone> milestones = milestoneService.getMilestoneByCompletionDate(parsedDate);
      if (milestones.isEmpty()) {
        return ResponseEntity.notFound().build();
      } else {
        return ResponseEntity.ok(milestones);
      }
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(null);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  // Get Milestones by Status
  @GetMapping("/milestone/status/{status}")
  public ResponseEntity<List<Milestone>> getMilestoneStatus(@PathVariable String status) {
    try {
      // Assuming that the status parameter will be passed as a String that matches the enum
      Milestone.Status milestoneStatus = Milestone.Status.valueOf(status);
      List<Milestone> milestones = milestoneService.getMilestoneByStatus(milestoneStatus);
      if (milestones.isEmpty()) {
        return ResponseEntity.notFound().build();
      } else {
        return ResponseEntity.ok(milestones);
      }
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(null);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

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

  @PostMapping("/milestone/create")
  public ResponseEntity<String> createMilestone(@RequestBody Milestone milestone) {
    try {
      milestoneService.createMilestone(milestone);
      return ResponseEntity.status(HttpStatus.CREATED).body("Milestone created successfully.");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      // Handle unexpected errors
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An unexpected error occurred: " + e.getMessage());
    }
  }

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

  @PatchMapping("/milestone/{milestoneId}/updateSavedAmount")
  public ResponseEntity<Milestone> updateSavedAmount(
      @PathVariable Integer milestoneId, @RequestParam BigDecimal addedAmount) {
    try {
      Milestone updatedMilestone =
          milestoneService.updateSavedAmountAndCheckCompletion(milestoneId, addedAmount);
      return ResponseEntity.ok(updatedMilestone);
    } catch (MilestoneException.MilestoneNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Milestone not found
    } catch (MilestoneException.InvalidAmountException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // Invalid added amount
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(null); // Other unexpected errors
    }
  }
}
