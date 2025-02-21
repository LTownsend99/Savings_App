package com.example.savings_app.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.savings_app.SavingsAppApplication;
import com.example.savings_app.model.Account;
import com.example.savings_app.service.AccountService;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

/** Unit tests for the AccountController class. */
@WebMvcTest(AccountController.class)
@ContextConfiguration(classes = SavingsAppApplication.class)
public class AccountControllerTest {

  @Autowired private MockMvc mockMvc; // MockMvc to simulate HTTP requests and test responses

  @MockBean
  private AccountService accountService; // Mocked AccountService to simulate the service layer

  private Account account; // Sample account to use for tests

  /**
   * Set up a sample account before each test. This ensures that we have a consistent object for
   * testing the controller methods.
   */
  @BeforeEach
  void setUp() {
    account =
        Account.builder()
            .userId(1)
            .firstName("John")
            .lastName("Smith")
            .email("test@example.com")
            .passwordHash("password")
            .role(Account.Role.parent)
            .dob(LocalDate.parse("1999-11-10"))
            .build();
  }

  /**
   * Tests the creation of an account through the AccountController. This simulates a POST request
   * to the /account/create endpoint and verifies that the response contains the expected values.
   *
   * @throws Exception if any error occurs during the test execution
   */
  @Test
  void testCreateAccount() throws Exception {

    // Mock the accountService to return the sample account when createAccount is called
    when(accountService.createAccount(any(Account.class))).thenReturn(account);

    // Perform the POST request to create an account and assert the response
    mockMvc
        .perform(
            post("/account/create") // Ensure this matches the controller's URL
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{ \"firstName\": \"John\", \"lastName\": \"Smith\", \"email\": \"test@example.com\", "
                        + "\"passwordHash\": \"hashed_password\", \"role\": \"parent\", \"createdAt\": \"2023-11-01\", \"dob\": \"2023-11-01\" }"))
        .andExpect(status().isOk()) // Expect HTTP status 200 (OK)
        .andExpect(jsonPath("$.firstName", is("John"))) // Expect firstName field to be "John"
        .andExpect(jsonPath("$.lastName", is("Smith"))) // Expect lastName field to be "Smith"
        .andExpect(jsonPath("$.email", is("test@example.com"))); // Expect email field to match
  }

  /**
   * Tests retrieving an account by user ID. This test simulates a GET request to the
   * /account/id/{userId} endpoint and verifies that the account details are returned correctly.
   *
   * @throws Exception if any error occurs during the test execution
   */
  @Test
  void getAccountByUserId_ShouldReturnAccount_WhenAccountExists() throws Exception {
    // Mock the accountService to return the sample account when getAccountByUserId is called
    when(accountService.getAccountByUserId(1)).thenReturn(Optional.of(account));

    // Perform the GET request to fetch the account by ID and assert the response
    mockMvc
        .perform(get("/account/id/1"))
        .andExpect(status().isOk()) // Expect HTTP status 200 (OK)
        .andExpect(jsonPath("$.userId").value(1)) // Expect userId to be 1
        .andExpect(jsonPath("$.firstName").value("John")) // Expect firstName to be "John"
        .andExpect(jsonPath("$.lastName").value("Smith")) // Expect lastName to be "Smith"
        .andExpect(jsonPath("$.email").value("test@example.com")); // Expect email to match

    // Verify that the accountService method was called exactly once
    verify(accountService, times(1)).getAccountByUserId(1);
  }

  /**
   * Tests retrieving an account by user ID when the account does not exist. This test simulates a
   * GET request to the /account/id/{userId} endpoint and verifies that a 404 status is returned.
   *
   * @throws Exception if any error occurs during the test execution
   */
  @Test
  void getAccountByUserId_ShouldReturn404_WhenAccountDoesNotExist() throws Exception {
    // Mock the accountService to return an empty Optional when the account is not found
    when(accountService.getAccountByUserId(99)).thenReturn(Optional.empty());

    // Perform the GET request and expect a 404 status since the account does not exist
    mockMvc.perform(get("/account/id/99")).andExpect(status().isNotFound());

    // Verify that the accountService method was called exactly once
    verify(accountService, times(1)).getAccountByUserId(99);
  }
}
