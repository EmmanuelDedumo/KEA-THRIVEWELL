package com.KEA.g3.ThriveWell.repository;

import com.KEA.g3.ThriveWell.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByUserId(Long userId);
}