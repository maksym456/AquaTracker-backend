package com.aquatracker.fish;

import com.aquatracker.common.IdMapper;

import java.util.Arrays;
import java.util.List;

public class FishResponseDto {
    private String id;
    private String name;
    private String waterType; // "freshwater" lub "saltwater"
    private List<Integer> tempRange; // [min, max]
    private String biotope;
    private List<Double> phRange; // [min, max]
    private List<Integer> hardness; // [min, max]
    private String temperament;
    private Integer minSchoolSize;
    private String lifespan;
    private String iconName;

    public FishResponseDto() {}

    public FishResponseDto(FishSpecies fish) {
        this.id = IdMapper.toFishId(fish.getId());
        this.name = fish.getName();
        
        // Mapowanie waterType: "Słodkowodna" -> "freshwater", "Słonowodna" -> "saltwater"
        if (fish.getWaterType() != null) {
            if (fish.getWaterType().equals("Słonowodna") || fish.getWaterType().equals("Słonawowodna")) {
                this.waterType = "saltwater";
            } else {
                this.waterType = "freshwater";
            }
        } else {
            this.waterType = "freshwater";
        }
        
        // Konwersja temperature z String "22-26" na List<Integer> [22, 26]
        this.tempRange = parseRange(fish.getTempMinC(), fish.getTempMaxC());
        
        this.biotope = fish.getBiotype();
        
        // Konwersja ph z String "6.5-7.5" na List<Double> [6.5, 7.5]
        this.phRange = Arrays.asList(fish.getPhMin(), fish.getPhMax());
        
        // Konwersja hardness z String "1-12" na List<Integer> [1, 12]
        this.hardness = Arrays.asList(fish.getGhMin(), fish.getGhMax());
        
        this.temperament = fish.getTemperament();
        this.minSchoolSize = fish.getMinShoalSize();
        this.lifespan = fish.getLifespan();
        this.iconName = fish.getIconName();
    }

    private List<Integer> parseRange(int min, int max) {
        return Arrays.asList(min, max);
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

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }
}
