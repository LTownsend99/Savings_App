package com.example.savings_app.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.savings_app.model.Account;
import com.example.savings_app.model.Milestone;
import com.example.savings_app.model.Savings;
import com.example.savings_app.service.AccountService;
import com.example.savings_app.service.SavingsService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SavingsController.class)
public class SavingsControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private SavingsService savingsService;
  @MockBean private AccountService accountService;

  private Savings savings;
  private Account account;
  private static final LocalDate NOW = LocalDate.parse("2024-11-16");

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  public void setUp() {
    savings =
        Savings.builder()
            .savingsId(1)
            .amount(BigDecimal.valueOf(100.00))
            .date(NOW)
            .milestoneId(1)
            .build();

    account = Account.builder().userId(1).firstName("testUser").email("test@example.com").build();
  }

  @Test
  public void testGetSavingsById_Success() throws Exception {
    when(savingsService.getSavingsById(1)).thenReturn(Optional.of(savings));

    mockMvc
        .perform(get("/savings/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.savingsId").value(1))
        .andExpect(jsonPath("$.amount").value(100.00));

    verify(savingsService, times(1)).getSavingsById(1);
  }

  @Test
  public void testGetSavingsById_NotFound() throws Exception {
    when(savingsService.getSavingsById(1)).thenReturn(Optional.empty());

    mockMvc
        .perform(get("/savings/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());

    verify(savingsService, times(1)).getSavingsById(1);
  }

  @Test
  public void testGetSavingsById_BadRequest() throws Exception {
    when(savingsService.getSavingsById(-1)).thenThrow(new IllegalArgumentException("Invalid ID"));

    mockMvc
        .perform(get("/savings/-1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    verify(savingsService, times(1)).getSavingsById(-1);
  }

  @Test
  public void testGetSavingsByDate_Success() throws Exception {

    LocalDate FormatDate = LocalDate.parse("2024-11-01");

    when(savingsService.getSavingsByDate(FormatDate)).thenReturn(Arrays.asList(savings));

    String dateString = FormatDate.toString();

    mockMvc
        .perform(
            get("/savings/date/" + dateString) // Pass date as part of the path
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].savingsId").value(1))
        .andExpect(jsonPath("$[0].amount").value(100.00));

    verify(savingsService, times(1)).getSavingsByDate(FormatDate);
  }

  @Test
  public void testGetSavingsByDate_NotFound() throws Exception {
    LocalDate startDate = LocalDate.parse("2024-11-01");

    // Mock the service to return an empty list
    when(savingsService.getSavingsByDate(startDate)).thenReturn(Arrays.asList());

    String dateString = startDate.toString();

    // Perform the mock request using the correct path variable
    mockMvc
        .perform(get("/savings/date/" + dateString).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound()); // Assert that the status is 404 Not Found

    // Verify that the service method was called with the correct parameter
    verify(savingsService, times(1)).getSavingsByDate(startDate);
  }

  @Test
  public void testGetSavingsByMilestoneId_Success() throws Exception {
    when(savingsService.getSavingsByMilestoneId(1)).thenReturn(Optional.of(savings));

    mockMvc
        .perform(get("/savings/milestone/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.savingsId").value(1))
        .andExpect(jsonPath("$.amount").value(100.00));

    verify(savingsService, times(1)).getSavingsByMilestoneId(1);
  }

  @Test
  public void testGetSavingsByMilestoneId_NotFound() throws Exception {
    when(savingsService.getSavingsByMilestoneId(1)).thenReturn(Optional.empty());

    mockMvc
        .perform(get("/savings/milestone/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());

    verify(savingsService, times(1)).getSavingsByMilestoneId(1);
  }

  @Test
  public void testGetSavingsByMilestoneId_BadRequest() throws Exception {
    when(savingsService.getSavingsByMilestoneId(-1))
        .thenThrow(new IllegalArgumentException("Invalid Milestone ID"));

    mockMvc
        .perform(get("/savings/milestone/-1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    verify(savingsService, times(1)).getSavingsByMilestoneId(-1);
  }

  @Test
  public void deleteSavings_Success() throws Exception {
    // Perform the DELETE request
    mockMvc.perform(delete("/savings/1")).andExpect(status().isNoContent());

    // Verify the service method is called
    verify(savingsService, times(1)).deleteSavings(1);
  }

  @Test
  public void deleteSavings_InvalidId() throws Exception {
    // Simulate an exception thrown by the service
    doThrow(new IllegalArgumentException("Invalid Savings Id: -1"))
        .when(savingsService)
        .deleteSavings(-1);

    // Perform the DELETE request
    mockMvc.perform(delete("/savings/-1")).andExpect(status().isBadRequest());

    // Verify the service method is called
    verify(savingsService, times(1)).deleteSavings(-1);
  }

  @Test
  public void testGetAllSavingsForUser_Success() throws Exception {
    when(accountService.getAccountByUserId(1)).thenReturn(Optional.of(account));
    when(savingsService.getAllSavingsForUser(account)).thenReturn(Arrays.asList(savings));

    mockMvc
        .perform(get("/savings/user/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].savingsId").value(1))
        .andExpect(jsonPath("$[0].amount").value(100.00));

    verify(accountService, times(1)).getAccountByUserId(1);
    verify(savingsService, times(1)).getAllSavingsForUser(account);
  }

  @Test
  public void testGetAllSavingsForUser_NotFound() throws Exception {
    when(accountService.getAccountByUserId(1)).thenReturn(Optional.of(account));
    when(savingsService.getAllSavingsForUser(account)).thenReturn(Arrays.asList());

    mockMvc
        .perform(get("/savings/user/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    verify(accountService, times(1)).getAccountByUserId(1);
    verify(savingsService, times(1)).getAllSavingsForUser(account);
  }

  @Test
  public void testGetAllSavingsForUser_BadRequest() throws Exception {
    when(accountService.getAccountByUserId(1)).thenReturn(Optional.empty());

    mockMvc
        .perform(get("/savings/user/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    verify(accountService, times(1)).getAccountByUserId(1);
    verify(savingsService, never()).getAllSavingsForUser(any());
  }

  @Test
  public void testCreateSavings_Success() throws Exception {
    String savingsJson = "{" +
            "\"savingsId\": 1, \"amount\": 100.00, \"date\": \"2024-11-16\", \"milestoneId\": 1}";

    when(savingsService.createSavings(any(Savings.class))).thenReturn(savings);

    mockMvc
            .perform(
                    post("/savings/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(savingsJson))
            .andExpect(status().isCreated())
            .andExpect(content().string("Savings created successfully."));

    verify(savingsService, times(1)).createSavings(any(Savings.class));
  }

  @Test
  public void testCreateSavings_BadRequest() throws Exception {
    String savingsJson = "{" +
            "\"savingsId\": 1, \"amount\": 100.00, \"date\": \"2024-11-16\", \"milestoneId\": 1}";

    doThrow(new IllegalArgumentException("Invalid savings data"))
            .when(savingsService)
            .createSavings(any(Savings.class));

    mockMvc
            .perform(
                    post("/savings/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(savingsJson))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Invalid savings data"));

    verify(savingsService, times(1)).createSavings(any(Savings.class));
  }
}
