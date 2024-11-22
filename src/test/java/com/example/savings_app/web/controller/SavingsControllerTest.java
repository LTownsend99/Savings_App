package com.example.savings_app.web.controller;

import com.example.savings_app.controller.SavingsController;
import com.example.savings_app.model.Milestone;
import com.example.savings_app.model.Savings;
import com.example.savings_app.service.SavingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SavingsController.class)
public class SavingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SavingsService savingsService;

    private Savings savings;
    private Date date;

    @BeforeEach
    public void setUp() {
        date = new Date();
        savings = Savings.builder()
                .savingsId(1)
                .amount(BigDecimal.valueOf(100.00))
                .date(date)
                .milestone(Milestone.builder().milestoneId(1).build())
                .build();
    }

    @Test
    public void testGetSavingsById_Success() throws Exception {
        when(savingsService.findById(1)).thenReturn(Optional.of(savings));

        mockMvc.perform(get("/savings/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.savingsId").value(1))
                .andExpect(jsonPath("$.amount").value(100.00));

        verify(savingsService, times(1)).findById(1);
    }

    @Test
    public void testGetSavingsById_NotFound() throws Exception {
        when(savingsService.findById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/savings/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(savingsService, times(1)).findById(1);
    }

    @Test
    public void testGetSavingsById_BadRequest() throws Exception {
        when(savingsService.findById(-1)).thenThrow(new IllegalArgumentException("Invalid ID"));

        mockMvc.perform(get("/savings/-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(savingsService, times(1)).findById(-1);
    }

    @Test
    public void testGetSavingsByDate_Success() throws Exception {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date FormatDate = dateFormat.parse("2024-11-01");

        when(savingsService.findByDate(FormatDate)).thenReturn(Arrays.asList(savings));

        String dateString = dateFormat.format(FormatDate);

        mockMvc.perform(get("/savings/date/" + dateString) // Pass date as part of the path
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].savingsId").value(1))
                .andExpect(jsonPath("$[0].amount").value(100.00));

        verify(savingsService, times(1)).findByDate(FormatDate);
    }

    @Test
    public void testGetSavingsByDate_NotFound() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = dateFormat.parse("2024-11-01");

        // Mock the service to return an empty list
        when(savingsService.findByDate(startDate)).thenReturn(Arrays.asList());

        String dateString = dateFormat.format(startDate);

        // Perform the mock request using the correct path variable
        mockMvc.perform(get("/savings/date/" + dateString)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Assert that the status is 404 Not Found

        // Verify that the service method was called with the correct parameter
        verify(savingsService, times(1)).findByDate(startDate);
    }


    @Test
    public void testGetSavingsByMilestoneId_Success() throws Exception {
        when(savingsService.findByMilestoneId(1)).thenReturn(Optional.of(savings));

        mockMvc.perform(get("/savings/milestone/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.savingsId").value(1))
                .andExpect(jsonPath("$.amount").value(100.00));

        verify(savingsService, times(1)).findByMilestoneId(1);
    }

    @Test
    public void testGetSavingsByMilestoneId_NotFound() throws Exception {
        when(savingsService.findByMilestoneId(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/savings/milestone/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(savingsService, times(1)).findByMilestoneId(1);
    }

    @Test
    public void testGetSavingsByMilestoneId_BadRequest() throws Exception {
        when(savingsService.findByMilestoneId(-1)).thenThrow(new IllegalArgumentException("Invalid Milestone ID"));

        mockMvc.perform(get("/savings/milestone/-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(savingsService, times(1)).findByMilestoneId(-1);
    }
}
