package com.KEA.g3.ThriveWell.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class MoodEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String moodType; // e.g., Happy, Sad, Anxious, etc.
    private String notes; // Additional notes or thoughts
    private LocalDateTime timestamp; // When the mood was logged

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user; // Reference to the user who created this mood entry

    public MoodEntry() {
        this.timestamp = LocalDateTime.now(); // Default to current time
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMoodType() {
        return moodType;
    }

    public void setMoodType(String moodType) {
        this.moodType = moodType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}
