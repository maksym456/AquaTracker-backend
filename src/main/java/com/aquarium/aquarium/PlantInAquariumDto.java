package com.aquarium.aquarium;

public class PlantInAquariumDto {
    private String plantId;
    private Integer count;

    public PlantInAquariumDto() {}

    public PlantInAquariumDto(String plantId, Integer count) {
        this.plantId = plantId;
        this.count = count;
    }

    public String getPlantId() {
        return plantId;
    }

    public void setPlantId(String plantId) {
        this.plantId = plantId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}

