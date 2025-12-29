package com.aquatracker.plant;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Profile("dev")
@RestController
@RequestMapping("/api/v1/plants")
public class PlantMockController {

    private static final List<PlantDto> PLANTS = List.of(
            new PlantDto(
                    "1",
                    "Anubias barteri",
                    "22-28",
                    "6.0-7.5",
                    "3-15",
                    "tropical",
                    "anubias_barteri.png",
                    "Roślina wolnorosnąca, idealna na pierwszy plan i do mocowania na korzeniach/kamieniach."
            ),
            new PlantDto(
                    "2",
                    "Microsorum pteropus (Java fern)",
                    "20-28",
                    "6.0-7.5",
                    "2-15",
                    "tropical",
                    "microsorum_pteropus.png",
                    "Paproć akwariowa do mocowania na dekoracjach, odporna i łatwa w uprawie."
            ),
            new PlantDto(
                    "3",
                    "Echinodorus bleheri",
                    "22-28",
                    "6.5-7.5",
                    "3-15",
                    "amazon",
                    "echinodorus_bleheri.png",
                    "Duża roślina tła/środka akwarium, lubi żyzne podłoże i regularne nawożenie."
            ),
            new PlantDto(
                    "4",
                    "Cryptocoryne wendtii",
                    "22-28",
                    "6.0-7.5",
                    "3-18",
                    "tropical",
                    "cryptocoryne_wendtii.png",
                    "Roślina na środek akwarium, tolerancyjna; po przesadzeniu może przejściowo gubić liście."
            ),
            new PlantDto(
                    "5",
                    "Vallisneria spiralis",
                    "20-28",
                    "6.5-8.0",
                    "5-20",
                    "tropical",
                    "vallisneria_spiralis.png",
                    "Szybkorosnąca roślina tła, tworzy rozłogi; dobra do startu i stabilizacji zbiornika."
            )
    );

    @GetMapping
    public List<PlantDto> getAllPlants() {
        return PLANTS;
    }

    public record PlantDto(
            String id,
            String name,
            String temperature,
            String ph,
            String hardnessDGH,
            String biotope,
            String iconName,
            String description
    ) {}
}
