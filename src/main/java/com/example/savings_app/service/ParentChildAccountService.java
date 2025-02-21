package com.example.savings_app.service;

import com.example.savings_app.model.Account;
import com.example.savings_app.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParentChildAccountService {

  private final CustomerService customerService;

  @Autowired
  public ParentChildAccountService(CustomerService customerService) {
    this.customerService = customerService;
  }

  public int createAccountWithCustomer(Account account) {

    Customer customer =
        Customer.builder().parentId(account.getUserId()).childId(account.getChildId()).build();

    // If the account has a child ID, create the customer relationship
    if (account.getChildId() != null) {
      // Ensure the Customer object has a valid custId after saving
      customer = customerService.createCustomer(customer);
      // Get the custId after the customer is created
      Integer custId = customer.getCustId(); // This will be set by the save method
      if (custId != null) {
        return custId;
      } else {
        throw new IllegalStateException("Failed to create customer, custId is null");
      }
    } else {
      throw new IllegalStateException("Failed to create customer");
    }
  }
}
