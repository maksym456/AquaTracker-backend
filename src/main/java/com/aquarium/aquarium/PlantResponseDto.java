package com.aquarium.aquarium;

public class PlantResponseDto {
    private Long id;
    private String name;
    private String species;

    public PlantResponseDto() {}

    public PlantResponseDto(Plant plant) {
        this.id = plant.getId();
        this.name = plant.getName();
        this.species = plant.getSpecies();
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

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }
}

