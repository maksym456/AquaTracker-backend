package com.aquatracker.sharing;

import com.aquatracker.aquarium.Aquarium;
import com.aquatracker.user.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "aquarium_shares", uniqueConstraints = @UniqueConstraint(columnNames = {"aquarium_id", "user_id"}))
public class AquariumShare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aquarium_id", nullable = false)
    private Aquarium aquarium;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "permission_level", nullable = false)
    private String permissionLevel; // "read", "write", "admin"

    @Column(name = "shared_by")
    private Long sharedBy; // User ID who shared the aquarium

    @Column(name = "shared_at", nullable = false)
    private LocalDateTime sharedAt;

    // Constructors
    public AquariumShare() {}

    public AquariumShare(Aquarium aquarium, User user, String permissionLevel, Long sharedBy) {
        this.aquarium = aquarium;
        this.user = user;
        this.permissionLevel = permissionLevel;
        this.sharedBy = sharedBy;
        this.sharedAt = LocalDateTime.now();
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

    public String getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(String permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    public Long getSharedBy() {
        return sharedBy;
    }

    public void setSharedBy(Long sharedBy) {
        this.sharedBy = sharedBy;
    }

    public LocalDateTime getSharedAt() {
        return sharedAt;
    }

    public void setSharedAt(LocalDateTime sharedAt) {
        this.sharedAt = sharedAt;
    }
}

