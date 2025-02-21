package com.example.savings_app.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.savings_app.model.Account;
import com.example.savings_app.model.Milestone;
import com.example.savings_app.service.AccountService;
import com.example.savings_app.service.MilestoneService;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

/** Unit tests for the MilestoneController class */
@WebMvcTest(MilestoneController.class)
public class MilestoneControllerTest {

  // Mocked services
  @MockBean private MilestoneService milestoneService;
  @MockBean private AccountService accountService;

  // Mocked MVC for performing HTTP requests
  @Autowired private MockMvc mockMvc;

  private Milestone milestone; // Sample Milestone object for testing
  private LocalDate startDate; // Start date for milestone
  private LocalDate completionDate; // Completion date for milestone
  private Account account; // Sample Account object for testing

  /**
   * Set up the test data before each test case. Initializes sample objects for Milestone and
   * Account, along with dates.
   */
  @BeforeEach
  public void setUp() throws ParseException {
    // Initialize test data with predefined values
    startDate = LocalDate.parse("2024-11-01");
    completionDate = LocalDate.parse("2024-11-01");
    milestone =
        Milestone.builder()
            .milestoneId(1)
            .milestoneName("Milestone")
            .targetAmount(BigDecimal.valueOf(200.00))
            .savedAmount(BigDecimal.valueOf(50.00))
            .startDate(startDate)
            .completionDate(completionDate)
            .status(Milestone.Status.active)
            .build();

    account = Account.builder().userId(1).firstName("testUser").email("test@example.com").build();
  }

  /** Test case for retrieving a milestone by its ID with a successful response. */
  @Test
  public void testGetMilestoneByMilestoneId_Success() throws Exception {
    // Mock the service method to return a milestone with ID 1
    when(milestoneService.getMilestoneByMilestoneId(1)).thenReturn(Optional.of(milestone));

    // Perform the GET request to the controller and assert the response
    mockMvc
        .perform(get("/milestone/1"))
        .andExpect(status().isOk()) // Expecting HTTP status 200 (OK)
        .andExpect(jsonPath("$.milestoneId").value(1)) // Assert milestoneId value
        .andExpect(jsonPath("$.milestoneName").value("Milestone")) // Assert milestoneName value
        .andExpect(jsonPath("$.targetAmount").value(200.00)) // Assert targetAmount value
        .andExpect(jsonPath("$.savedAmount").value(50.00)) // Assert savedAmount value
        .andExpect(jsonPath("$.status").value("active")); // Assert status value

    // Verify that the service method was called exactly once with the correct parameter
    verify(milestoneService, times(1)).getMilestoneByMilestoneId(1);
  }

  /** Test case for handling a scenario where the milestone ID is not found in the database. */
  @Test
  public void testGetMilestoneByMilestoneId_NotFound() throws Exception {
    // Mock the service to return an empty Optional, simulating not found
    when(milestoneService.getMilestoneByMilestoneId(2)).thenReturn(Optional.empty());

    // Perform the GET request for a non-existent milestone and assert the Not Found response
    mockMvc.perform(get("/milestone/2")).andExpect(status().isNotFound());

    // Verify that the service method was called exactly once
    verify(milestoneService, times(1)).getMilestoneByMilestoneId(2);
  }

  /** Test case for handling a bad request when an invalid milestone ID is passed. */
  @Test
  public void testGetMilestoneByMilestoneId_BadRequest() throws Exception {
    // Simulate the case when the service throws an IllegalArgumentException for invalid milestoneId
    when(milestoneService.getMilestoneByMilestoneId(-1))
        .thenThrow(new IllegalArgumentException("Invalid Milestone milestoneId: -1"));

    // Perform the GET request for an invalid ID and expect a Bad Request response
    mockMvc.perform(get("/milestone/-1")).andExpect(status().isBadRequest());

    // Verify that the service method was called with the invalid ID
    verify(milestoneService, times(1)).getMilestoneByMilestoneId(-1);
  }

  /** Test case for handling an internal server error scenario. */
  @Test
  public void testGetMilestoneByMilestoneId_InternalServerError() throws Exception {
    // Simulate the case when the service throws a RuntimeException
    when(milestoneService.getMilestoneByMilestoneId(1))
        .thenThrow(new RuntimeException("Failed to retrieve Milestone"));

    // Perform the GET request and expect an Internal Server Error response
    mockMvc.perform(get("/milestone/1")).andExpect(status().isInternalServerError());

    // Verify that the service method was called with the correct parameter
    verify(milestoneService, times(1)).getMilestoneByMilestoneId(1);
  }

  /** Test case for retrieving a milestone by name with a successful response. */
  @Test
  public void testFindByName_Success() throws Exception {
    // Mock the service to return a milestone with the given name
    when(milestoneService.getMilestoneByName("Milestone")).thenReturn(Optional.of(milestone));

    // Perform the GET request and assert the response
    mockMvc
        .perform(get("/milestone/name/Milestone"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.milestoneId").value(1))
        .andExpect(jsonPath("$.milestoneName").value("Milestone"));

    // Verify that the service method was called with the correct name
    verify(milestoneService, times(1)).getMilestoneByName("Milestone");
  }

  /** Test case for handling a not found scenario when searching for a milestone by name. */
  @Test
  public void testFindByName_NotFound() throws Exception {
    // Mock the service to return an empty Optional for a non-existent milestone
    when(milestoneService.getMilestoneByName("Nonexistent Milestone")).thenReturn(Optional.empty());

    // Perform the GET request and expect a Not Found response
    mockMvc.perform(get("/milestone/name/Nonexistent Milestone")).andExpect(status().isNotFound());

    // Verify that the service method was called with the correct name
    verify(milestoneService, times(1)).getMilestoneByName("Nonexistent Milestone");
  }

  // Additional test cases would continue here with similar patterns of mocking service calls,
  // performing HTTP requests, and verifying responses for different scenarios.

  // For example, testing for different status codes (BadRequest, NotFound, etc.),
  // testing CRUD operations, and testing with different milestone attributes.
}
