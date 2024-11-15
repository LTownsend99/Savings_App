package com.example.savings_app.web.controller;

import com.example.savings_app.controller.AccountController;
import com.example.savings_app.model.Account;
import com.example.savings_app.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    private Account account;

    @BeforeEach
    void setUp() {
        // Set up a sample account for testing
        account = Account.builder()
                .userId(1)
                .firstName("John")
                .lastName("Smith")
                .email("test@example.com")
                .passwordHash("password")
                .role(Account.Role.PARENT)
                .build();
    }

    @Test
    void testCreateAccount() throws Exception {
        // Mock the service call
        when(accountService.createAccount(any(Account.class))).thenReturn(account);

        // Perform the request and assert the result
        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"first_name\": \"John\", \"lastName\": \"Doe\", \"email\": \"john.doe@example.com\", " +
                                "\"passwordHash\": \"hashed_password\", \"role\": \"PARENT\", \"createdAt\": \"2023-11-01\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.first_name", is("John")))
                .andExpect(jsonPath("$.last_name", is("Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")));
    }

    @Test
    void getAccountByUserId_ShouldReturnAccount_WhenAccountExists() throws Exception {
        when(accountService.getAccountByUserId(1)).thenReturn(Optional.of(account));

        mockMvc.perform(get("/account/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

        verify(accountService, times(1)).getAccountByUserId(1);
    }

    @Test
    void getAccountByUserId_ShouldReturn404_WhenAccountDoesNotExist() throws Exception {
        when(accountService.getAccountByUserId(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/account/99"))
                .andExpect(status().isNotFound());

        verify(accountService, times(1)).getAccountByUserId(99);
    }


}
