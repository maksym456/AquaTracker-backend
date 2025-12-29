package com.aquatracker.aquarium;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AquariumTest {

    @Test
    void shouldSetAndGetAllBasicFieldsCorrectly() {
        Aquarium aquarium = new Aquarium();

        aquarium.setName("Moje piękne akwarium");
        aquarium.setVolumeLiters(120);
        aquarium.setTemperatureC(25.5);
        aquarium.setWaterType("sweet"); 
        aquarium.setBiotope("Amazonia");
        aquarium.setPh(7.2);
        aquarium.setHardness(8);
        aquarium.setHardnessDGH(10); 
        aquarium.setDescription("Piękne akwarium roślinne z neonkami");
        aquarium.setCreatedAt(LocalDateTime.of(2025, 12, 29, 12, 0));
        assertThat(aquarium.getName()).isEqualTo("Moje piękne akwarium");
        assertThat(aquarium.getVolumeLiters()).isEqualTo(120);
        assertThat(aquarium.getTemperatureC()).isEqualTo(25.5);
        assertThat(aquarium.getWaterType()).isEqualTo("sweet");
        assertThat(aquarium.getBiotope()).isEqualTo("Amazonia");
        assertThat(aquarium.getPh()).isEqualTo(7.2);
        assertThat(aquarium.getHardness()).isEqualTo(8);
        assertThat(aquarium.getDescription()).isEqualTo("Piękne akwarium roślinne z neonkami");
        assertThat(aquarium.getCreatedAt()).isEqualTo(LocalDateTime.of(2025, 12, 29, 12, 0));
    }

    @Test
    void shouldReturnHardnessDGHWhenSet() {
        Aquarium aquarium = new Aquarium();
        aquarium.setHardnessDGH(12);
        assertThat(aquarium.getHardnessDGH()).isEqualTo(12);
        assertThat(aquarium.getHardness()).isEqualTo(12);
    }

    @Test
    void shouldKeepOriginalHardnessWhenHardnessDGHIsSetLater() {
        Aquarium aquarium = new Aquarium();
        aquarium.setHardness(7);
        aquarium.setHardnessDGH(15);
        assertThat(aquarium.getHardnessDGH()).isEqualTo(15);
        assertThat(aquarium.getHardness()).isEqualTo(7);
    }

    @Test
    void shouldReturnZeroValuesForNewAquarium() {
        Aquarium aquarium = new Aquarium();
        assertThat(aquarium.getVolumeLiters()).isEqualTo(0);
        assertThat(aquarium.getTemperatureC()).isEqualTo(0.0);
        assertThat(aquarium.getPh()).isNull();
        assertThat(aquarium.getHardness()).isNull();
        assertThat(aquarium.getHardnessDGH()).isNull();
        assertThat(aquarium.getName()).isNull();
    }
}