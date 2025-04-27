package com.KEA.g3.ThriveWell.controller;

import com.KEA.g3.ThriveWell.dtos.MoodEntryResponseDtO;
import com.KEA.g3.ThriveWell.dtos.MoodStatsDTO;
import com.KEA.g3.ThriveWell.entity.MoodEntry;
import com.KEA.g3.ThriveWell.entity.UserEntity;
import com.KEA.g3.ThriveWell.service.MoodEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/moodentry")
@CrossOrigin(origins = "http://localhost:3000")
public class MoodEntryController {

    @Autowired
    private MoodEntryService moodEntryService;

    @PostMapping
    public ResponseEntity<MoodEntry> createMoodEntry(@RequestBody MoodEntry moodEntry) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity currentUser = (UserEntity) authentication.getPrincipal();

        moodEntry.setUser(currentUser);
        MoodEntry savedMoodEntry = moodEntryService.saveMoodEntry(moodEntry);

        return ResponseEntity.status(201).body(savedMoodEntry);
    }

    @GetMapping
    public ResponseEntity<List<MoodEntry>> getUserMoodEntries() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity currentUser = (UserEntity) authentication.getPrincipal();

        List<MoodEntry> userMoodEntries = moodEntryService.getMoodEntriesByUser(currentUser);
        return ResponseEntity.ok(userMoodEntries);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MoodEntryResponseDtO> getMoodEntry(@PathVariable Long id) {
        Optional<MoodEntry> optionalMoodEntry = moodEntryService.getMoodEntryById(id);

        if (optionalMoodEntry.isPresent()) {
            MoodEntry moodEntry = optionalMoodEntry.get();

            MoodEntryResponseDtO responseDTO = new MoodEntryResponseDtO();
            responseDTO.setId(moodEntry.getId());
            responseDTO.setMoodType(moodEntry.getMoodType());
            responseDTO.setNotes(moodEntry.getNotes());
            responseDTO.setTimestamp(moodEntry.getTimestamp().toString());

            return ResponseEntity.ok(responseDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MoodEntry> updateMoodEntry(@PathVariable Long id, @RequestBody MoodEntry moodEntry) {
        Optional<MoodEntry> existingMoodEntryOpt = moodEntryService.getMoodEntryById(id);

        if (existingMoodEntryOpt.isPresent()) {
            MoodEntry existingMoodEntry = existingMoodEntryOpt.get();
            existingMoodEntry.setMoodType(moodEntry.getMoodType());
            existingMoodEntry.setNotes(moodEntry.getNotes());
            existingMoodEntry.setTimestamp(moodEntry.getTimestamp());

            MoodEntry updatedMoodEntry = moodEntryService.saveMoodEntry(existingMoodEntry);
            return ResponseEntity.ok(updatedMoodEntry);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMoodEntry(@PathVariable Long id) {
        Optional<MoodEntry> moodEntryOpt = moodEntryService.getMoodEntryById(id);

        if (moodEntryOpt.isPresent()) {
            moodEntryService.deleteMoodEntry(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/stats/{timeRange}")
    public ResponseEntity<MoodStatsDTO> getMoodStats(@PathVariable String timeRange) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity currentUser = (UserEntity) authentication.getPrincipal();

        // Calculate the date range based on the requested time range
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate;

        switch (timeRange) {
            case "week":
                startDate = endDate.minusDays(7);
                break;
            case "month":
                startDate = endDate.minusMonths(1);
                break;
            case "year":
                startDate = endDate.minusYears(1);
                break;
            default:
                startDate = endDate.minusDays(7); // Default to week
        }

        // Get all mood entries for this user
        List<MoodEntry> entries = moodEntryService.getMoodEntriesByUser(currentUser);

        // Filter entries by date range
        List<MoodEntry> filteredEntries = entries.stream()
                .filter(entry -> !entry.getTimestamp().isBefore(startDate) && !entry.getTimestamp().isAfter(endDate))
                .collect(Collectors.toList());

        // Create the stats DTO
        MoodStatsDTO statsDTO = new MoodStatsDTO();

        // Count occurrences of each mood type
        Map<String, Integer> moodCounts = new HashMap<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<MoodStatsDTO.MoodTimelineEntry> timeline = filteredEntries.stream()
                .map(entry -> {
                    // Add to mood counts
                    String moodType = entry.getMoodType();
                    moodCounts.put(moodType, moodCounts.getOrDefault(moodType, 0) + 1);

                    // Create timeline entry
                    String formattedDate = entry.getTimestamp().format(dateFormatter);
                    return new MoodStatsDTO.MoodTimelineEntry(formattedDate, moodType);
                })
                .collect(Collectors.toList());

        statsDTO.setMoodCounts(moodCounts);
        statsDTO.setMoodTimeline(timeline);

        return ResponseEntity.ok(statsDTO);
    }
}
