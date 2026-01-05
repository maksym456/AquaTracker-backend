package com.aquatracker.fish;

public class FishInAquariumDto {
    private Long fishId;
    private Integer count;

    public FishInAquariumDto() {}

    public FishInAquariumDto(Long fishId, Integer count) {
        this.fishId = fishId;
        this.count = count;
    }

    public Long getFishId() {
        return fishId;
    }

    public void setFishId(Long fishId) {
        this.fishId = fishId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}

