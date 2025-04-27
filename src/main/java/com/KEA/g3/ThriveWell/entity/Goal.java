package com.KEA.g3.ThriveWell.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String goalName;
    private String description;
    private LocalDate targetDate;
    private String progress; // e.g., "Not Started", "In Progress", "Completed"
    private String category; // e.g., "Personal", "Health", "Career", "Education", "Financial"
    private String priority; // e.g., "Low", "Medium", "High"

    // Add reference to UserEntity
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    // Default constructor
    public Goal() {}

    // Constructor with parameters
    public Goal(String goalName, String description, LocalDate targetDate, String progress,
                String category, String priority, UserEntity user) {
        this.goalName = goalName;
        this.description = description;
        this.targetDate = targetDate;
        this.progress = progress;
        this.category = category;
        this.priority = priority;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}