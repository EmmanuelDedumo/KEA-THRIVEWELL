package com.KEA.g3.ThriveWell.controller;

import com.KEA.g3.ThriveWell.entity.Goal;
import com.KEA.g3.ThriveWell.entity.UserEntity;
import com.KEA.g3.ThriveWell.repository.GoalRepository;
import com.KEA.g3.ThriveWell.service.GoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;


import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/goal")
public class GoalController {

    @Autowired
    private GoalService goalService;
    private final GoalRepository goalRepository;

    public GoalController(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    // Get all goals
    @GetMapping
    public ResponseEntity<List<Goal>> getGoalsForUser(Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        List<Goal> goals = goalRepository.findByUserId(user.getId());
        return ResponseEntity.ok(goals);
    }


    // Get a specific goal by id
    @GetMapping("/{id}")
    public ResponseEntity<Goal> getGoalById(@PathVariable Long id) {
        Goal goal = goalService.getGoalById(id);
        return ResponseEntity.ok(goal);
    }

    // Create a new goal
    @PostMapping
    public ResponseEntity<Goal> createGoal(@RequestBody Goal goal) {
        // Get the current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity currentUser = (UserEntity) authentication.getPrincipal();

        // Assign the user to the goal
        goal.setUser(currentUser);

        Goal savedGoal = goalService.saveGoal(goal);
        return ResponseEntity.status(201).body(savedGoal);
    }

    // Update an existing goal
    @PutMapping("/{id}")
    public ResponseEntity<?> updateGoal(
            @PathVariable Long id,
            @RequestBody Goal updatedGoal,
            @AuthenticationPrincipal UserEntity user
    ) {
        Optional<Goal> optionalGoal = goalRepository.findById(id);
        if (optionalGoal.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Goal existingGoal = optionalGoal.get();
        if (!existingGoal.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        existingGoal.setGoalName(updatedGoal.getGoalName());
        existingGoal.setDescription(updatedGoal.getDescription());
        existingGoal.setProgress(updatedGoal.getProgress());
        existingGoal.setPriority(updatedGoal.getPriority());
        existingGoal.setCategory(updatedGoal.getCategory());
        existingGoal.setTargetDate(updatedGoal.getTargetDate());

        goalRepository.save(existingGoal);

        return ResponseEntity.ok().build();
    }


    // Delete a goal by id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id) {
        goalService.deleteGoal(id);
        return ResponseEntity.noContent().build();
    }
}