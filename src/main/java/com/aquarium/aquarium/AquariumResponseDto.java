package com.aquarium.aquarium;

import java.util.List;
import java.util.stream.Collectors;

public class AquariumResponseDto {
    private Long id;
    private String name;
    private String waterType;
    private Double temperature;
    private String biotope;
    private Double ph;
    private Integer hardness;
    private String description;
    private List<Long> fishes;
    private List<Long> plants;

    public AquariumResponseDto() {}

    public AquariumResponseDto(Aquarium aquarium) {
        this.id = aquarium.getId();
        this.name = aquarium.getName();
        this.waterType = aquarium.getWaterType();
        this.temperature = aquarium.getTemperatureC();
        this.biotope = aquarium.getBiotope();
        this.ph = aquarium.getPh();
        this.hardness = aquarium.getHardness();
        this.description = aquarium.getDescription();
        
        try {
            this.fishes = aquarium.getFishInAquarium() != null && !aquarium.getFishInAquarium().isEmpty()
                ? aquarium.getFishInAquarium().stream()
                    .filter(af -> af != null && af.getFishSpecies() != null)
                    .map(af -> af.getFishSpecies().getId())
                    .filter(id -> id != null)
                    .collect(Collectors.toList())
                : List.of();
        } catch (Exception e) {
            this.fishes = List.of();
        }
            
        try {
            this.plants = aquarium.getPlants() != null && !aquarium.getPlants().isEmpty()
                ? aquarium.getPlants().stream()
                    .filter(p -> p != null && p.getId() != null)
                    .map(Plant::getId)
                    .collect(Collectors.toList())
                : List.of();
        } catch (Exception e) {
            this.plants = List.of();
        }
    }

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

    public String getWaterType() {
        return waterType;
    }

    public void setWaterType(String waterType) {
        this.waterType = waterType;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public String getBiotope() {
        return biotope;
    }

    public void setBiotope(String biotope) {
        this.biotope = biotope;
    }

    public Double getPh() {
        return ph;
    }

    public void setPh(Double ph) {
        this.ph = ph;
    }

    public Integer getHardness() {
        return hardness;
    }

    public void setHardness(Integer hardness) {
        this.hardness = hardness;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Long> getFishes() {
        return fishes;
    }

    public void setFishes(List<Long> fishes) {
        this.fishes = fishes;
    }

    public List<Long> getPlants() {
        return plants;
    }

    public void setPlants(List<Long> plants) {
        this.plants = plants;
    }
}

