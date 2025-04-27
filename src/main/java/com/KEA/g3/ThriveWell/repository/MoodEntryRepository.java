package com.KEA.g3.ThriveWell.repository;

import com.KEA.g3.ThriveWell.entity.MoodEntry;
import com.KEA.g3.ThriveWell.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MoodEntryRepository extends JpaRepository<MoodEntry, Long> {
    List<MoodEntry> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    List<MoodEntry> findByUser(UserEntity user);
    List<MoodEntry> findByUserAndTimestampBetweenOrderByTimestampAsc(UserEntity user, LocalDateTime start, LocalDateTime end);
}
