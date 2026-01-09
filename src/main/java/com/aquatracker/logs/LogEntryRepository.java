package com.aquatracker.logs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {
    List<LogEntry> findByUser_IdOrderByCreatedAtDesc(String userId);
    List<LogEntry> findByAquarium_IdOrderByCreatedAtDesc(Long aquariumId);
    List<LogEntry> findByUser_IdAndActionTypeOrderByCreatedAtDesc(String userId, String actionType);
}

