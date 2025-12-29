package com.aquatracker.aquarium;

import com.aquatracker.plant.Plant;  

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AquariumPlantTest {

    @Test
    void shouldSetAndGetAllFieldsCorrectly() {
        Aquarium aquarium = new Aquarium();
        aquarium.setId(1L);
        aquarium.setName("Roślinne akwarium");
        Plant plant = new Plant();
        plant.setId(20L);
        plant.setName("Anubias nana");
        plant.setSpecies("Anubias barteri var. nana");
        plant.setTempMinC(22);
        plant.setTempMaxC(28);
        plant.setPhMin(6.0);
        plant.setPhMax(7.5);
        AquariumPlant aquariumPlant = new AquariumPlant();
        aquariumPlant.setId(300L);
        aquariumPlant.setAquarium(aquarium);
        aquariumPlant.setPlant(plant);
        aquariumPlant.setPlantCount(6);
        assertThat(aquariumPlant.getId()).isEqualTo(300L);
        assertThat(aquariumPlant.getAquarium()).isSameAs(aquarium);
        assertThat(aquariumPlant.getAquarium().getName()).isEqualTo("Roślinne akwarium");
        assertThat(aquariumPlant.getPlant()).isSameAs(plant);
        assertThat(aquariumPlant.getPlant().getName()).isEqualTo("Anubias nana");
        assertThat(aquariumPlant.getPlant().getSpecies()).isEqualTo("Anubias barteri var. nana");
        assertThat(aquariumPlant.getPlant().getTemperature()).isEqualTo("22-28");
        assertThat(aquariumPlant.getPlant().getPh()).isEqualTo("6.0-7.5");
        assertThat(aquariumPlant.getPlantCount()).isEqualTo(6);
    }

    @Test
    void shouldHaveDefaultValuesForNewInstance() {
        AquariumPlant aquariumPlant = new AquariumPlant();
        assertThat(aquariumPlant.getId()).isNull();
        assertThat(aquariumPlant.getAquarium()).isNull();
        assertThat(aquariumPlant.getPlant()).isNull();
        assertThat(aquariumPlant.getPlantCount()).isEqualTo(0);
    }

    @Test
    void shouldAllowChangingPlantCount() {
        AquariumPlant aquariumPlant = new AquariumPlant();
        Plant plant = new Plant();
        aquariumPlant.setPlant(plant);
        aquariumPlant.setPlantCount(3);
        assertThat(aquariumPlant.getPlantCount()).isEqualTo(3);
        aquariumPlant.setPlantCount(10);
        assertThat(aquariumPlant.getPlantCount()).isEqualTo(10);
    }

    @Test
    void shouldHandleZeroAndNegativePlantCount() {
        AquariumPlant aquariumPlant = new AquariumPlant();
        aquariumPlant.setPlantCount(0);
        assertThat(aquariumPlant.getPlantCount()).isEqualTo(0);
        aquariumPlant.setPlantCount(-5);
        assertThat(aquariumPlant.getPlantCount()).isEqualTo(-5);
    }

    @Test
    void shouldUseHelperMethodsInPlant() {
        Plant plant = new Plant();
        plant.setTempMinC(20);
        plant.setTempMaxC(26);
        plant.setPhMin(6.5);
        plant.setPhMax(7.8);
        plant.setGhMin(4);
        plant.setGhMax(12);
        plant.setIconName("anubias.png");
        assertThat(plant.getTemperature()).isEqualTo("20-26");
        assertThat(plant.getPh()).isEqualTo("6.5-7.8");
        assertThat(plant.getHardnessDGH()).isEqualTo("4-12");
        assertThat(plant.getIconName()).isEqualTo("anubias.png");
        plant.setIconName(null);
        assertThat(plant.getIconName()).isEmpty();
    }
}