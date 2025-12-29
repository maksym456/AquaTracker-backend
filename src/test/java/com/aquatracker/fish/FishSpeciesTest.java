package com.aquatracker.fish;  

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FishSpeciesTest {

    @Test
    void shouldSetAndGetAllBasicFieldsCorrectly() {
        FishSpecies fish = new FishSpecies();
        fish.setId(100L);
        fish.setName("Neon Innesa");
        fish.setDescription("Mała, ławicowa ryba z intensywnym niebieskim połyskiem");
        fish.setImage("/fish/Neon_Innesa.png");
        fish.setWaterType("Słodkowodna");
        fish.setTempMinC(22);
        fish.setTempMaxC(28);
        fish.setPhMin(6.0);
        fish.setPhMax(7.5);
        fish.setGhMin(5);
        fish.setGhMax(15);
        fish.setBiotype("Amazonia");
        fish.setTemperament("spokojne");
        fish.setMinSchoolSize(6);
        fish.setLifespan("3-5 lat");
        fish.setIconName("Neon_Innesa.png");
        assertThat(fish.getId()).isEqualTo(100L);
        assertThat(fish.getName()).isEqualTo("Neon Innesa");
        assertThat(fish.getDescription()).isEqualTo("Mała, ławicowa ryba z intensywnym niebieskim połyskiem");
        assertThat(fish.getImage()).isEqualTo("/fish/Neon_Innesa.png");
        assertThat(fish.getWaterType()).isEqualTo("Słodkowodna");
        assertThat(fish.getTempMinC()).isEqualTo(22);
        assertThat(fish.getTempMaxC()).isEqualTo(28);
        assertThat(fish.getPhMin()).isEqualTo(6.0);
        assertThat(fish.getPhMax()).isEqualTo(7.5);
        assertThat(fish.getGhMin()).isEqualTo(5);
        assertThat(fish.getGhMax()).isEqualTo(15);
        assertThat(fish.getBiotype()).isEqualTo("Amazonia");
        assertThat(fish.getTemperament()).isEqualTo("spokojne");
        assertThat(fish.getMinSchoolSize()).isEqualTo(6);
        assertThat(fish.getLifespan()).isEqualTo("3-5 lat");
        assertThat(fish.getIconName()).isEqualTo("Neon_Innesa.png");
    }

    @Test
    void shouldHaveCorrectDefaultValuesForNewInstance() {
        FishSpecies fish = new FishSpecies();
        assertThat(fish.getId()).isNull();
        assertThat(fish.getName()).isNull();
        assertThat(fish.getDescription()).isNull();
        assertThat(fish.getImage()).isNull();
        assertThat(fish.getWaterType()).isNull();
        assertThat(fish.getTempMinC()).isEqualTo(0);
        assertThat(fish.getTempMaxC()).isEqualTo(0);
        assertThat(fish.getPhMin()).isEqualTo(0.0);
        assertThat(fish.getPhMax()).isEqualTo(0.0);
        assertThat(fish.getGhMin()).isEqualTo(0);
        assertThat(fish.getGhMax()).isEqualTo(0);
        assertThat(fish.getBiotype()).isNull();
        assertThat(fish.getTemperament()).isNull();
        assertThat(fish.getMinSchoolSize()).isEqualTo(0);
        assertThat(fish.getLifespan()).isNull();
        assertThat(fish.getIconName()).isNull();
    }

    @Test
    void shouldAllowChangingValuesMultipleTimes() {
        FishSpecies fish = new FishSpecies();
        fish.setTempMinC(20);
        fish.setTempMaxC(26);
        assertThat(fish.getTempMinC()).isEqualTo(20);
        assertThat(fish.getTempMaxC()).isEqualTo(26);
        fish.setTempMinC(22);
        fish.setTempMaxC(28);
        assertThat(fish.getTempMinC()).isEqualTo(22);
        assertThat(fish.getTempMaxC()).isEqualTo(28);
    }

    @Test
    void shouldHandleSaltwaterType() {
        FishSpecies fish = new FishSpecies();
        fish.setWaterType("Słonowodna");
        assertThat(fish.getWaterType()).isEqualTo("Słonowodna");
    }

    @Test
    void shouldAcceptNullIconName() {
        FishSpecies fish = new FishSpecies();
        fish.setIconName(null);
        assertThat(fish.getIconName()).isNull();
    }
}