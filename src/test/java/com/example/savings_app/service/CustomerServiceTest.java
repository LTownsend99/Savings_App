package com.example.savings_app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.savings_app.model.Account;
import com.example.savings_app.model.Customer;
import com.example.savings_app.repository.CustomerRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CustomerServiceTest {

  private CustomerRepository customerRepository;
  private CustomerService customerService;

  @Mock private AccountService accountService;

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

  @BeforeEach
  void setUp() {
    accountService = mock(AccountService.class);
    customerRepository = mock(CustomerRepository.class);
    customerService = new CustomerService(customerRepository);
  }

  @Test
  void getCustomerByCustId_ShouldReturnCustomer_WhenCustomerExists() {
    Customer customer = Customer.builder().custId(CUST_ID).parentId(1).childId(2).build();

    when(customerRepository.findById(CUST_ID)).thenReturn(Optional.of(customer));

    Optional<Customer> result = customerService.getCustomerByCustId(CUST_ID);

    assertTrue(result.isPresent());
    assertEquals(CUST_ID, result.get().getCustId());
    assertEquals(1, result.get().getParentId());
    verify(customerRepository, times(1)).findById(CUST_ID);
  }

  @Test
  void getCustomerByCustId_ShouldReturnEmptyOptional_WhenCustomerDoesNotExist() {
    int custId = 2;
    when(customerRepository.findById(custId)).thenReturn(Optional.empty());

    Optional<Customer> result = customerService.getCustomerByCustId(custId);

    assertFalse(result.isPresent());
    verify(customerRepository, times(1)).findById(custId);
  }

  @Test
  void getCustomerByCustId_ShouldThrowIllegalArgumentException_WhenInvalidCustIdProvided() {
    int custId = -1;
    when(customerRepository.findById(custId)).thenThrow(new IllegalArgumentException("Invalid ID"));

    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              customerService.getCustomerByCustId(custId);
            });

    assertTrue(exception.getMessage().contains("Invalid customer custId: -1"));
    verify(customerRepository, never()).findById(custId);
  }

  @Test
  void getCustomerByCustId_ShouldThrowRuntimeException_WhenUnexpectedErrorOccurs() {
    int custId = 3;

    // Mock the behavior where findById throws a RuntimeException
    when(customerRepository.findById(custId)).thenThrow(new RuntimeException("Unexpected error"));

    // Call the method and assert that it throws a RuntimeException
    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> {
              customerService.getCustomerByCustId(custId);
            });

    // Check that the exception message contains the expected message
    assertTrue(exception.getMessage().contains("Failed to retrieve customer with custId: 3"));

    // Verify that the repository method was indeed called
    verify(customerRepository, times(1)).findById(custId);
  }

  @Test
  public void testCreateCustomerSuccess() {
    // Arrange
    when(accountService.getAccountByUserId(parentAccount.getUserId()))
        .thenReturn(java.util.Optional.of(parentAccount));
    when(accountService.getAccountByUserId(childAccount.getUserId()))
        .thenReturn(java.util.Optional.of(childAccount));

    // Act
    customerService.createCustomer(customer);

    // Assert
    verify(customerRepository, times(1)).save(customer);
  }

  @Test
  public void testCreateCustomer_InvalidParentAccount() {
    // Arrange
    when(accountService.getAccountByUserId(parentAccount.getUserId()))
        .thenReturn(java.util.Optional.empty());
    when(accountService.getAccountByUserId(childAccount.getUserId()))
        .thenReturn(java.util.Optional.of(childAccount));

    // Customer with null parentId
    Customer invalidCustomer =
        Customer.builder()
            .custId(CUST_ID)
            .childId(childAccount.getUserId()) // valid child ID
            .parentId(null) // invalid parent ID
            .build();

    // Act and Assert
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              customerService.createCustomer(invalidCustomer);
            });

    // Validate the exception message
    assertEquals("Both parent and child accounts must be provided.", thrown.getMessage());
  }

  @Test
  public void testCreateCustomer_InvalidChildAccount() {
    // Arrange
    when(accountService.getAccountByUserId(parentAccount.getUserId()))
        .thenReturn(java.util.Optional.of(parentAccount));
    when(accountService.getAccountByUserId(childAccount.getUserId()))
        .thenReturn(java.util.Optional.empty());

    // Customer with null parentId
    Customer invalidCustomer =
        Customer.builder()
            .custId(CUST_ID)
            .childId(null) // valid child ID
            .parentId(parentAccount.getUserId()) // invalid parent ID
            .build();

    // Act and Assert
    IllegalArgumentException thrown =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              customerService.createCustomer(invalidCustomer);
            });

    // Validate the exception message
    assertEquals("Both parent and child accounts must be provided.", thrown.getMessage());
  }
}
