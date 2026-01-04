package com.aquatracker.history;

import com.aquatracker.aquarium.Aquarium;
import com.aquatracker.user.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "aquarium_parameter_history")
public class AquariumParameterHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aquarium_id", nullable = false)
    private Aquarium aquarium;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "parameter_name", nullable = false)
    private String parameterName; // temperature, ph, hardness, volume, water_type, etc.

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Constructors
    public AquariumParameterHistory() {}

    public AquariumParameterHistory(Aquarium aquarium, User user, String parameterName, String oldValue, String newValue) {
        this.aquarium = aquarium;
        this.user = user;
        this.parameterName = parameterName;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.changedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Aquarium getAquarium() {
        return aquarium;
    }

    public void setAquarium(Aquarium aquarium) {
        this.aquarium = aquarium;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

