package com.example.savings_app.controller;

import com.example.savings_app.exception.MilestoneException;
import com.example.savings_app.model.Milestone;
import com.example.savings_app.service.MilestoneService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MilestoneController.class)
public class MilestoneControllerTest {

    @MockBean
    private MilestoneService milestoneService;

    @Autowired
    private MockMvc mockMvc;

    private Milestone milestone;
    private Date startDate;
    private Date completionDate;

    @BeforeEach
    public void setUp() throws ParseException {
        // Initialize test data
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        startDate = dateFormat.parse("2024-11-01");
        completionDate = dateFormat.parse("2024-11-01");
        milestone = Milestone.builder()
                .milestoneId(1)
                .milestoneName("Milestone")
                .targetAmount(BigDecimal.valueOf(200.00))
                .savedAmount(BigDecimal.valueOf(50.00))
                .startDate(startDate)
                .completionDate(completionDate)
                .status(Milestone.Status.ACTIVE)
                .build();

    }

    @Test
    public void testGetMilestoneByMilestoneId_Success() throws Exception {
        // Mock the service method
        when(milestoneService.getMilestoneByMilestoneId(1)).thenReturn(Optional.of(milestone));

        mockMvc.perform(get("/milestone/1"))
                .andExpect(status().isOk())  // Expecting HTTP status 200
                .andExpect(jsonPath("$.milestoneId").value(1))  // Assert milestoneId value
                .andExpect(jsonPath("$.milestoneName").value("Milestone"))  // Assert milestoneName value
                .andExpect(jsonPath("$.targetAmount").value(200.00))  // Assert targetAmount value
                .andExpect(jsonPath("$.savedAmount").value(50.00))  // Assert savedAmount value
                .andExpect(jsonPath("$.status").value("ACTIVE"));  // Assert status value (if this field is serialized)

        // Verify the service method was called once with the correct parameter
        verify(milestoneService, times(1)).getMilestoneByMilestoneId(1);
    }

    @Test
    public void testGetMilestoneByMilestoneId_NotFound() throws Exception {
        // Mock the service method
        when(milestoneService.getMilestoneByMilestoneId(2)).thenReturn(Optional.empty());

        mockMvc.perform(get("/milestone/2"))
                .andExpect(status().isNotFound());

        verify(milestoneService, times(1)).getMilestoneByMilestoneId(2);
    }

    @Test
    public void testGetMilestoneByMilestoneId_BadRequest() throws Exception {
        // Simulate the case when the service throws an IllegalArgumentException
        when(milestoneService.getMilestoneByMilestoneId(-1)).thenThrow(new IllegalArgumentException("Invalid Milestone milestoneId: -1"));

        mockMvc.perform(get("/milestone/-1"))
                .andExpect(status().isBadRequest());

        verify(milestoneService, times(1)).getMilestoneByMilestoneId(-1);
    }

    @Test
    public void testGetMilestoneByMilestoneId_InternalServerError() throws Exception {
        // Simulate the case when the service throws a generic exception
        when(milestoneService.getMilestoneByMilestoneId(1)).thenThrow(new RuntimeException("Failed to retrieve Milestone"));

        mockMvc.perform(get("/milestone/1"))
                .andExpect(status().isInternalServerError());

        verify(milestoneService, times(1)).getMilestoneByMilestoneId(1);
    }

    @Test
    public void testFindByName_Success() throws Exception {
        // Mock the service method
        when(milestoneService.getMilestoneByName("Milestone")).thenReturn(Optional.of(milestone));

        mockMvc.perform(get("/milestone/name/Milestone"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.milestoneId").value(1))
                .andExpect(jsonPath("$.milestoneName").value("Milestone"));

        verify(milestoneService, times(1)).getMilestoneByName("Milestone");
    }

    @Test
    public void testFindByName_NotFound() throws Exception {
        // Mock the service method
        when(milestoneService.getMilestoneByName("Nonexistent Milestone")).thenReturn(Optional.empty());

        mockMvc.perform(get("/milestone/name/Nonexistent Milestone"))
                .andExpect(status().isNotFound());

        verify(milestoneService, times(1)).getMilestoneByName("Nonexistent Milestone");
    }

    @Test
    public void testFindByStartDate_Success() throws Exception {
        // Mock the service method
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = dateFormat.parse("2024-11-01");

        when(milestoneService.getMilestoneByStartDate(startDate)).thenReturn(Arrays.asList(milestone));

        String startDateString = dateFormat.format(startDate);

        mockMvc.perform(get("/milestone/startDate/" + startDateString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].milestoneId").value(1))
                .andExpect(jsonPath("$[0].milestoneName").value("Milestone"));

        verify(milestoneService, times(1)).getMilestoneByStartDate(startDate);
    }

    @Test
    public void testFindByCompletionDate_Success() throws Exception {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date completionDate = dateFormat.parse("2024-11-01");

        // Mock the service method
        when(milestoneService.getMilestoneByCompletionDate(completionDate)).thenReturn(Arrays.asList(milestone));

        String completionDateString = dateFormat.format(completionDate);

        mockMvc.perform(get("/milestone/completionDate/" + completionDateString))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].milestoneId").value(1))
                .andExpect(jsonPath("$[0].milestoneName").value("Milestone"));

        verify(milestoneService, times(1)).getMilestoneByCompletionDate(completionDate);
    }

    @Test
    public void testFindByStatus_Success() throws Exception {
        // Mock the service method
        when(milestoneService.getMilestoneByStatus(Milestone.Status.ACTIVE)).thenReturn(Arrays.asList(milestone));

        mockMvc.perform(get("/milestone/status/ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].milestoneId").value(1))
                .andExpect(jsonPath("$[0].milestoneName").value("Milestone"));

        verify(milestoneService, times(1)).getMilestoneByStatus(Milestone.Status.ACTIVE);
    }

    @Test
    public void testFindByName_BadRequest() throws Exception {
        // Simulate the case when the service throws an IllegalArgumentException
        when(milestoneService.getMilestoneByName("Test Milestone")).thenThrow(new IllegalArgumentException("Invalid milestone name"));

        mockMvc.perform(get("/milestone/name/Test Milestone"))
                .andExpect(status().isBadRequest());

        verify(milestoneService, times(1)).getMilestoneByName("Test Milestone");
    }

    @Test
    public void testFindByStartDate_BadRequest() throws Exception {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = dateFormat.parse("2024-11-01");

        // Simulate the case when the service throws an IllegalArgumentException
        when(milestoneService.getMilestoneByStartDate(startDate)).thenThrow(new IllegalArgumentException("Invalid start date"));

        String startDateString = dateFormat.format(startDate);

        mockMvc.perform(get("/milestone/startDate/" + startDateString))
                .andExpect(status().isBadRequest());

        verify(milestoneService, times(1)).getMilestoneByStartDate(startDate);
    }

    @Test
    public void testFindByCompletionDate_BadRequest() throws Exception {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date completionDate = dateFormat.parse("2024-11-01");

        // Simulate the case when the service throws an IllegalArgumentException
        when(milestoneService.getMilestoneByCompletionDate(completionDate)).thenThrow(new IllegalArgumentException("Invalid completion date"));

        String completionDateString = dateFormat.format(completionDate);

        mockMvc.perform(get("/milestone/completionDate/" + completionDateString))
                .andExpect(status().isBadRequest());

        verify(milestoneService, times(1)).getMilestoneByCompletionDate(completionDate);
    }

    @Test
    public void testFindByStatus_BadRequest() throws Exception {
        // Simulate the case when the service throws an IllegalArgumentException
        when(milestoneService.getMilestoneByStatus(Milestone.Status.ACTIVE)).thenThrow(new IllegalArgumentException("Invalid status"));

        mockMvc.perform(get("/milestone/status/ACTIVE"))
                .andExpect(status().isBadRequest());

        verify(milestoneService, times(1)).getMilestoneByStatus(Milestone.Status.ACTIVE);
    }

    @Test
    public void testDeleteMilestone_Success() throws Exception {
        int milestoneId = 1;

        // Mock the service method
        doNothing().when(milestoneService).deleteMilestone(milestoneId);

        mockMvc.perform(delete("/milestone/" + milestoneId))
                .andExpect(status().isOk())
                .andExpect(content().string("Milestone with ID " + milestoneId + " deleted successfully."));

        verify(milestoneService, times(1)).deleteMilestone(milestoneId);
    }

    @Test
    public void testDeleteMilestone_BadRequest() throws Exception {
        int milestoneId = 1;
        String errorMessage = "Milestone not found";

        // Mock the service method to throw IllegalArgumentException
        doThrow(new IllegalArgumentException(errorMessage)).when(milestoneService).deleteMilestone(milestoneId);

        mockMvc.perform(delete("/milestone/" + milestoneId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorMessage));

        verify(milestoneService, times(1)).deleteMilestone(milestoneId);
    }

    @Test
    public void testCreateMilestone_Success() throws Exception {
        // Prepare the test milestone
        String milestoneJson = "{" +
                "\"milestoneId\": 1, \"milestoneName\": \"Milestone\", \"targetAmount\": 200.00, \"savedAmount\": 50.00, \"startDate\": \"2024-11-01\", \"completionDate\": \"2024-11-01\", \"status\": \"ACTIVE\"}";

        when(milestoneService.createMilestone(any(Milestone.class))).thenReturn(milestone);

        mockMvc.perform(post("/milestone/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(milestoneJson))
                .andExpect(status().isCreated())
                .andExpect(content().string("Milestone created successfully."));

        verify(milestoneService, times(1)).createMilestone(any(Milestone.class));
    }

    @Test
    public void testCreateMilestone_BadRequest() throws Exception {
        // Prepare the test milestone JSON
        String milestoneJson = "{" +
                "\"milestoneId\": 1, \"milestoneName\": \"Milestone\", \"targetAmount\": 200.00, \"savedAmount\": 50.00, \"startDate\": \"2024-11-01\", \"completionDate\": \"2024-11-01\", \"status\": \"ACTIVE\"}";

        String errorMessage = "Invalid milestone data";

        // Mock the service method to throw IllegalArgumentException
        doThrow(new IllegalArgumentException(errorMessage)).when(milestoneService).createMilestone(any(Milestone.class));

        mockMvc.perform(post("/milestone/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(milestoneJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorMessage));

        verify(milestoneService, times(1)).createMilestone(any(Milestone.class));
    }

    @Test
    public void testMarkMilestoneAsCompleted_Success() throws Exception {
        int milestoneId = 1;
        Milestone updatedMilestone = new Milestone();

        // Mock the service method
        when(milestoneService.markMilestoneAsCompleted(milestoneId)).thenReturn(updatedMilestone);

        mockMvc.perform(patch("/milestone/" + milestoneId + "/complete"))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));

        verify(milestoneService, times(1)).markMilestoneAsCompleted(milestoneId);
    }

    @Test
    public void testMarkMilestoneAsCompleted_NotFound() throws Exception {
        int milestoneId = -1;

        // Mock the service method to throw IllegalArgumentException
        doThrow(new IllegalArgumentException()).when(milestoneService).markMilestoneAsCompleted(milestoneId);

        mockMvc.perform(patch("/milestone/" + milestoneId + "/complete"))
                .andExpect(status().isNotFound());

        verify(milestoneService, times(1)).markMilestoneAsCompleted(milestoneId);
    }

    @Test
    public void testMarkMilestoneAsCompleted_Conflict() throws Exception {
        int milestoneId = 1;

        // Mock the service method to throw IllegalStateException
        doThrow(new IllegalStateException()).when(milestoneService).markMilestoneAsCompleted(milestoneId);

        mockMvc.perform(patch("/milestone/" + milestoneId + "/complete"))
                .andExpect(status().isConflict());

        verify(milestoneService, times(1)).markMilestoneAsCompleted(milestoneId);
    }

    @Test
    public void testUpdateSavedAmount_Success() throws Exception {
        int milestoneId = 1;
        BigDecimal addedAmount = BigDecimal.valueOf(50.00);
        Milestone updatedMilestone = new Milestone();

        // Mock the service method
        when(milestoneService.updateSavedAmountAndCheckCompletion(milestoneId, addedAmount)).thenReturn(updatedMilestone);

        mockMvc.perform(patch("/milestone/" + milestoneId + "/updateSavedAmount")
                        .param("addedAmount", addedAmount.toString()))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));

        verify(milestoneService, times(1)).updateSavedAmountAndCheckCompletion(milestoneId, addedAmount);
    }

    @Test
    public void testUpdateSavedAmount_NotFound() throws Exception {
        int milestoneId = 1;
        BigDecimal addedAmount = BigDecimal.valueOf(50.00);

        // Mock the service method to throw MilestoneNotFoundException
        doThrow(new MilestoneException.MilestoneNotFoundException("Milestone not found for id: " + milestoneId)).when(milestoneService).updateSavedAmountAndCheckCompletion(milestoneId, addedAmount);

        mockMvc.perform(patch("/milestone/" + milestoneId + "/updateSavedAmount")
                        .param("addedAmount", addedAmount.toString()))
                .andExpect(status().isNotFound());

        verify(milestoneService, times(1)).updateSavedAmountAndCheckCompletion(milestoneId, addedAmount);
    }

    @Test
    public void testUpdateSavedAmount_BadRequest() throws Exception {
        int milestoneId = 1;
        BigDecimal addedAmount = BigDecimal.valueOf(-50.00);

        // Mock the service method to throw InvalidAmountException
        doThrow(new MilestoneException.InvalidAmountException("The added amount must be greater than zero."))
                .when(milestoneService).updateSavedAmountAndCheckCompletion(milestoneId, addedAmount);

        mockMvc.perform(patch("/milestone/" + milestoneId + "/updateSavedAmount")
                        .param("addedAmount", addedAmount.toString()))
                .andExpect(status().isBadRequest());

        verify(milestoneService, times(1)).updateSavedAmountAndCheckCompletion(milestoneId, addedAmount);
    }

}
