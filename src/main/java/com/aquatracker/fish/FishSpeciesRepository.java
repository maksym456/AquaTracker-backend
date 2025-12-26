package com.aquatracker.fish;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FishSpeciesRepository extends JpaRepository<FishSpecies, Long> {
    List<FishSpecies> findByName(String name);
}