package com.example.savings_app.service;

import com.example.savings_app.model.Account;
import com.example.savings_app.model.Customer;
import com.example.savings_app.repository.AccountRepository;
import com.example.savings_app.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private AccountService accountService;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, AccountService accountService) {
        this.customerRepository = customerRepository;
        this.accountService = accountService;
    }

    public void createCustomer(Customer customer) {
        // Validate parent and child accounts
        if (customer.getParentId() == null || customer.getChildId() == null) {
            throw new IllegalArgumentException("Both parent and child accounts must be provided.");
        }

        // Fetch parent and child accounts by their IDs
        Account parentAccount = accountService.getAccountByUserId(customer.getParentId())
                .orElseThrow(() -> new IllegalArgumentException("Parent account not found."));

        Account childAccount =  accountService.getAccountByUserId(customer.getChildId())
                .orElseThrow(() -> new IllegalArgumentException("Child account not found."));

        // Ensure the roles of the accounts are valid
        if (parentAccount.getRole() != Account.Role.PARENT) {
            throw new IllegalArgumentException("The parent account must have the 'PARENT' role.");
        }

        if (childAccount.getRole() != Account.Role.CHILD) {
            throw new IllegalArgumentException("The child account must have the 'CHILD' role.");
        }

        // Create and save the customer relationship
        customerRepository.save(customer);
    }


    public Optional<Customer> getCustomerByCustId(int custId) {

        try {
            return customerRepository.findById(custId);
        } catch (IllegalArgumentException e) {
            // Handle the case where the provided ID is invalid
            throw new IllegalArgumentException("Invalid customer custId: " + custId, e);
        } catch (Exception e) {
            // Catch any unexpected exceptions
            throw new RuntimeException("Failed to retrieve customer with custId: " + custId, e);
        }
    }

    public void deleteCustomer(int custId) {
        try {
            customerRepository.deleteById(custId);
        } catch (IllegalArgumentException e) {
            // Handle the case where the provided ID is invalid
            throw new IllegalArgumentException("Invalid customer userId: " + custId, e);
        }
    }
}
