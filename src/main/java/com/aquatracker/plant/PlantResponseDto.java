package com.aquatracker.plant;

public class PlantResponseDto {
    private Long id;
    private String name;
    private String temperature;
    private String biotope;
    private String ph;
    private String hardnessDGH;
    private String lightRequirements;
    private String co2Requirements;
    private String difficulty;
    private String description;
    private String iconName;

    public PlantResponseDto() {}

    public PlantResponseDto(Plant plant) {
        this.id = plant.getId(); // Long ID bez prefiksu
        this.name = plant.getName();
        this.temperature = plant.getTemperature();
        this.biotope = plant.getBiotope();
        this.ph = plant.getPh();
        this.hardnessDGH = plant.getHardnessDGH();
        this.lightRequirements = plant.getLightRequirements();
        this.co2Requirements = plant.getCo2Requirements();
        this.difficulty = plant.getDifficulty();
        this.description = plant.getDescription();
        this.iconName = plant.getIconName();
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

    public String getLightRequirements() {
        return lightRequirements;
    }

    public void setLightRequirements(String lightRequirements) {
        this.lightRequirements = lightRequirements;
    }

    public String getCo2Requirements() {
        return co2Requirements;
    }

    public void setCo2Requirements(String co2Requirements) {
        this.co2Requirements = co2Requirements;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }
}

