package com.aquatracker.fish;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Profile("dev")
@Validated
@RestController
@RequestMapping("/api/v1/fish")
public class FishMockController {

    private final Map<Long, Fish> store = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    @PostConstruct
    void init() {
        seed(new Fish(null, "Welonka (Złota rybka)", "Słodkowodna", "18-22", "temperate", "7.0-8.0", "5-19",
                "spokojne", 1, "10-15 lat", "Welonka__Złota_rybka.png"));
        seed(new Fish(null, "Gupik (Głupik)", "Słodkowodna", "22-28", "tropical", "6.8-7.8", "8-20",
                "spokojne", 6, "2-3 lata", "Gupik__Głupik.png"));
        seed(new Fish(null, "Neon Innesa", "Słodkowodna", "20-26", "amazon", "5.0-7.0", "1-10",
                "spokojne", 10, "3-5 lat", "Neon_Innesa.png"));
        seed(new Fish(null, "Błazenek pomarańczowy", "Słonowodna", "24-27", "coralReef", "8.1-8.4", "8-12",
                "pół-agresywne", 2, "6-10 lat", "Błazenek_pomarańczowy.png"));
        seed(new Fish(null, "Pokolec królewski", "Słonowodna", "24-28", "coralReef", "8.1-8.4", "8-12",
                "umiarkowane", 1, "8-12 lat", "Pokolec_królewski.png"));
    }

    private void seed(Fish fishWithoutId) {
        long id = nextId.getAndIncrement();
        Fish fish = new Fish(
                id,
                fishWithoutId.name(),
                fishWithoutId.waterType(),
                fishWithoutId.temperature(),
                fishWithoutId.biotope(),
                fishWithoutId.ph(),
                fishWithoutId.hardnessDGH(),
                fishWithoutId.temperament(),
                fishWithoutId.minShoalSize(),
                fishWithoutId.lifeSpan(),
                fishWithoutId.iconName()
        );
        store.put(id, fish);
    }

    @GetMapping
    public List<Fish> listFish() {
        return store.values().stream()
                .sorted(Comparator.comparing(Fish::id))
                .toList();
    }

    @GetMapping("/{fishId}")
    public ResponseEntity<Fish> getFish(@PathVariable Long fishId) {
        Fish fish = store.get(fishId);
        return fish == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(fish);
    }

    @PostMapping
    public ResponseEntity<Fish> addFish(@Valid @RequestBody FishCreateRequest req) {
        long id = nextId.getAndIncrement();

        Fish created = new Fish(
                id,
                req.name(),
                req.waterType(),
                req.temperature(),
                req.biotope(),
                req.ph(),
                req.hardnessDGH(),
                req.temperament(),
                req.minShoalSize(),
                req.lifeSpan(),
                req.iconName()
        );

        store.put(id, created);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @DeleteMapping("/{fishId}")
    public ResponseEntity<Void> deleteFish(@PathVariable Long fishId) {
        return store.remove(fishId) == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.noContent().build();
    }

    public record Fish(
            Long id,
            String name,
            String waterType,
            String temperature,
            String biotope,
            String ph,
            String hardnessDGH,
            String temperament,
            Integer minShoalSize,
            String lifeSpan,
            String iconName
    ) {}

    public record FishCreateRequest(
            @NotBlank String name,
            @NotBlank String waterType,
            @NotBlank String temperature,
            @NotBlank String biotope,
            @NotBlank String ph,
            @NotBlank String hardnessDGH,
            @NotBlank String temperament,
            @NotNull @Positive Integer minShoalSize,
            @NotBlank String lifeSpan,
            @NotBlank String iconName
    ) {}
}
