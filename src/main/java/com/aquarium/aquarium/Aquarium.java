package com.aquarium.aquarium;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "aquariums")
public class Aquarium {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int volumeLiters;
    private double temperatureC;
    private String waterType;
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @ManyToMany
    @JoinTable(
            name = "aquarium_plant",
            joinColumns = @JoinColumn(name = "aquarium_id"),
            inverseJoinColumns = @JoinColumn(name = "plant_id")
    )
    private Set<Plant> plants;

    @OneToMany(mappedBy = "aquarium", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AquariumFish> fishInAquarium;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVolumeLiters() {
        return volumeLiters;
    }

    public void setVolumeLiters(int volumeLiters) {
        this.volumeLiters = volumeLiters;
    }

    public double getTemperatureC() {
        return temperatureC;
    }

    public void setTemperatureC(double temperatureC) {
        this.temperatureC = temperatureC;
    }

    public String getWaterType() {
        return waterType;
    }

    public void setWaterType(String waterType) {
        this.waterType = waterType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Set<Plant> getPlants() {
        return plants;
    }

    public void setPlants(Set<Plant> plants) {
        this.plants = plants;
    }

    public List<AquariumFish> getFishInAquarium() {
        return fishInAquarium;
    }

    public void setFishInAquarium(List<AquariumFish> fishInAquarium) {
        this.fishInAquarium = fishInAquarium;
    }
}