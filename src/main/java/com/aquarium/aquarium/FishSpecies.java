package com.aquarium.aquarium;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "fish_species")
public class FishSpecies {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // np. "Błazenek"

    private String waterType; // np. "Słodko/słonowodna"

    private int tempMinC; // Min temperatura

    private int tempMaxC; // Max temperatura, np. 26

    private String temperament; // np. "Spokojna/Agresywna/Nieboża"


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

    public String getTemperament() {
        return temperament;
    }

    public void setTemperament(String temperament) {
        this.temperament = temperament;
    }
}