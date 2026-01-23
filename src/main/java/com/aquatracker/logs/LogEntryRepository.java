package com.aquatracker.logs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {
    List<LogEntry> findByUser_IdOrderByCreatedAtDesc(String userId);
    List<LogEntry> findByAquarium_IdOrderByCreatedAtDesc(Long aquariumId);
    List<LogEntry> findByUser_IdAndActionTypeOrderByCreatedAtDesc(String userId, String actionType);
    
    /**
     * Bulk update - ustawia aquarium_id na null dla wszystkich logów danego akwarium
     * Szybsze niż aktualizacja pojedynczo w pętli
     */
    @Modifying
    @Query("UPDATE LogEntry l SET l.aquarium = null WHERE l.aquarium.id = :aquariumId")
    void setAquariumToNullByAquariumId(@Param("aquariumId") Long aquariumId);
}

