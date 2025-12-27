package com.aquatracker.fish;

public class FishInAquariumDto {
    private String fishId;
    private Integer count;

    public FishInAquariumDto() {}

    public FishInAquariumDto(String fishId, Integer count) {
        this.fishId = fishId;
        this.count = count;
    }

    public String getFishId() {
        return fishId;
    }

    public void setFishId(String fishId) {
        this.fishId = fishId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}

