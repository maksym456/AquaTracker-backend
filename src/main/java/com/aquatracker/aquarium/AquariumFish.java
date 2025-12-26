package com.aquatracker.aquarium;

import com.aquatracker.fish.FishSpecies;
import jakarta.persistence.*;

@Entity
@Table(name = "aquarium_fish")
public class AquariumFish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aquarium_id")
    private Aquarium aquarium;

    @ManyToOne
    @JoinColumn(name = "fish_species_id")
    private FishSpecies fishSpecies;

    private int fishCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Aquarium getAquarium() {
        return aquarium;
    }

    public void setAquarium(Aquarium aquarium) {
        this.aquarium = aquarium;
    }

    public FishSpecies getFishSpecies() {
        return fishSpecies;
    }

    public void setFishSpecies(FishSpecies fishSpecies) {
        this.fishSpecies = fishSpecies;
    }

    public int getFishCount() {
        return fishCount;
    }

    public void setFishCount(int fishCount) {
        this.fishCount = fishCount;
    }
}