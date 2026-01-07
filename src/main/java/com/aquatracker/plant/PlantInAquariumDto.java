package com.aquatracker.plant;

public class PlantInAquariumDto {
    private Long plantId;
    private Integer count;

    public PlantInAquariumDto() {}

    public PlantInAquariumDto(Long plantId, Integer count) {
        this.plantId = plantId;
        this.count = count;
    }

    public Long getPlantId() {
        return plantId;
    }

    public void setPlantId(Long plantId) {
        this.plantId = plantId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}

