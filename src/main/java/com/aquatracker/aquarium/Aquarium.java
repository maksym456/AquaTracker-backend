package com.aquatracker.aquarium;

import com.aquatracker.user.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "aquariums")
public class Aquarium {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int volumeLiters;
    private double temperatureC;
    private String waterType;
    private String biotope;
    private Double ph;
    private Integer hardness;
    private Integer hardnessDGH;
    private String description;
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User owner;

    @OneToMany(mappedBy = "aquarium", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AquariumPlant> plantsInAquarium = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "aquarium", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AquariumFish> fishInAquarium = new java.util.ArrayList<>();

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

    public int getVolumeLiters() {
        return volumeLiters;
    }

    public void setVolumeLiters(int volumeLiters) {
        this.volumeLiters = volumeLiters;
    }

    public double getTemperatureC() {
        return temperatureC;
    }

    public void setTemperatureC(double temperatureC) {
        this.temperatureC = temperatureC;
    }

    public String getWaterType() {
        return waterType;
    }

    public void setWaterType(String waterType) {
        this.waterType = waterType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public List<AquariumPlant> getPlantsInAquarium() {
        return plantsInAquarium;
    }

    public void setPlantsInAquarium(List<AquariumPlant> plantsInAquarium) {
        this.plantsInAquarium = plantsInAquarium;
    }

    public Integer getHardnessDGH() {
        return hardnessDGH != null ? hardnessDGH : hardness;
    }

    public void setHardnessDGH(Integer hardnessDGH) {
        this.hardnessDGH = hardnessDGH;
        if (hardness == null) {
            this.hardness = hardnessDGH;
        }
    }

    public List<AquariumFish> getFishInAquarium() {
        return fishInAquarium;
    }

    public void setFishInAquarium(List<AquariumFish> fishInAquarium) {
        this.fishInAquarium = fishInAquarium;
    }

    public String getBiotope() {
        return biotope;
    }

    public void setBiotope(String biotope) {
        this.biotope = biotope;
    }

    public Double getPh() {
        return ph;
    }

    public void setPh(Double ph) {
        this.ph = ph;
    }

    public Integer getHardness() {
        return hardness;
    }

    public void setHardness(Integer hardness) {
        this.hardness = hardness;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
