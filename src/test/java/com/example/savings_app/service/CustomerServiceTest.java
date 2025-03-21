package com.example.savings_app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.savings_app.model.Account;
import com.example.savings_app.model.Customer;
import com.example.savings_app.repository.CustomerRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

/** Unit tests for the CustomerService class. */
@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

  // Mocked dependencies
  private CustomerRepository customerRepository;
  private CustomerService customerService;
  @Mock private AccountService accountService;

  // Constants for testing
  private final int CUST_ID = 1;

  private final Account parentAccount =
      Account.builder().userId(10).role(Account.Role.parent).build();

  private final Account childAccount =
      Account.builder().userId(20).role(Account.Role.child).build();

  private final Customer customer =
      Customer.builder()
          .parentId(parentAccount.getUserId())
          .childId(childAccount.getUserId())
          .build();

  /** Set up the test environment by initializing the mocked dependencies. */
  @BeforeEach
  void setUp() {
    accountService = mock(AccountService.class); // Mock AccountService
    customerRepository = mock(CustomerRepository.class); // Mock CustomerRepository
    customerService = new CustomerService(customerRepository); // Initialize CustomerService
  }

  /**
   * Test case for retrieving a customer by a valid customer ID. Verifies that the correct customer
   * is returned when it exists in the repository.
   */
  @Test
  void getCustomerByCustId_ShouldReturnCustomer_WhenCustomerExists() {
    Customer customer = Customer.builder().custId(CUST_ID).parentId(1).childId(2).build();

    // Mock the repository method to return a customer
    when(customerRepository.findById(CUST_ID)).thenReturn(Optional.of(customer));

    // Call the service method
    Optional<Customer> result = customerService.getCustomerByCustId(CUST_ID);

    // Assert that the customer is present and the correct customer data is returned
    assertTrue(result.isPresent());
    assertEquals(CUST_ID, result.get().getCustId());
    assertEquals(1, result.get().getParentId());

    // Verify that the repository method was called once
    verify(customerRepository, times(1)).findById(CUST_ID);
  }

  /**
   * Test case for retrieving a customer by an invalid customer ID that does not exist. Verifies
   * that an empty Optional is returned when the customer is not found.
   */
  @Test
  void getCustomerByCustId_ShouldReturnEmptyOptional_WhenCustomerDoesNotExist() {
    int custId = 2;

    // Mock the repository method to return empty for non-existing customer
    when(customerRepository.findById(custId)).thenReturn(Optional.empty());

    // Call the service method
    Optional<Customer> result = customerService.getCustomerByCustId(custId);

    // Assert that the result is empty
    assertFalse(result.isPresent());

    // Verify that the repository method was called once
    verify(customerRepository, times(1)).findById(custId);
  }

  /**
   * Test case for handling an invalid customer ID. Verifies that an IllegalArgumentException is
   * thrown for invalid customer IDs.
   */
  @Test
  void getCustomerByCustId_ShouldThrowIllegalArgumentException_WhenInvalidCustIdProvided() {
    int custId = -1;

    // Assert that the exception is thrown with the expected message
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              customerService.getCustomerByCustId(custId);
            });

    assertTrue(exception.getMessage().contains("Invalid customer custId: -1"));

    // Verify that the repository method was not called
    verify(customerRepository, never()).findById(custId);
  }

  /**
   * Test case for handling unexpected errors that may occur during customer retrieval. Verifies
   * that a RuntimeException is thrown if an unexpected error occurs.
   */
  @Test
  void getCustomerByCustId_ShouldThrowRuntimeException_WhenUnexpectedErrorOccurs() {
    int custId = 3;

    // Mock the repository method to throw a RuntimeException
    when(customerRepository.findById(custId)).thenThrow(new RuntimeException("Unexpected error"));

    // Call the method and assert that it throws a RuntimeException
    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> {
              customerService.getCustomerByCustId(custId);
            });

    // Assert that the exception message contains the expected message
    assertTrue(exception.getMessage().contains("Failed to retrieve customer with custId: 3"));

    // Verify that the repository method was called once
    verify(customerRepository, times(1)).findById(custId);
  }

  /**
   * Test case for creating a customer successfully. Verifies that a customer is created and saved
   * to the repository when the accounts are valid.
   */
  @Test
  public void testCreateCustomerSuccess() {
    // Call the createCustomer method
    customerService.createCustomer(customer);

    // Verify that the customerRepository.save method was called once
    verify(customerRepository, times(1)).save(customer);
  }

  /**
   * Test case for creating a customer when the parent account is invalid (not found). Verifies that
   * an IllegalArgumentException is thrown if the parent account is invalid.
   */
  @Test
  public void testCreateCustomer_InvalidParentAccount() {
        // Create an invalid customer with a null parentId
    Customer invalidCustomer =
        Customer.builder()
            .custId(CUST_ID)
            .childId(childAccount.getUserId()) // valid child ID
            .parentId(null) // invalid parent ID
            .build();

    // Assert that an exception is thrown with the expected message
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              customerService.createCustomer(invalidCustomer);
            });

    assertEquals("Both parent and child accounts must be provided.", thrown.getMessage());
  }

  /**
   * Test case for creating a customer when the child account is invalid (not found). Verifies that
   * an IllegalArgumentException is thrown if the child account is invalid.
   */
  @Test
  public void testCreateCustomer_InvalidChildAccount() {

    // Create an invalid customer with a null childId
    Customer invalidCustomer =
        Customer.builder()
            .custId(CUST_ID)
            .childId(null) // invalid child ID
            .parentId(parentAccount.getUserId()) // valid parent ID
            .build();

    // Assert that an exception is thrown with the expected message
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              customerService.createCustomer(invalidCustomer);
            });

    assertEquals("Both parent and child accounts must be provided.", thrown.getMessage());
  }
}
