package com.aquarium.aquarium;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "plants")
public class Plant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String species;

    @ManyToMany(mappedBy = "plants")
    private Set<Aquarium> aquariums;

    // --- Gettery i Settery ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }
    public Set<Aquarium> getAquariums() { return aquariums; }
    public void setAquariums(Set<Aquarium> aquariums) { this.aquariums = aquariums; }
}