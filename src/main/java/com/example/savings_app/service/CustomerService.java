package com.example.savings_app.service;

import com.example.savings_app.model.Customer;
import com.example.savings_app.repository.CustomerRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

  private final CustomerRepository customerRepository;

  @Autowired
  public CustomerService(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }

  // Method to create a customer
  public Customer createCustomer(Customer customer) {
    // Validate parent and child accounts

    System.out.println(customer.toString());

    if (customer.getParentId() == null || customer.getChildId() == null) {
      throw new IllegalArgumentException("Both parent and child accounts must be provided.");
    }

    try {
      // Save the account to the repository
      return customerRepository.save(customer);
    } catch (org.springframework.dao.DataIntegrityViolationException e) {
      throw new IllegalStateException("Failed to create customer", e);
    }
  }

  // Method to get a customer by custId
  public Optional<Customer> getCustomerByCustId(int custId) {
    if (custId <= 0) {
      throw new IllegalArgumentException("Invalid customer custId: " + custId);
    }

    try {
      return customerRepository.findById(custId);
    } catch (Exception e) {
      throw new RuntimeException("Failed to retrieve customer with custId: " + custId, e);
    }
  }

  // Method to delete a customer
  public void deleteCustomer(int custId) {
    customerRepository.deleteById(custId);
  }
}
