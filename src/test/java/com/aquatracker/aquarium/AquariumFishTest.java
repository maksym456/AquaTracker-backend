package com.aquatracker.aquarium;

import com.aquatracker.fish.FishSpecies;  

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AquariumFishTest {

    @Test
    void shouldSetAndGetAllFieldsCorrectly() {
        Aquarium aquarium = new Aquarium();
        aquarium.setId(1L);
        aquarium.setName("Główne akwarium");

        FishSpecies fishSpecies = new FishSpecies(); 
        fishSpecies.setId(30L);
        fishSpecies.setName("Neon Innesa");
        AquariumFish aquariumFish = new AquariumFish();
        aquariumFish.setId(400L);
        aquariumFish.setAquarium(aquarium);
        aquariumFish.setFishSpecies(fishSpecies);
        aquariumFish.setFishCount(20);
        assertThat(aquariumFish.getId()).isEqualTo(400L);
        assertThat(aquariumFish.getAquarium()).isSameAs(aquarium);
        assertThat(aquariumFish.getAquarium().getName()).isEqualTo("Główne akwarium");
        assertThat(aquariumFish.getFishSpecies()).isSameAs(fishSpecies);
        assertThat(aquariumFish.getFishSpecies().getName()).isEqualTo("Neon Innesa");
        assertThat(aquariumFish.getFishCount()).isEqualTo(20);
    }

    @Test
    void shouldHaveDefaultValuesForNewInstance() {
        AquariumFish aquariumFish = new AquariumFish();
        assertThat(aquariumFish.getId()).isNull();
        assertThat(aquariumFish.getAquarium()).isNull();
        assertThat(aquariumFish.getFishSpecies()).isNull();
        assertThat(aquariumFish.getFishCount()).isEqualTo(0);
    }

    @Test
    void shouldAllowChangingFishCountMultipleTimes() {
        AquariumFish aquariumFish = new AquariumFish();
        FishSpecies species = new FishSpecies();
        aquariumFish.setFishSpecies(species);
        aquariumFish.setFishCount(12);
        assertThat(aquariumFish.getFishCount()).isEqualTo(12);
        aquariumFish.setFishCount(35);
        assertThat(aquariumFish.getFishCount()).isEqualTo(35);
    }

    @Test
    void shouldHandleZeroFishCount() {
        AquariumFish aquariumFish = new AquariumFish();
        aquariumFish.setFishCount(0);
        assertThat(aquariumFish.getFishCount()).isEqualTo(0);
    }

    @Test
    void shouldAcceptNegativeFishCountCurrently() {
        AquariumFish aquariumFish = new AquariumFish();
        aquariumFish.setFishCount(-3);
        assertThat(aquariumFish.getFishCount()).isEqualTo(-3);
    }

}