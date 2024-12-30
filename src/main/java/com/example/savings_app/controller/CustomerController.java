package com.example.savings_app.controller;

import com.example.savings_app.model.Customer;
import com.example.savings_app.service.CustomerService;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CustomerController {

  private final CustomerService customerService;

  @Autowired
  public CustomerController(CustomerService customerService) {
    this.customerService = customerService;
  }

  @GetMapping("/customer/{custId}")
  public ResponseEntity<Customer> getCustomerByCustId(@PathVariable int custId) {
    try {
      Optional<Customer> customer = customerService.getCustomerByCustId(custId);
      return customer.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(null);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  @DeleteMapping("/customer/{userId}")
  public ResponseEntity<String> deleteCustomer(@PathVariable int custId) {
    try {
      customerService.deleteCustomer(custId);
      return ResponseEntity.ok("Customer with ID " + custId + " deleted successfully.");
    } catch (IllegalArgumentException e) {
      // Handle invalid userId or other exceptions from the service layer
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      // Handle unexpected errors
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An unexpected error occurred: " + e.getMessage());
    }
  }

  @PostMapping("/customer/create")
  public ResponseEntity<String> createCustomer(@RequestBody Customer customer) {
    try {
      // Calling the service layer to create the customer
      customerService.createCustomer(customer);
      return ResponseEntity.status(HttpStatus.CREATED).body("Customer created successfully.");
    } catch (IllegalArgumentException e) {
      // Handle invalid customer data or relationship
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      // Handle unexpected errors
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An unexpected error occurred: " + e.getMessage());
    }
  }
}
