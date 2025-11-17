package com.aquarium.aquarium;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/fish")
public class FishController {

    private final FishSpeciesRepository fishSpeciesRepository;

    @Autowired
    public FishController(FishSpeciesRepository fishSpeciesRepository) {
        this.fishSpeciesRepository = fishSpeciesRepository;
    }

    // Endpoint 3.1: List fish
    @GetMapping
    public List<FishSpecies> getAllFish() {
        return fishSpeciesRepository.findAll();
    }

    // Endpoint 3.2: Get fish by id
    @GetMapping("/{fishId}")
    public ResponseEntity<FishSpecies> getFishById(@PathVariable Long fishId) {
        Optional<FishSpecies> fish = fishSpeciesRepository.findById(fishId);
        return fish.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Endpoint do tworzenia nowych gatunków ryb (potrzebny do testów)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FishSpecies createFish(@RequestBody FishSpecies fish) {
        return fishSpeciesRepository.save(fish);
    }
}