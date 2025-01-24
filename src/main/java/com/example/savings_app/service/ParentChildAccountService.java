package com.example.savings_app.service;

import com.example.savings_app.model.Account;
import com.example.savings_app.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParentChildAccountService {

  private final AccountService accountService;
  private final CustomerService customerService;

  @Autowired
  public ParentChildAccountService(AccountService accountService, CustomerService customerService) {
    this.accountService = accountService;
    this.customerService = customerService;
  }

  public int createAccountWithCustomer(Account account) {

    Customer customer =
        Customer.builder().parentId(account.getUserId()).childId(account.getChildId()).build();

    // If the account has a child ID, create the customer relationship
    if (account.getChildId() != null) {
      customerService.createCustomer(customer);
    }

    return customer.getCustId();
  }
}
