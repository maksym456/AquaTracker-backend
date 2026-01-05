package com.aquatracker.plant;

public class PlantResponseDto {
    private Long id;
    private String name;
    private String temperature;
    private String biotope;
    private String ph;
    private String hardnessDGH;
    private String iconName;

    public PlantResponseDto() {}

    public PlantResponseDto(Plant plant) {
        this.id = plant.getId(); // Long ID bez prefiksu
        this.name = plant.getName();
        this.temperature = plant.getTemperature();
        this.biotope = plant.getBiotope();
        this.ph = plant.getPh();
        this.hardnessDGH = plant.getHardnessDGH();
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

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }
}

