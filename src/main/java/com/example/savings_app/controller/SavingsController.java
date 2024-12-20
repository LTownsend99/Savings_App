package com.example.savings_app.controller;

import com.example.savings_app.model.Savings;
import com.example.savings_app.service.SavingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class SavingsController {

    private final SavingsService savingsService;

    @Autowired
    public SavingsController(SavingsService savingsService) {
        this.savingsService = savingsService;
    }

    @GetMapping("/savings/{savingsId}")
    public ResponseEntity<Savings> getSavingsById(@PathVariable int savingsId) {
        try {
            Optional<Savings> savings = savingsService.getSavingsById(savingsId);
            return savings.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/savings/date/{date}")
    public ResponseEntity<List<Savings>> getSavingsByDate(@PathVariable String date) throws ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date parsedDate = dateFormat.parse(String.valueOf(date));

        try {
            List<Savings> savings = savingsService.getSavingsByDate(parsedDate);
            if (savings.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(savings);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/savings/milestone/{milestoneId}")
    public ResponseEntity<Savings> getSavingsByMilestoneId(@PathVariable int milestoneId) {
        try {
            Optional<Savings> savings = savingsService.getSavingsByMilestoneId(milestoneId);
            return savings.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @DeleteMapping("/savings/{savingsId}")
    public ResponseEntity<Void> deleteSavings(@PathVariable int savingsId) {
        try {
            savingsService.deleteSavings(savingsId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
