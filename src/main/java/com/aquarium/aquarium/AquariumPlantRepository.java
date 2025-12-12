package com.aquarium.aquarium;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AquariumPlantRepository extends JpaRepository<AquariumPlant, Long> {
    List<AquariumPlant> findByAquariumId(Long aquariumId);
    Optional<AquariumPlant> findByAquariumIdAndPlantId(Long aquariumId, Long plantId);
    void deleteByAquariumIdAndPlantId(Long aquariumId, Long plantId);
}

