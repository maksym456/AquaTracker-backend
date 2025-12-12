package com.aquarium.aquarium;

import java.util.List;

public class FishResponseDto {
    private String id;
    private String name;
    private String waterType;
    private String temperature;
    private String biotope;
    private String ph;
    private String hardnessDGH;
    private String temperament;
    private Integer minShoalSize;
    private String lifeSpan;
    private String iconName;

    public FishResponseDto() {}

    public FishResponseDto(FishSpecies fish) {
        this.id = IdMapper.toFishId(fish.getId());
        this.name = fish.getName();
        this.waterType = fish.getWaterType();
        this.temperature = fish.getTemperature();
        this.biotope = fish.getBiotype();
        this.ph = fish.getPh();
        this.hardnessDGH = fish.getHardnessDGH();
        this.temperament = fish.getTemperament();
        this.minShoalSize = fish.getMinShoalSize();
        this.lifeSpan = fish.getLifeSpan();
        this.iconName = fish.getIconName();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getBiotope() {
        return biotope;
    }

    public void setBiotope(String biotope) {
        this.biotope = biotope;
    }

    public String getPh() {
        return ph;
    }

    public void setPh(String ph) {
        this.ph = ph;
    }

    public String getHardnessDGH() {
        return hardnessDGH;
    }

    public void setHardnessDGH(String hardnessDGH) {
        this.hardnessDGH = hardnessDGH;
    }

    public String getTemperament() {
        return temperament;
    }

    public void setTemperament(String temperament) {
        this.temperament = temperament;
    }

    public Integer getMinShoalSize() {
        return minShoalSize;
    }

    public void setMinShoalSize(Integer minShoalSize) {
        this.minShoalSize = minShoalSize;
    }

    public String getLifeSpan() {
        return lifeSpan;
    }

    public void setLifeSpan(String lifeSpan) {
        this.lifeSpan = lifeSpan;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }
}

