package com.aquarium.aquarium;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class AquariumValidationService {

    public List<String> validateAquarium(Aquarium aquarium) {
        List<String> warnings = new ArrayList<>();
        List<AquariumFish> fishes = aquarium.getFishInAquarium();


        for (AquariumFish entry : fishes) {
            FishSpecies species = entry.getFishSpecies();
            int count = entry.getFishCount();


            if (aquarium.getTemperatureC() < species.getTempMinC() || aquarium.getTemperatureC() > species.getTempMaxC()) {
                warnings.add("OSTRZEŻENIE: Temperatura w akwarium (" + aquarium.getTemperatureC() +
                        ") jest nieodpowiednia dla gatunku " + species.getName() +
                        " (wymagane: " + species.getTempMinC() + "-" + species.getTempMaxC() + ")");
            }


            if (count < species.getMinSchoolSize()) {
                warnings.add("OSTRZEŻENIE: Za mało ryb gatunku " + species.getName() +
                        ". Masz: " + count + ", minimalnie: " + species.getMinSchoolSize());
            }
        }

        boolean hasAggressive = fishes.stream().anyMatch(f -> "agresywne".equalsIgnoreCase(f.getFishSpecies().getTemperament()));
        boolean hasPeaceful = fishes.stream().anyMatch(f -> "spokojne".equalsIgnoreCase(f.getFishSpecies().getTemperament()));

        if (hasAggressive && hasPeaceful) {
            warnings.add("BŁĄD KRYTYCZNY: Nie można łączyć ryb agresywnych ze spokojnymi!");
        }

        return warnings;
    }
}