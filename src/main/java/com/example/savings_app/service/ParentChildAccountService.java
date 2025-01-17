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

  public Account createAccountWithCustomer(Account account) {
    // Create the account first
    Account savedAccount = accountService.createAccount(account);

    // If the account has a child ID, create the customer relationship
    if (savedAccount.getChildId() != null) {
      customerService.createCustomer(
          Customer.builder()
              .parentId(savedAccount.getUserId())
              .childId(savedAccount.getChildId())
              .build());
    }

    return savedAccount;
  }
}
