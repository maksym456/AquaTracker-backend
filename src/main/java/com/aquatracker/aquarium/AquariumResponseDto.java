package com.aquatracker.aquarium;

import com.aquatracker.fish.FishInAquariumDto;
import com.aquatracker.plant.PlantInAquariumDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class AquariumResponseDto {
    private Long id;
    private String ownerId; // UUID (String) - TODO: zmienić na UUID gdy będzie implementacja
    private String name;
    private String description;
    private Integer volumeLiters;
    private String waterType;
    private Double temperatureC;
    private String biotope;
    private Double ph;
    private Integer hardnessDGH;
    private List<FishInAquariumDto> fish; // OpenAPI: fish (nie fishes)
    private List<PlantInAquariumDto> plants;
    private AquariumStatusDto status;
    private LocalDateTime createdAt;

    public AquariumResponseDto() {}

    public AquariumResponseDto(Aquarium aquarium, AquariumValidationService validationService) {
        this.id = aquarium.getId(); // Long ID bez prefiksu
        // TODO: ownerId powinno być UUID z cognitoSub, na razie używamy Long ID jako string
        this.ownerId = aquarium.getOwner() != null ? aquarium.getOwner().getId().toString() : null;
        this.name = aquarium.getName();
        this.description = aquarium.getDescription();
        this.volumeLiters = aquarium.getVolumeLiters();
        this.waterType = aquarium.getWaterType();
        this.temperatureC = aquarium.getTemperatureC();
        this.biotope = aquarium.getBiotope();
        this.ph = aquarium.getPh();
        this.hardnessDGH = aquarium.getHardnessDGH();
        this.createdAt = aquarium.getCreatedAt();
        
        try {
            this.fish = aquarium.getFishInAquarium() != null && !aquarium.getFishInAquarium().isEmpty()
                ? aquarium.getFishInAquarium().stream()
                    .filter(af -> af != null && af.getFishSpecies() != null)
                    .map(af -> new FishInAquariumDto(af.getFishSpecies().getId(), af.getFishCount()))
                    .collect(Collectors.toList())
                : List.of();
        } catch (Exception e) {
            this.fish = List.of();
        }
            
        try {
            this.plants = aquarium.getPlantsInAquarium() != null && !aquarium.getPlantsInAquarium().isEmpty()
                ? aquarium.getPlantsInAquarium().stream()
                    .filter(ap -> ap != null && ap.getPlant() != null)
                    .map(ap -> new PlantInAquariumDto(ap.getPlant().getId(), ap.getPlantCount()))
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
