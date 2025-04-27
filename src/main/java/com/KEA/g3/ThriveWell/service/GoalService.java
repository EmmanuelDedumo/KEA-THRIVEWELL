package com.KEA.g3.ThriveWell.service;

import com.KEA.g3.ThriveWell.entity.Goal;
import com.KEA.g3.ThriveWell.repository.GoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GoalService {

    @Autowired
    private GoalRepository goalRepository;

    // Get all goals
    public List<Goal> getAllGoals() {
        return goalRepository.findAll();
    }

    // Get goal by id
    public Goal getGoalById(Long id) {
        Optional<Goal> goal = goalRepository.findById(id);
        return goal.orElse(null); // Return null if not found
    }

    // Save new goal
    public Goal saveGoal(Goal goal) {
        return goalRepository.save(goal);
    }

    // Update an existing goal
    public Goal updateGoal(Long id, Goal goal) {
        if (goalRepository.existsById(id)) {
            goal.setId(id); // Set the id to ensure the existing goal is updated
            return goalRepository.save(goal);
        }
        return null;
    }


    // Delete a goal by id
    public void deleteGoal(Long id) {
        goalRepository.deleteById(id);
    }
}
