package com.KEA.g3.ThriveWell.dtos;

import java.util.List;
import java.util.Map;

public class MoodStatsDTO {
    private Map<String, Integer> moodCounts;
    private List<MoodTimelineEntry> moodTimeline;

    // Nested class for timeline entries
    public static class MoodTimelineEntry {
        private String date;
        private String mood;

        public MoodTimelineEntry(String date, String mood) {
            this.date = date;
            this.mood = mood;
        }

        // Getters and setters
        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getMood() {
            return mood;
        }

        public void setMood(String mood) {
            this.mood = mood;
        }
    }

    // Getters and setters
    public Map<String, Integer> getMoodCounts() {
        return moodCounts;
    }

    public void setMoodCounts(Map<String, Integer> moodCounts) {
        this.moodCounts = moodCounts;
    }

    public List<MoodTimelineEntry> getMoodTimeline() {
        return moodTimeline;
    }

    public void setMoodTimeline(List<MoodTimelineEntry> moodTimeline) {
        this.moodTimeline = moodTimeline;
    }
}