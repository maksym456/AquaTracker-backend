package com.aquatracker.aquarium;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AquariumFishRepository extends JpaRepository<AquariumFish, Long> {
    List<AquariumFish> findByAquariumId(Long aquariumId);
    Optional<AquariumFish> findByAquariumIdAndFishSpeciesId(Long aquariumId, Long fishSpeciesId);
    void deleteByAquariumIdAndFishSpeciesId(Long aquariumId, Long fishSpeciesId);
}

