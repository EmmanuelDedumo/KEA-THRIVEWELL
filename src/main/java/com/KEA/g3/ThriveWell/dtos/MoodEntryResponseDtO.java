package com.KEA.g3.ThriveWell.dtos;

public class MoodEntryResponseDtO {
    private Long id;
    private String moodType;
    private String notes;
    private String timestamp;
    private ActivityResponseDTO activity;

    // Getters and setters

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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public ActivityResponseDTO getActivity() {
        return activity;
    }

    public void setActivity(ActivityResponseDTO activity) {
        this.activity = activity;
    }

    // Inner ActivityResponseDTO class
    public static class ActivityResponseDTO {
        private Long id;
        private String name;

        // Getters and setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
