package com.example.savings_app.service;

import com.example.savings_app.model.Customer;
import com.example.savings_app.repository.CustomerRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Service class responsible for managing customer-related business logic. */
@Service
public class CustomerService {

  private final CustomerRepository customerRepository;

  /**
   * Constructor to inject the CustomerRepository dependency into the CustomerService.
   *
   * @param customerRepository Repository used to interact with the customer data in the database.
   */
  @Autowired
  public CustomerService(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }

  /**
   * Creates a new customer after validating that both parent and child accounts are provided. If
   * the validation fails, an IllegalArgumentException is thrown.
   *
   * @param customer The customer to be created.
   * @return The created customer.
   * @throws IllegalArgumentException if either parentId or childId is missing.
   * @throws IllegalStateException if there is an error while saving the customer.
   */
  public Customer createCustomer(Customer customer) {
    // Validate parent and child accounts
    System.out.println(customer.toString());

    if (customer.getParentId() == null || customer.getChildId() == null) {
      throw new IllegalArgumentException("Both parent and child accounts must be provided.");
    }

    try {
      // Save the customer to the repository
      return customerRepository.save(customer);
    } catch (org.springframework.dao.DataIntegrityViolationException e) {
      throw new IllegalStateException("Failed to create customer", e);
    }
  }

  /**
   * Retrieves a customer by their custId.
   *
   * @param custId The customer ID of the customer to be retrieved.
   * @return An Optional containing the customer if found, otherwise an empty Optional.
   * @throws IllegalArgumentException if the provided custId is invalid (<= 0).
   * @throws RuntimeException if there is an error during the retrieval process.
   */
  public Optional<Customer> getCustomerByCustId(int custId) {
    // Validate the customer ID
    if (custId <= 0) {
      throw new IllegalArgumentException("Invalid customer custId: " + custId);
    }

    try {
      // Retrieve the customer by custId from the repository
      return customerRepository.findById(custId);
    } catch (Exception e) {
      throw new RuntimeException("Failed to retrieve customer with custId: " + custId, e);
    }
  }

  /**
   * Deletes a customer by their custId.
   *
   * @param custId The customer ID of the customer to be deleted.
   */
  public void deleteCustomer(int custId) {
    // Delete the customer from the repository by their custId
    customerRepository.deleteById(custId);
  }
}
