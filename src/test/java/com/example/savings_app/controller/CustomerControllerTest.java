package com.example.savings_app.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.savings_app.model.Customer;
import com.example.savings_app.service.CustomerService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** Unit tests for the CustomerController class. */
@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

  @Autowired private MockMvc mockMvc; // MockMvc to simulate HTTP requests and test responses

  @MockBean
  private CustomerService customerService; // Mocked CustomerService to simulate the service layer

  private Customer customer; // Sample customer to use for tests

  /**
   * Set up a sample customer before each test. This ensures that we have a consistent object for
   * testing the controller methods.
   */
  @BeforeEach
  void setUp() {
    customer =
        Customer.builder()
            .custId(1)
            .parentId(1)
            .childId(2)
            .build(); // Creating a mock customer object
  }

  /**
   * Tests retrieving a customer by their customer ID when the customer exists. This test simulates
   * a GET request to the /customer/id/{custId} endpoint and verifies that the customer details are
   * returned correctly.
   *
   * @throws Exception if any error occurs during the test execution
   */
  @Test
  void getCustomerByCustId_ShouldReturnCustomer_WhenCustomerExists() throws Exception {

    // Mock the customerService to return the sample customer when getCustomerByCustId is called
    when(customerService.getCustomerByCustId(1)).thenReturn(Optional.of(customer));

    // Perform the GET request to fetch the customer by ID and assert the response
    mockMvc
        .perform(get("/customer/id/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()) // Expect HTTP status 200 (OK)
        .andExpect(jsonPath("$.custId").value(1)) // Expect custId to be 1
        .andExpect(jsonPath("$.parentId").value(1)) // Expect parentId to be 1
        .andExpect(jsonPath("$.childId").value(2)); // Expect childId to be 2

    // Verify that the customerService method was called exactly once
    verify(customerService, times(1)).getCustomerByCustId(1);
  }

  /**
   * Tests retrieving a customer by their customer ID when the customer does not exist. This test
   * simulates a GET request to the /customer/id/{custId} endpoint and verifies that a 404 status is
   * returned if no customer is found.
   *
   * @throws Exception if any error occurs during the test execution
   */
  @Test
  void getCustomerByCustId_ShouldReturn404_WhenCustomerDoesNotExist() throws Exception {
    // Mock the customerService to return an empty Optional when the customer does not exist
    when(customerService.getCustomerByCustId(2)).thenReturn(Optional.empty());

    // Perform the GET request and expect a 404 status since the customer does not exist
    mockMvc
        .perform(get("/customer/id/2").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound()); // Expect 404 status when customer is not found

    // Verify that the customerService method was called exactly once
    verify(customerService, times(1)).getCustomerByCustId(2);
  }

  /**
   * Tests retrieving a customer by their customer ID when the customer ID is invalid (e.g.,
   * negative). This test simulates a GET request to the /customer/id/{custId} endpoint and verifies
   * that a 400 status is returned.
   *
   * @throws Exception if any error occurs during the test execution
   */
  @Test
  void getCustomerByCustId_ShouldReturn400_WhenCustIdIsInvalid() throws Exception {
    // Mock the customerService to throw an IllegalArgumentException when an invalid customer ID is
    // provided
    when(customerService.getCustomerByCustId(-1))
        .thenThrow(new IllegalArgumentException("Invalid customer custId: -1"));

    // Perform the GET request and expect a 400 status since the customer ID is invalid
    mockMvc
        .perform(get("/customer/id/-1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest()); // Expect 400 status for invalid customer ID

    // Verify that the customerService method was called exactly once
    verify(customerService, times(1)).getCustomerByCustId(-1);
  }

  /**
   * Tests retrieving a customer by their customer ID when an unexpected error occurs. This test
   * simulates a GET request to the /customer/id/{custId} endpoint and verifies that a 500 status is
   * returned if an unexpected error occurs.
   *
   * @throws Exception if any error occurs during the test execution
   */
  @Test
  void getCustomerByCustId_ShouldReturn500_WhenUnexpectedErrorOccurs() throws Exception {
    // Mock the customerService to throw a RuntimeException when an unexpected error occurs
    when(customerService.getCustomerByCustId(3))
        .thenThrow(new RuntimeException("Unexpected error"));

    // Perform the GET request and expect a 500 status since an unexpected error occurs
    mockMvc
        .perform(get("/customer/id/3").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError()); // Expect 500 status for unexpected errors

    // Verify that the customerService method was called exactly once
    verify(customerService, times(1)).getCustomerByCustId(3);
  }
}
