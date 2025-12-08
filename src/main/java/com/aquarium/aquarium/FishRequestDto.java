package com.aquarium.aquarium;

import java.util.List;

public class FishRequestDto {
    private String name;
    private String description;
    private String image;
    private String waterType;
    private List<Integer> tempRange;
    private String biotope;
    private List<Double> phRange;
    private List<Integer> hardness;
    private String temperament;
    private Integer minSchoolSize;
    private String lifespan;

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getWaterType() {
        return waterType;
    }

    public void setWaterType(String waterType) {
        this.waterType = waterType;
    }

    public List<Integer> getTempRange() {
        return tempRange;
    }

    public void setTempRange(List<Integer> tempRange) {
        this.tempRange = tempRange;
    }

    public String getBiotope() {
        return biotope;
    }

    public void setBiotope(String biotope) {
        this.biotope = biotope;
    }

    public List<Double> getPhRange() {
        return phRange;
    }

    public void setPhRange(List<Double> phRange) {
        this.phRange = phRange;
    }

    public List<Integer> getHardness() {
        return hardness;
    }

    public void setHardness(List<Integer> hardness) {
        this.hardness = hardness;
    }

    public String getTemperament() {
        return temperament;
    }

    public void setTemperament(String temperament) {
        this.temperament = temperament;
    }

    public Integer getMinSchoolSize() {
        return minSchoolSize;
    }

    public void setMinSchoolSize(Integer minSchoolSize) {
        this.minSchoolSize = minSchoolSize;
    }

    public String getLifespan() {
        return lifespan;
    }

    public void setLifespan(String lifespan) {
        this.lifespan = lifespan;
    }
}

