package com.example.savings_app.controller;

import com.example.savings_app.model.Customer;
import com.example.savings_app.service.CustomerService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * CustomerController is a REST controller that handles HTTP requests related to customer
 * operations. It includes methods for creating, fetching, and deleting customers from the system.
 */
@RestController
public class CustomerController {

  private final CustomerService customerService;

  /**
   * Constructor to initialize CustomerService.
   *
   * @param customerService The service that handles customer-related operations.
   */
  @Autowired
  public CustomerController(CustomerService customerService) {
    this.customerService = customerService;
  }

  /**
   * Retrieves a customer by their unique customer ID.
   *
   * @param custId The unique ID of the customer to retrieve.
   * @return A ResponseEntity containing the customer details if found, or 404 if not found.
   */
  @GetMapping("/customer/id/{custId}")
  public ResponseEntity<Customer> getCustomerByCustId(@PathVariable int custId) {
    try {
      // Attempt to find the customer by their customer ID
      Optional<Customer> customer = customerService.getCustomerByCustId(custId);

      // Return the customer if found, else return 404 not found
      return customer.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    } catch (IllegalArgumentException e) {
      // Return 400 Bad Request if there is an issue with the input or validation
      return ResponseEntity.badRequest().body(null);
    } catch (Exception e) {
      // Return 500 Internal Server Error for unexpected issues
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  /**
   * Deletes a customer from the system using their unique customer ID.
   *
   * @param custId The unique ID of the customer to delete.
   * @return A ResponseEntity containing a success or error message based on the deletion result.
   */
  @DeleteMapping("/customer/{custId}")
  public ResponseEntity<String> deleteCustomer(@PathVariable int custId) {
    try {
      // Call the service layer to delete the customer by their ID
      customerService.deleteCustomer(custId);
      return ResponseEntity.ok("Customer with ID " + custId + " deleted successfully.");
    } catch (IllegalArgumentException e) {
      // Handle invalid customer ID or other exceptions
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      // Handle unexpected errors
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An unexpected error occurred: " + e.getMessage());
    }
  }

  /**
   * Creates a new customer in the system.
   *
   * @param customer The customer object to be created.
   * @return A ResponseEntity with a status message indicating success or failure.
   */
  @PostMapping("/customer/create")
  public ResponseEntity<String> createCustomer(@RequestBody Customer customer) {
    try {
      // Call the service to create the customer
      customerService.createCustomer(customer);
      return ResponseEntity.status(HttpStatus.CREATED).body("Customer created successfully.");
    } catch (IllegalArgumentException e) {
      // Handle invalid customer data or any issues during the creation process
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      // Handle any unexpected errors
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An unexpected error occurred: " + e.getMessage());
    }
  }
}
