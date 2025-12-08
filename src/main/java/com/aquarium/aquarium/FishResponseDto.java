package com.aquarium.aquarium;

import java.util.List;

public class FishResponseDto {
    private Long id;
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

    public FishResponseDto() {}

    public FishResponseDto(FishSpecies fish) {
        this.id = fish.getId();
        this.name = fish.getName();
        this.description = fish.getDescription();
        this.image = fish.getImage();
        this.waterType = fish.getWaterType() != null && fish.getWaterType().equals("Słonowodna") 
            ? "saltwater" 
            : "freshwater";
        this.tempRange = List.of(fish.getTempMinC(), fish.getTempMaxC());
        this.biotope = fish.getBiotype();
        this.phRange = List.of(fish.getPhMin(), fish.getPhMax());
        this.hardness = List.of(fish.getGhMin(), fish.getGhMax());
        this.temperament = fish.getTemperament();
        this.minSchoolSize = fish.getMinSchoolSize();
        this.lifespan = fish.getLifespan();
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

