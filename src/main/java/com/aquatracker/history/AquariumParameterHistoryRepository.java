package com.aquatracker.history;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AquariumParameterHistoryRepository extends JpaRepository<AquariumParameterHistory, Long> {
    List<AquariumParameterHistory> findByAquarium_IdOrderByChangedAtDesc(Long aquariumId);
    List<AquariumParameterHistory> findByUser_IdOrderByChangedAtDesc(Long userId);
    List<AquariumParameterHistory> findByAquarium_IdAndParameterNameOrderByChangedAtDesc(Long aquariumId, String parameterName);
}

