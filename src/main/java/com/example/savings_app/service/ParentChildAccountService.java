package com.example.savings_app.service;

import com.example.savings_app.model.Account;
import com.example.savings_app.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for handling the creation of parent-child account relationships. It
 * utilizes the CustomerService to manage customer creation and associates customers with accounts
 * by linking a parent ID and a child ID.
 */
@Service
public class ParentChildAccountService {

  // Dependency injection of the CustomerService to handle customer-related operations
  private final CustomerService customerService;

  /**
   * Constructor for injecting the CustomerService dependency.
   *
   * @param customerService the service used for managing customers
   */
  @Autowired
  public ParentChildAccountService(CustomerService customerService) {
    this.customerService = customerService;
  }

  /**
   * Creates an account for a customer, establishing a parent-child relationship based on the
   * provided account information. If the account has a child ID, the method creates a customer
   * object and links the customer with the account.
   *
   * @param account the account to be created, containing user ID and child ID
   * @return the customer ID (custId) after successful customer creation
   * @throws IllegalStateException if the child ID is null or if the customer creation fails
   */
  public int createAccountWithCustomer(Account account) {

    // Create a new Customer object using the parent ID (user ID) and child ID from the account
    Customer customer =
        Customer.builder().parentId(account.getUserId()).childId(account.getChildId()).build();

    // Check if the account has a child ID, which is necessary for creating a customer relationship
    if (account.getChildId() != null) {
      // Create the customer using the customerService and ensure the customer is saved
      customer = customerService.createCustomer(customer);

      // Retrieve the customer ID (custId) after the customer is successfully created
      Integer custId = customer.getCustId(); // This will be set by the save method

      // If the customer ID is valid, return it
      if (custId != null) {
        return custId;
      } else {
        // If customer ID is null, throw an exception indicating failure to create the customer
        throw new IllegalStateException("Failed to create customer, custId is null");
      }
    } else {
      // If the account does not have a valid child ID, throw an exception
      throw new IllegalStateException("Failed to create customer: child ID is null");
    }
  }
}
