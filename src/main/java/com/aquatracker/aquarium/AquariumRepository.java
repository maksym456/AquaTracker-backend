package com.aquatracker.aquarium;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AquariumRepository extends JpaRepository<Aquarium, Long> {
    List<Aquarium> findByOwner_Id(String ownerId);
}

