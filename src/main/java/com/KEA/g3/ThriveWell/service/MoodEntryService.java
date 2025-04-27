package com.KEA.g3.ThriveWell.service;

import com.KEA.g3.ThriveWell.entity.MoodEntry;
import com.KEA.g3.ThriveWell.entity.UserEntity;
import com.KEA.g3.ThriveWell.repository.MoodEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MoodEntryService {

    @Autowired
    private MoodEntryRepository moodEntryRepository;

    public MoodEntry saveMoodEntry(MoodEntry moodEntry, UserEntity user) {
        moodEntry.setUser(user);
        return moodEntryRepository.save(moodEntry);
    }

    public MoodEntry saveMoodEntry(MoodEntry moodEntry) {
        return moodEntryRepository.save(moodEntry);
    }

    public List<MoodEntry> getMoodEntriesByUser(UserEntity user) {
        return moodEntryRepository.findByUser(user);
    }

    public List<MoodEntry> getMoodEntriesByUserAndDateRange(UserEntity user, LocalDateTime start, LocalDateTime end) {
        return moodEntryRepository.findByUserAndTimestampBetweenOrderByTimestampAsc(user, start, end);
    }

    public Optional<MoodEntry> getMoodEntryById(Long id) {
        return moodEntryRepository.findById(id);
    }

    public void deleteMoodEntry(Long id) {
        moodEntryRepository.deleteById(id); // Delete mood entry by id
    }
}