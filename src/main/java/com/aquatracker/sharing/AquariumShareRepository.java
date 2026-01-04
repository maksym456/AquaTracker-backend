package com.aquatracker.sharing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AquariumShareRepository extends JpaRepository<AquariumShare, Long> {
    List<AquariumShare> findByAquarium_Id(Long aquariumId);
    List<AquariumShare> findByUser_Id(Long userId);
    Optional<AquariumShare> findByAquarium_IdAndUser_Id(Long aquariumId, Long userId);
    void deleteByAquarium_IdAndUser_Id(Long aquariumId, Long userId);
}

