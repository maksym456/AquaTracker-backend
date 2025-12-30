package com.aquatracker.aquarium;

import com.aquatracker.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "aquariums")
public class Aquarium {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @NotBlank(message = "Nazwa akwarium jest wymagana")
    private String name;
    
    @Column(name = "volume_liters")
    @Min(value = 1, message = "Objętość musi być większa niż 0 litrów")
    private int volumeLiters;
    
    @Column(name = "temperaturec")
    @DecimalMin(value = "15.0", message = "Temperatura zbyt niska dla większości ryb")
    @DecimalMax(value = "32.0", message = "Temperatura zbyt wysoka")
    private double temperatureC;
    
    @Column(name = "water_type")
    private String waterType;
    
    @Column(name = "biotope")
    private String biotope;
    
    @Column(name = "ph")
    @DecimalMin(value = "5.0", message = "pH zbyt niskie")
    @DecimalMax(value = "9.0", message = "pH zbyt wysokie")
    private Double ph;
    
    @Column(name = "hardness")
    private Integer hardness;
    
    @Column(name = "hardness_dgh")
    private Integer hardnessDGH;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "created_at")
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
