package com.aquatracker.plant;

import jakarta.persistence.*;

@Entity
@Table(name = "plants")
public class Plant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String species;
    private String biotope;
    private int tempMinC;
    private int tempMaxC;
    private double phMin;
    private double phMax;
    private int ghMin;
    private int ghMax;
    private String iconName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }
    public String getBiotope() { return biotope; }
    public void setBiotope(String biotope) { this.biotope = biotope; }
    public int getTempMinC() { return tempMinC; }
    public void setTempMinC(int tempMinC) { this.tempMinC = tempMinC; }
    public int getTempMaxC() { return tempMaxC; }
    public void setTempMaxC(int tempMaxC) { this.tempMaxC = tempMaxC; }
    public double getPhMin() { return phMin; }
    public void setPhMin(double phMin) { this.phMin = phMin; }
    public double getPhMax() { return phMax; }
    public void setPhMax(double phMax) { this.phMax = phMax; }
    public int getGhMin() { return ghMin; }
    public void setGhMin(int ghMin) { this.ghMin = ghMin; }
    public int getGhMax() { return ghMax; }
    public void setGhMax(int ghMax) { this.ghMax = ghMax; }
    public String getIconName() { return iconName != null ? iconName : ""; }
    public void setIconName(String iconName) { this.iconName = iconName; }

    public String getTemperature() {
        return tempMinC + "-" + tempMaxC;
    }

    public String getPh() {
        return phMin + "-" + phMax;
    }

    public String getHardnessDGH() {
        return ghMin + "-" + ghMax;
    }
}