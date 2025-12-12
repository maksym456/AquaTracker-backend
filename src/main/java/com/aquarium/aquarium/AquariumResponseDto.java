package com.aquarium.aquarium;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class AquariumResponseDto {
    private String id;
    private String ownerId;
    private String name;
    private String description;
    private Integer volumeLiters;
    private String waterType;
    private Double temperatureC;
    private Double ph;
    private Integer hardnessDGH;
    private List<FishInAquariumDto> fish;
    private List<PlantInAquariumDto> plants;
    private AquariumStatusDto status;
    private LocalDateTime createdAt;

    public AquariumResponseDto() {}

    public AquariumResponseDto(Aquarium aquarium, AquariumValidationService validationService) {
        this.id = IdMapper.toAquariumId(aquarium.getId());
        this.ownerId = aquarium.getOwner() != null ? IdMapper.toUserId(aquarium.getOwner().getId()) : null;
        this.name = aquarium.getName();
        this.description = aquarium.getDescription();
        this.volumeLiters = aquarium.getVolumeLiters();
        this.waterType = aquarium.getWaterType();
        this.temperatureC = aquarium.getTemperatureC();
        this.ph = aquarium.getPh();
        this.hardnessDGH = aquarium.getHardnessDGH();
        this.createdAt = aquarium.getCreatedAt();
        
        try {
            this.fish = aquarium.getFishInAquarium() != null && !aquarium.getFishInAquarium().isEmpty()
                ? aquarium.getFishInAquarium().stream()
                    .filter(af -> af != null && af.getFishSpecies() != null)
                    .map(af -> new FishInAquariumDto(IdMapper.toFishId(af.getFishSpecies().getId()), af.getFishCount()))
                    .collect(Collectors.toList())
                : List.of();
        } catch (Exception e) {
            this.fish = List.of();
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

    public Integer getVolumeLiters() {
        return volumeLiters;
    }

    public void setVolumeLiters(Integer volumeLiters) {
        this.volumeLiters = volumeLiters;
    }

    public String getWaterType() {
        return waterType;
    }

    public void setWaterType(String waterType) {
        this.waterType = waterType;
    }

    public Double getTemperatureC() {
        return temperatureC;
    }

    public void setTemperatureC(Double temperatureC) {
        this.temperatureC = temperatureC;
    }

    public Double getPh() {
        return ph;
    }

    public void setPh(Double ph) {
        this.ph = ph;
    }

    public Integer getHardnessDGH() {
        return hardnessDGH;
    }

    public void setHardnessDGH(Integer hardnessDGH) {
        this.hardnessDGH = hardnessDGH;
    }

    public List<FishInAquariumDto> getFish() {
        return fish;
    }

    public void setFish(List<FishInAquariumDto> fish) {
        this.fish = fish;
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

