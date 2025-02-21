package com.example.savings_app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.savings_app.model.Account;
import com.example.savings_app.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/** Unit tests for the ParentChildAccountService class. */
class ParentChildAccountServiceTest {

  @Mock private CustomerService customerService; // Mock the CustomerService to simulate interaction

  @InjectMocks
  private ParentChildAccountService parentChildAccountService; // The service under test

  /** Set up the test environment before each test. Initializes mocks using Mockito annotations. */
  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this); // Initializes annotated mocks
  }

  /**
   * Test for creating an account with a valid child ID. It tests if a customer is successfully
   * created when a valid child ID is provided.
   */
  @Test
  void createAccountWithCustomer_ShouldCreateCustomer_WhenValidChildIdProvided() {
    // Create an account with valid userId (Parent ID) and childId
    Account account =
        Account.builder()
            .userId(1) // Parent ID
            .childId(2) // Valid Child ID
            .build();

    // Create a Customer object with valid customer ID (custId)
    Customer customer =
        Customer.builder()
            .parentId(account.getUserId())
            .childId(account.getChildId())
            .custId(100) // Explicitly setting a valid custId
            .build();

    // Mock the customerService to return a customer with custId 100 when createCustomer is called
    when(customerService.createCustomer(any(Customer.class))).thenReturn(customer);

    // Call the service method to create an account with the customer
    int custId = parentChildAccountService.createAccountWithCustomer(account);

    // Verify the returned customer ID matches the expected value
    assertEquals(100, custId, "Customer ID should match the mocked value");

    // Verify that the createCustomer method was called exactly once
    verify(customerService, times(1)).createCustomer(any(Customer.class));
  }

  /**
   * Test for handling the scenario where the child ID is null. It tests that an exception is thrown
   * when the child ID is missing (null).
   */
  @Test
  void createAccountWithCustomer_ShouldThrowException_WhenChildIdIsNull() {
    // Create an account with valid userId (Parent ID) but null childId
    Account account =
        Account.builder()
            .userId(1) // Parent ID
            .childId(null) // Invalid case: No child ID
            .build();

    // Check that an IllegalStateException is thrown when trying to create an account with invalid
    // childId
    Exception exception =
        assertThrows(
            IllegalStateException.class,
            () -> {
              parentChildAccountService.createAccountWithCustomer(account);
            });

    // Ensure the exception message matches the expected message
    assertEquals("Failed to create customer: child ID is null", exception.getMessage());

    // Verify that the createCustomer method is not called due to the invalid childId
    verify(customerService, never()).createCustomer(any(Customer.class));
  }
}
