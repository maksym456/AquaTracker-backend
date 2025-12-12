package com.aquarium.aquarium;

import jakarta.persistence.*;

@Entity
@Table(name = "fish_species")
public class FishSpecies {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String waterType;
    private String biotype;
    private int tempMinC;
    private int tempMaxC;
    private double phMin;
    private double phMax;
    private int ghMin;
    private int ghMax;
    private String temperament;
    private int minSchoolSize;
    private String lifespan;
    private String description;
    private String image;
    private String iconName;

    public FishSpecies() {}

    public FishSpecies(String name, String waterType, int tempMinC, int tempMaxC, String biotype,
                       double phMin, double phMax, int ghMin, int ghMax,
                       String temperament, int minSchoolSize, String lifespan, String description, String image) {
        this.name = name;
        this.waterType = waterType;
        this.tempMinC = tempMinC;
        this.tempMaxC = tempMaxC;
        this.biotype = biotype;
        this.phMin = phMin;
        this.phMax = phMax;
        this.ghMin = ghMin;
        this.ghMax = ghMax;
        this.temperament = temperament;
        this.minSchoolSize = minSchoolSize;
        this.lifespan = lifespan;
        this.description = description;
        this.image = image;
        this.iconName = image != null ? image.replace("/fish/", "") : null;
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

    public String getWaterType() {
        return waterType;
    }

    public void setWaterType(String waterType) {
        this.waterType = waterType;
    }

    public String getBiotype() {
        return biotype;
    }

    public void setBiotype(String biotype) {
        this.biotype = biotype;
    }

    public int getTempMinC() {
        return tempMinC;
    }

    public void setTempMinC(int tempMinC) {
        this.tempMinC = tempMinC;
    }

    public int getTempMaxC() {
        return tempMaxC;
    }

    public void setTempMaxC(int tempMaxC) {
        this.tempMaxC = tempMaxC;
    }

    public double getPhMin() {
        return phMin;
    }

    public void setPhMin(double phMin) {
        this.phMin = phMin;
    }

    public double getPhMax() {
        return phMax;
    }

    public void setPhMax(double phMax) {
        this.phMax = phMax;
    }

    public int getGhMin() {
        return ghMin;
    }

    public void setGhMin(int ghMin) {
        this.ghMin = ghMin;
    }

    public int getGhMax() {
        return ghMax;
    }

    public void setGhMax(int ghMax) {
        this.ghMax = ghMax;
    }

    public String getTemperament() {
        return temperament;
    }

    public void setTemperament(String temperament) {
        this.temperament = temperament;
    }

    public int getMinSchoolSize() {
        return minSchoolSize;
    }

    public void setMinSchoolSize(int minSchoolSize) {
        this.minSchoolSize = minSchoolSize;
    }

    public String getLifespan() {
        return lifespan;
    }

    public void setLifespan(String lifespan) {
        this.lifespan = lifespan;
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

    public String getIconName() {
        return iconName != null ? iconName : (image != null ? image : "");
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public String getTemperature() {
        return tempMinC + "-" + tempMaxC;
    }

    public String getPh() {
        return phMin + "-" + phMax;
    }

    public String getHardnessDGH() {
        return ghMin + "-" + ghMax;
    }

    public int getMinShoalSize() {
        return minSchoolSize;
    }

    public void setMinShoalSize(int minShoalSize) {
        this.minSchoolSize = minShoalSize;
    }

    public String getLifeSpan() {
        return lifespan;
    }

    public void setLifeSpan(String lifeSpan) {
        this.lifespan = lifeSpan;
    }
}
