package com.example.savings_app.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.savings_app.model.Account;
import com.example.savings_app.model.Savings;
import com.example.savings_app.service.AccountService;
import com.example.savings_app.service.SavingsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** Unit tests for the SavingsController class . */
@WebMvcTest(SavingsController.class)
public class SavingsControllerTest {

  @Autowired private MockMvc mockMvc;

  // Mock services for savings and account operations
  @MockBean private SavingsService savingsService;
  @MockBean private AccountService accountService;

  private Savings savings;
  private Account account;
  private static final LocalDate NOW = LocalDate.parse("2024-11-16");

  private final ObjectMapper objectMapper = new ObjectMapper();

  /** Setup method to initialize common objects before each test. */
  @BeforeEach
  public void setUp() {
    // Initialize a savings object with sample data
    savings =
        Savings.builder()
            .savingsId(1)
            .amount(BigDecimal.valueOf(100.00))
            .date(NOW)
            .milestoneId(1)
            .build();

    // Initialize an account object with sample data
    account = Account.builder().userId(1).firstName("testUser").email("test@example.com").build();
  }

  /** Test case for successful retrieval of savings by ID. */
  @Test
  public void testGetSavingsById_Success() throws Exception {
    // Mock the service to return a savings object when the savings ID is 1
    when(savingsService.getSavingsById(1)).thenReturn(Optional.of(savings));

    // Perform the GET request and validate the response
    mockMvc
        .perform(get("/savings/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.savingsId").value(1))
        .andExpect(jsonPath("$.amount").value(100.00));

    // Verify the service method was called once
    verify(savingsService, times(1)).getSavingsById(1);
  }

  /** Test case for when savings by ID are not found. */
  @Test
  public void testGetSavingsById_NotFound() throws Exception {
    // Mock the service to return empty when the savings ID is 1
    when(savingsService.getSavingsById(1)).thenReturn(Optional.empty());

    // Perform the GET request and expect 404 Not Found
    mockMvc
        .perform(get("/savings/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());

    // Verify the service method was called once
    verify(savingsService, times(1)).getSavingsById(1);
  }

  /** Test case for bad request when an invalid savings ID is provided. */
  @Test
  public void testGetSavingsById_BadRequest() throws Exception {
    // Mock the service to throw an exception for an invalid savings ID
    when(savingsService.getSavingsById(-1)).thenThrow(new IllegalArgumentException("Invalid ID"));

    // Perform the GET request and expect 400 Bad Request
    mockMvc
        .perform(get("/savings/-1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    // Verify the service method was called once
    verify(savingsService, times(1)).getSavingsById(-1);
  }

  /** Test case for successful retrieval of savings by date. */
  @Test
  public void testGetSavingsByDate_Success() throws Exception {
    LocalDate FormatDate = LocalDate.parse("2024-11-01");

    // Mock the service to return a list of savings when queried by date
    when(savingsService.getSavingsByDate(FormatDate)).thenReturn(Arrays.asList(savings));

    // Perform the GET request using the date as a path variable
    String dateString = FormatDate.toString();
    mockMvc
        .perform(get("/savings/date/" + dateString).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].savingsId").value(1))
        .andExpect(jsonPath("$[0].amount").value(100.00));

    // Verify the service method was called once with the correct date
    verify(savingsService, times(1)).getSavingsByDate(FormatDate);
  }

  /** Test case for when no savings are found for the given date. */
  @Test
  public void testGetSavingsByDate_NotFound() throws Exception {
    LocalDate startDate = LocalDate.parse("2024-11-01");

    // Mock the service to return an empty list for the given date
    when(savingsService.getSavingsByDate(startDate)).thenReturn(Arrays.asList());

    // Perform the GET request and expect 404 Not Found
    String dateString = startDate.toString();
    mockMvc
        .perform(get("/savings/date/" + dateString).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());

    // Verify the service method was called once with the correct date
    verify(savingsService, times(1)).getSavingsByDate(startDate);
  }

  /** Test case for successful retrieval of savings by milestone ID. */
  @Test
  public void testGetSavingsByMilestoneId_Success() throws Exception {
    // Mock the service to return a savings object when the milestone ID is 1
    when(savingsService.getSavingsByMilestoneId(1)).thenReturn(Optional.of(savings));

    // Perform the GET request and validate the response
    mockMvc
        .perform(get("/savings/milestone/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.savingsId").value(1))
        .andExpect(jsonPath("$.amount").value(100.00));

    // Verify the service method was called once
    verify(savingsService, times(1)).getSavingsByMilestoneId(1);
  }

  /** Test case for when no savings are found for the given milestone ID. */
  @Test
  public void testGetSavingsByMilestoneId_NotFound() throws Exception {
    // Mock the service to return empty when the milestone ID is 1
    when(savingsService.getSavingsByMilestoneId(1)).thenReturn(Optional.empty());

    // Perform the GET request and expect 404 Not Found
    mockMvc
        .perform(get("/savings/milestone/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());

    // Verify the service method was called once
    verify(savingsService, times(1)).getSavingsByMilestoneId(1);
  }

  /** Test case for bad request when an invalid milestone ID is provided. */
  @Test
  public void testGetSavingsByMilestoneId_BadRequest() throws Exception {
    // Mock the service to throw an exception for an invalid milestone ID
    when(savingsService.getSavingsByMilestoneId(-1))
        .thenThrow(new IllegalArgumentException("Invalid Milestone ID"));

    // Perform the GET request and expect 400 Bad Request
    mockMvc
        .perform(get("/savings/milestone/-1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    // Verify the service method was called once
    verify(savingsService, times(1)).getSavingsByMilestoneId(-1);
  }

  /** Test case for successful deletion of savings. */
  @Test
  public void deleteSavings_Success() throws Exception {
    // Perform the DELETE request to delete savings by ID
    mockMvc.perform(delete("/savings/1")).andExpect(status().isNoContent());

    // Verify the service method was called once
    verify(savingsService, times(1)).deleteSavings(1);
  }

  /** Test case for deletion failure due to invalid savings ID. */
  @Test
  public void deleteSavings_InvalidId() throws Exception {
    // Simulate an exception thrown by the service when trying to delete an invalid ID
    doThrow(new IllegalArgumentException("Invalid Savings Id: -1"))
        .when(savingsService)
        .deleteSavings(-1);

    // Perform the DELETE request and expect 400 Bad Request
    mockMvc.perform(delete("/savings/-1")).andExpect(status().isBadRequest());

    // Verify the service method was called once
    verify(savingsService, times(1)).deleteSavings(-1);
  }

  /** Test case for successful retrieval of all savings for a user. */
  @Test
  public void testGetAllSavingsForUser_Success() throws Exception {
    // Mock the service to return an account and savings for the user with ID 1
    when(accountService.getAccountByUserId(1)).thenReturn(Optional.of(account));
    when(savingsService.getAllSavingsForUser(account)).thenReturn(Arrays.asList(savings));

    // Perform the GET request and validate the response
    mockMvc
        .perform(get("/savings/user/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].savingsId").value(1))
        .andExpect(jsonPath("$[0].amount").value(100.00));

    // Verify the service methods were called with the correct parameters
    verify(accountService, times(1)).getAccountByUserId(1);
    verify(savingsService, times(1)).getAllSavingsForUser(account);
  }

  /** Test case for when no savings are found for the user. */
  @Test
  public void testGetAllSavingsForUser_NotFound() throws Exception {
    // Mock the service to return an empty list when no savings are found for the user
    when(accountService.getAccountByUserId(1)).thenReturn(Optional.of(account));
    when(savingsService.getAllSavingsForUser(account)).thenReturn(Arrays.asList());

    // Perform the GET request and expect 204 No Content
    mockMvc
        .perform(get("/savings/user/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    // Verify the service methods were called with the correct parameters
    verify(accountService, times(1)).getAccountByUserId(1);
    verify(savingsService, times(1)).getAllSavingsForUser(account);
  }

  /** Test case for bad request when no account is found for the user. */
  @Test
  public void testGetAllSavingsForUser_BadRequest() throws Exception {
    // Mock the service to return an empty account
    when(accountService.getAccountByUserId(1)).thenReturn(Optional.empty());

    // Perform the GET request and expect 400 Bad Request
    mockMvc
        .perform(get("/savings/user/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    // Verify the account service was called once and savings service was never called
    verify(accountService, times(1)).getAccountByUserId(1);
    verify(savingsService, never()).getAllSavingsForUser(any());
  }

  /** Test case for successful creation of new savings. */
  @Test
  public void testCreateSavings_Success() throws Exception {
    String savingsJson =
        "{" + "\"savingsId\": 1, \"amount\": 100.00, \"date\": \"2024-11-16\", \"milestoneId\": 1}";

    // Mock the service to return the created savings
    when(savingsService.createSavings(any(Savings.class))).thenReturn(savings);

    // Perform the POST request and expect success
    mockMvc
        .perform(
            post("/savings/create").contentType(MediaType.APPLICATION_JSON).content(savingsJson))
        .andExpect(status().isCreated())
        .andExpect(content().string("Savings created successfully."));

    // Verify the service method was called once with the provided savings data
    verify(savingsService, times(1)).createSavings(any(Savings.class));
  }

  /** Test case for bad request during savings creation due to invalid data. */
  @Test
  public void testCreateSavings_BadRequest() throws Exception {
    String savingsJson =
        "{" + "\"savingsId\": 1, \"amount\": 100.00, \"date\": \"2024-11-16\", \"milestoneId\": 1}";

    // Simulate an exception thrown by the service due to invalid data
    doThrow(new IllegalArgumentException("Invalid savings data"))
        .when(savingsService)
        .createSavings(any(Savings.class));

    // Perform the POST request and expect 400 Bad Request
    mockMvc
        .perform(
            post("/savings/create").contentType(MediaType.APPLICATION_JSON).content(savingsJson))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Invalid savings data"));

    // Verify the service method was called once with the provided savings data
    verify(savingsService, times(1)).createSavings(any(Savings.class));
  }
}
