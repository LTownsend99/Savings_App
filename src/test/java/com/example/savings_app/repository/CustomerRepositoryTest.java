package com.example.savings_app.repository;

import com.example.savings_app.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        // Setup a test customer
        testCustomer = Customer.builder()
                .parentId(1)
                .childId(2)
                .build();
        customerRepository.save(testCustomer);  // Save the customer to the DB for testing
    }

    @Test
    void testFindById_Found() {
        // Test that the customer can be retrieved by ID
        Optional<Customer> retrievedCustomer = customerRepository.findById(testCustomer.getCustId());

        // Assert that the retrieved customer is present and has the correct ID
        assertThat(retrievedCustomer).isPresent();
        assertThat(retrievedCustomer.get().getCustId()).isEqualTo(testCustomer.getCustId());
    }

    @Test
    void testFindById_NotFound() {
        // Test that no customer is found when the ID does not exist
        Optional<Customer> retrievedCustomer = customerRepository.findById(999); // Assuming 999 does not exist

        // Assert that the Optional is empty (i.e., no customer was found)
        assertThat(retrievedCustomer).isNotPresent();
    }

    @Test
    void testDeleteCustomer() {
        // Verify that the customer exists before deleting
        Optional<Customer> customerBeforeDelete = customerRepository.findById(testCustomer.getCustId());
        assertThat(customerBeforeDelete).isPresent();

        // Delete the customer
        customerRepository.deleteById(testCustomer.getCustId());

        // Verify that the customer is deleted by checking if it no longer exists
        Optional<Customer> customerAfterDelete = customerRepository.findById(testCustomer.getCustId());
        assertThat(customerAfterDelete).isNotPresent(); // Customer should be deleted and not found
    }

    @Test
    void testDeleteCustomer_NotFound() {
        // Delete a customer that doesn't exist (ID = 999)
        customerRepository.deleteById(999); // Assuming 999 does not exist

        // No exception should be thrown, and the repository should not throw any error
        // We just verify that no customer exists with ID 999
        Optional<Customer> customerAfterDelete = customerRepository.findById(999);
        assertThat(customerAfterDelete).isNotPresent(); // Customer should not exist
    }

}
