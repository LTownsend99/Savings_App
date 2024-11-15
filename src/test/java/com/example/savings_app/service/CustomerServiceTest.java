package com.example.savings_app.service;

import com.example.savings_app.model.Customer;
import com.example.savings_app.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomerServiceTest {

    private CustomerRepository customerRepository;
    private CustomerService customerService;
    private final int CUST_ID = 1;

    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerRepository.class);
        customerService = new CustomerService(customerRepository);
    }

    @Test
    void getCustomerByCustId_ShouldReturnCustomer_WhenCustomerExists() {
        Customer customer = Customer.builder()
                .custId(CUST_ID)
                .parentId(1)
                .childId(2)
                .build();

        when(customerRepository.findByCustomerId(CUST_ID)).thenReturn(Optional.of(customer));

        Optional<Customer> result = customerService.getCustomerByCustId(CUST_ID);

        assertTrue(result.isPresent());
        assertEquals(CUST_ID, result.get().getCustId());
        assertEquals(1, result.get().getParentId());
        verify(customerRepository, times(1)).findByCustomerId(CUST_ID);
    }

    @Test
    void getCustomerByCustId_ShouldReturnEmptyOptional_WhenCustomerDoesNotExist() {
        int custId = 2;
        when(customerRepository.findByCustomerId(custId)).thenReturn(Optional.empty());

        Optional<Customer> result = customerService.getCustomerByCustId(custId);

        assertFalse(result.isPresent());
        verify(customerRepository, times(1)).findByCustomerId(custId);
    }

    @Test
    void getCustomerByCustId_ShouldThrowIllegalArgumentException_WhenInvalidCustIdProvided() {
        int custId = -1;
        when(customerRepository.findByCustomerId(custId)).thenThrow(new IllegalArgumentException("Invalid ID"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            customerService.getCustomerByCustId(custId);
        });

        assertTrue(exception.getMessage().contains("Invalid customer custId: -1"));
        verify(customerRepository, times(1)).findByCustomerId(custId);
    }

    @Test
    void getCustomerByCustId_ShouldThrowRuntimeException_WhenUnexpectedErrorOccurs() {
        int custId = 3;
        when(customerRepository.findByCustomerId(custId)).thenThrow(new RuntimeException("Unexpected error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            customerService.getCustomerByCustId(custId);
        });

        assertTrue(exception.getMessage().contains("Failed to retrieve customer with custId: 3"));
        verify(customerRepository, times(1)).findByCustomerId(custId);
    }
}
