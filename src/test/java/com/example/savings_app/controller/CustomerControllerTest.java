package com.example.savings_app.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.savings_app.model.Customer;
import com.example.savings_app.service.CustomerService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private CustomerService customerService;

  private Customer customer;

  @BeforeEach
  void setUp() {
    customer = Customer.builder().custId(1).parentId(1).childId(2).build();
  }

  @Test
  void getCustomerByCustId_ShouldReturnCustomer_WhenCustomerExists() throws Exception {

    when(customerService.getCustomerByCustId(1)).thenReturn(Optional.of(customer));

    mockMvc
        .perform(get("/customer/id/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.custId").value(1))
        .andExpect(jsonPath("$.parentId").value(1))
        .andExpect(jsonPath("$.childId").value(2));

    verify(customerService, times(1)).getCustomerByCustId(1);
  }

  @Test
  void getCustomerByCustId_ShouldReturn404_WhenCustomerDoesNotExist() throws Exception {
    when(customerService.getCustomerByCustId(2)).thenReturn(Optional.empty());

    mockMvc
        .perform(get("/customer/id/2").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());

    verify(customerService, times(1)).getCustomerByCustId(2);
  }

  @Test
  void getCustomerByCustId_ShouldReturn400_WhenCustIdIsInvalid() throws Exception {
    when(customerService.getCustomerByCustId(-1))
        .thenThrow(new IllegalArgumentException("Invalid customer custId: -1"));

    mockMvc
        .perform(get("/customer/id/-1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    verify(customerService, times(1)).getCustomerByCustId(-1);
  }

  @Test
  void getCustomerByCustId_ShouldReturn500_WhenUnexpectedErrorOccurs() throws Exception {
    when(customerService.getCustomerByCustId(3))
        .thenThrow(new RuntimeException("Unexpected error"));

    mockMvc
        .perform(get("/customer/id/3").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError());

    verify(customerService, times(1)).getCustomerByCustId(3);
  }
}
