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

class ParentChildAccountServiceTest {

  @Mock private CustomerService customerService;

  @InjectMocks private ParentChildAccountService parentChildAccountService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createAccountWithCustomer_ShouldCreateCustomer_WhenValidChildIdProvided() {
    // Arrange
    Account account =
        Account.builder()
            .userId(1) // Parent ID
            .childId(2) // Valid Child ID
            .build();

    // Create a Customer with a valid custId
    Customer customer =
        Customer.builder()
            .parentId(account.getUserId())
            .childId(account.getChildId())
            .custId(100) // Explicitly setting a valid custId
            .build();

    // Mock the customerService to return a customer with custId
    when(customerService.createCustomer(any(Customer.class))).thenReturn(customer);

    // Act
    int custId = parentChildAccountService.createAccountWithCustomer(account);

    // Assert
    assertEquals(100, custId, "Customer ID should match the mocked value");
    verify(customerService, times(1)).createCustomer(any(Customer.class));
  }

  @Test
  void createAccountWithCustomer_ShouldThrowException_WhenChildIdIsNull() {
    Account account =
        Account.builder()
            .userId(1) // Parent ID
            .childId(null) // No child ID (invalid case)
            .build();

    Exception exception =
        assertThrows(
            IllegalStateException.class,
            () -> {
              parentChildAccountService.createAccountWithCustomer(account);
            });

    assertEquals("Failed to create customer", exception.getMessage());
    verify(customerService, never())
        .createCustomer(any(Customer.class)); // Ensure service is not called
  }
}
