package com.example.savings_app.controller;

import com.example.savings_app.model.Milestone;
import com.example.savings_app.service.MilestoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class MilestoneController {

    private final MilestoneService milestoneService;

    @Autowired
    public MilestoneController(MilestoneService milestoneService) {
        this.milestoneService = milestoneService;
    }

    @GetMapping("/milestone/{milestoneId}")
    public ResponseEntity<Milestone> getMilestoneByMilestoneId(@PathVariable int milestoneId) {
        try {
            Optional<Milestone> milestone = milestoneService.getMilestoneByMilestoneId(milestoneId);
            return milestone.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/milestone/name/{name}")
    public ResponseEntity<Milestone> findByName(@PathVariable String name) {
        try {
            Optional<Milestone> milestone = milestoneService.findByName(name);
            return milestone.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Get Milestones by Start Date
    @GetMapping("/milestone/startDate/{startDate}")
    public ResponseEntity<List<Milestone>> findByStartDate(@PathVariable String startDate) throws ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date parsedDate = dateFormat.parse(String.valueOf(startDate));

        try {
            List<Milestone> milestones = milestoneService.findByStartDate(parsedDate);
            if (milestones.isEmpty()) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(milestones);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Get Milestones by Completion Date
    @GetMapping("/milestone/completionDate/{completionDate}")
    public ResponseEntity<List<Milestone>> findByCompletionDate(@PathVariable String completionDate) throws ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date parsedDate = dateFormat.parse(String.valueOf(completionDate));

        try {
            List<Milestone> milestones = milestoneService.findByCompletionDate(parsedDate);
            if (milestones.isEmpty()) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(milestones);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Get Milestones by Status
    @GetMapping("/milestone/status/{status}")
    public ResponseEntity<List<Milestone>> findByStatus(@PathVariable String status) {
        try {
            // Assuming that the status parameter will be passed as a String that matches the enum
            Milestone.Status milestoneStatus = Milestone.Status.valueOf(status.toUpperCase());
            List<Milestone> milestones = milestoneService.findByStatus(milestoneStatus);
            if (milestones.isEmpty()) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(milestones);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
