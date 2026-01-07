package com.aquatracker.fish;

public class FishResponseDto {
    private Long id;
    private String name;
    private String waterType; // OpenAPI: enum [Słodkowodna, Słonawowodna, Słonowodna]
    private String temperature; // String format: "22-26"
    private String biotope;
    private String ph; // String format: "6.5-7.5"
    private String hardnessDGH; // String format: "1-12"
    private String temperament;
    private Integer minShoalSize;
    private String lifeSpan;
    private String iconName;

    public FishResponseDto() {}

    public FishResponseDto(FishSpecies fish) {
        this.id = fish.getId(); // Long ID bez prefiksu
        this.name = fish.getName();
        this.waterType = fish.getWaterType(); // Używamy oryginalnej wartości (Słodkowodna/Słonowodna/Słonawowodna)
        this.temperature = fish.getTemperature(); // Zwraca "22-26"
        this.biotope = fish.getBiotype();
        this.ph = fish.getPh(); // Zwraca "6.5-7.5"
        this.hardnessDGH = fish.getHardnessDGH(); // Zwraca "1-12"
        this.temperament = fish.getTemperament();
        this.minShoalSize = fish.getMinShoalSize();
        this.lifeSpan = fish.getLifeSpan();
        this.iconName = fish.getIconName();
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
