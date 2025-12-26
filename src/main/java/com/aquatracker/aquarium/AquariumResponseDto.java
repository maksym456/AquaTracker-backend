package com.aquatracker.aquarium;

import com.aquatracker.fish.FishInAquariumDto;
import com.aquatracker.common.IdMapper;
import com.aquatracker.plant.PlantInAquariumDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class AquariumResponseDto {
    private String id;
    private String ownerId;
    private String name;
    private String description;
    private Integer volume; // zmienione z volumeLiters
    private String waterType;
    private Double temperature; // zmienione z temperatureC
    private String biotope;
    private Double ph;
    private Integer hardness; // zmienione z hardnessDGH
    private List<FishInAquariumDto> fishes; // zmienione z fish
    private List<PlantInAquariumDto> plants;
    private AquariumStatusDto status;
    private LocalDateTime createdAt;

    public AquariumResponseDto() {}

    public AquariumResponseDto(Aquarium aquarium, AquariumValidationService validationService) {
        this.id = IdMapper.toAquariumId(aquarium.getId());
        this.ownerId = aquarium.getOwner() != null ? IdMapper.toUserId(aquarium.getOwner().getId()) : null;
        this.name = aquarium.getName();
        this.description = aquarium.getDescription();
        this.volume = aquarium.getVolumeLiters(); // mapowanie volumeLiters -> volume
        this.waterType = aquarium.getWaterType();
        this.temperature = aquarium.getTemperatureC(); // mapowanie temperatureC -> temperature
        this.biotope = aquarium.getBiotope();
        this.ph = aquarium.getPh();
        this.hardness = aquarium.getHardnessDGH(); // mapowanie hardnessDGH -> hardness
        this.createdAt = aquarium.getCreatedAt();
        
        try {
            this.fishes = aquarium.getFishInAquarium() != null && !aquarium.getFishInAquarium().isEmpty()
                ? aquarium.getFishInAquarium().stream()
                    .filter(af -> af != null && af.getFishSpecies() != null)
                    .map(af -> new FishInAquariumDto(IdMapper.toFishId(af.getFishSpecies().getId()), af.getFishCount()))
                    .collect(Collectors.toList())
                : List.of();
        } catch (Exception e) {
            this.fishes = List.of();
        }
            
        try {
            this.plants = aquarium.getPlantsInAquarium() != null && !aquarium.getPlantsInAquarium().isEmpty()
                ? aquarium.getPlantsInAquarium().stream()
                    .filter(ap -> ap != null && ap.getPlant() != null)
                    .map(ap -> new PlantInAquariumDto(IdMapper.toPlantId(ap.getPlant().getId()), ap.getPlantCount()))
                    .collect(Collectors.toList())
                : List.of();
        } catch (Exception e) {
            this.plants = List.of();
        }

        if (validationService != null) {
            this.status = validationService.validateAquarium(aquarium);
        } else {
            // Fallback - jeśli validationService nie jest dostępny, tworzymy pusty status
            this.status = new AquariumStatusDto("OK", List.of(), LocalDateTime.now());
        }
    }

    public AquariumResponseDto(Aquarium aquarium) {
        this(aquarium, null);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
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

    public List<FishInAquariumDto> getFishes() {
        return fishes;
    }

    public void setFishes(List<FishInAquariumDto> fishes) {
        this.fishes = fishes;
    }

    public List<PlantInAquariumDto> getPlants() {
        return plants;
    }

    public void setPlants(List<PlantInAquariumDto> plants) {
        this.plants = plants;
    }

    public AquariumStatusDto getStatus() {
        return status;
    }

    public void setStatus(AquariumStatusDto status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
