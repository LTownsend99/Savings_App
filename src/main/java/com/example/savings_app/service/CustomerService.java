package com.example.savings_app.service;

import com.example.savings_app.model.Customer;
import com.example.savings_app.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Optional<Customer> getCustomerByCustId(int custId) {

        try {
            return customerRepository.findByCustomerId(custId);
        } catch (IllegalArgumentException e) {
            // Handle the case where the provided ID is invalid
            throw new IllegalArgumentException("Invalid customer custId: " + custId, e);
        } catch (Exception e) {
            // Catch any unexpected exceptions
            throw new RuntimeException("Failed to retrieve customer with custId: " + custId, e);
        }
    }
}
