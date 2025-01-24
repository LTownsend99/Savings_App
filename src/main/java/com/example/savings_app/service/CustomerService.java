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
  public void createCustomer(Customer customer) {
    // Validate parent and child accounts
    if (customer.getParentId() == null || customer.getChildId() == null) {
      throw new IllegalArgumentException("Both parent and child accounts must be provided.");
    }

    // Logic for creating a customer remains the same
    customerRepository.save(customer);
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
