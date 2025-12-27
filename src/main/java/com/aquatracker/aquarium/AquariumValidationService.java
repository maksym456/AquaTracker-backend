package com.aquatracker.aquarium;

import com.aquatracker.fish.FishSpecies;
import com.aquatracker.plant.Plant;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AquariumValidationService {

    public AquariumStatusDto validateAquarium(Aquarium aquarium) {
        AquariumStatusDto status = new AquariumStatusDto();
        List<AquariumStatusDto.StatusIssueDto> issues = new ArrayList<>();
        String level = "OK";

        if (aquarium.getFishInAquarium() != null && !aquarium.getFishInAquarium().isEmpty()) {
            for (AquariumFish aquariumFish : aquarium.getFishInAquarium()) {
                if (aquariumFish.getFishSpecies() == null) {
                    continue;
                }

                FishSpecies fish = aquariumFish.getFishSpecies();
                int fishCount = aquariumFish.getFishCount();

                double temp = aquarium.getTemperatureC();
                if (temp > 0 && (temp < fish.getTempMinC() || temp > fish.getTempMaxC())) {
                    issues.add(new AquariumStatusDto.StatusIssueDto(
                        "TEMPERATURE_OUT_OF_RANGE",
                        String.format("Temperatura %.1f°C poza zakresem dla %s (%d-%d).",
                            temp, fish.getName(), fish.getTempMinC(), fish.getTempMaxC())
                    ));
                    level = "WARNING";
                }

                if (aquarium.getPh() != null) {
                    double ph = aquarium.getPh();
                    if (ph < fish.getPhMin() || ph > fish.getPhMax()) {
                        issues.add(new AquariumStatusDto.StatusIssueDto(
                            "PH_OUT_OF_RANGE",
                            String.format("pH %.2f poza zakresem dla %s (%.1f-%.1f).",
                                ph, fish.getName(), fish.getPhMin(), fish.getPhMax())
                        ));
                        level = "WARNING";
                    }
                }

                if (aquarium.getHardnessDGH() != null) {
                    int hardness = aquarium.getHardnessDGH();
                    if (hardness < fish.getGhMin() || hardness > fish.getGhMax()) {
                        issues.add(new AquariumStatusDto.StatusIssueDto(
                            "HARDNESS_OUT_OF_RANGE",
                            String.format("Twardość %d dGH poza zakresem dla %s (%d-%d).",
                                hardness, fish.getName(), fish.getGhMin(), fish.getGhMax())
                        ));
                        level = "WARNING";
                    }
                }

                if (fish.getMinShoalSize() > 0 && fishCount < fish.getMinShoalSize()) {
                    issues.add(new AquariumStatusDto.StatusIssueDto(
                        "INSUFFICIENT_GROUP_SIZE",
                        String.format("Zbyt mała liczebność stada %s (min. %d, jest %d).",
                            fish.getName(), fish.getMinShoalSize(), fishCount)
                    ));
                    level = "WARNING";
                }

                if (fish.getWaterType() != null && aquarium.getWaterType() != null) {
                    if (!fish.getWaterType().equals(aquarium.getWaterType())) {
                        issues.add(new AquariumStatusDto.StatusIssueDto(
                            "WATER_TYPE_MISMATCH",
                            String.format("Niezgodność typu wody: akwarium %s, ryba %s wymaga %s.",
                                aquarium.getWaterType(), fish.getName(), fish.getWaterType())
                        ));
                        level = "WARNING";
                    }
                }
            }
        }

        if (aquarium.getPlantsInAquarium() != null && !aquarium.getPlantsInAquarium().isEmpty()) {
            for (AquariumPlant aquariumPlant : aquarium.getPlantsInAquarium()) {
                if (aquariumPlant.getPlant() == null) {
                    continue;
                }

                Plant plant = aquariumPlant.getPlant();

                double temp = aquarium.getTemperatureC();
                if (temp > 0 && plant.getTempMinC() > 0 && plant.getTempMaxC() > 0) {
                    if (temp < plant.getTempMinC() || temp > plant.getTempMaxC()) {
                        issues.add(new AquariumStatusDto.StatusIssueDto(
                            "PLANT_TEMPERATURE_OUT_OF_RANGE",
                            String.format("Temperatura %.1f°C poza zakresem dla rośliny %s (%d-%d).",
                                temp, plant.getName(), plant.getTempMinC(), plant.getTempMaxC())
                        ));
                        level = "WARNING";
                    }
                }

                if (aquarium.getPh() != null && plant.getPhMin() > 0 && plant.getPhMax() > 0) {
                    double ph = aquarium.getPh();
                    if (ph < plant.getPhMin() || ph > plant.getPhMax()) {
                        issues.add(new AquariumStatusDto.StatusIssueDto(
                            "PLANT_PH_OUT_OF_RANGE",
                            String.format("pH %.2f poza zakresem dla rośliny %s (%.1f-%.1f).",
                                ph, plant.getName(), plant.getPhMin(), plant.getPhMax())
                        ));
                        level = "WARNING";
                    }
                }
            }
        }

        // Sprawdzanie zgodności temperamentów między rybami
        if (aquarium.getFishInAquarium() != null && !aquarium.getFishInAquarium().isEmpty()) {
            List<AquariumFish> fishList = aquarium.getFishInAquarium().stream()
                    .filter(af -> af.getFishSpecies() != null && af.getFishSpecies().getTemperament() != null)
                    .toList();

            for (int i = 0; i < fishList.size(); i++) {
                AquariumFish fish1 = fishList.get(i);
                FishSpecies species1 = fish1.getFishSpecies();
                String temperament1 = species1.getTemperament();
                Long speciesId1 = species1.getId();

                for (int j = i + 1; j < fishList.size(); j++) {
                    AquariumFish fish2 = fishList.get(j);
                    FishSpecies species2 = fish2.getFishSpecies();
                    String temperament2 = species2.getTemperament();
                    Long speciesId2 = species2.getId();

                    // Sprawdzenie zgodności temperamentów
                    String compatibilityIssue = checkTemperamentCompatibility(
                            temperament1, temperament2, speciesId1, speciesId2, species1.getName(), species2.getName());
                    
                    if (compatibilityIssue != null) {
                        issues.add(new AquariumStatusDto.StatusIssueDto(
                            "TEMPERAMENT_INCOMPATIBILITY",
                            compatibilityIssue
                        ));
                        
                        // Ustawienie poziomu na ERROR jeśli nieprawidłowe (najwyższy priorytet), WARNING jeśli ostrzeżenie
                        if (compatibilityIssue.contains("nieprawidłowe") || compatibilityIssue.contains("Nieprawidłowe")) {
                            level = "ERROR";
                        } else if (!level.equals("ERROR")) {
                            level = "WARNING";
                        }
                    }
                }
            }
        }

        status.setLevel(level);
        status.setIssues(issues);
        status.setLastCheckedAt(LocalDateTime.now());

        return status;
    }

    /**
     * Sprawdza zgodność temperamentów między dwoma gatunkami ryb.
     * Zwraca komunikat błędu/ostrzeżenia lub null jeśli wszystko OK.
     */
    private String checkTemperamentCompatibility(String temp1, String temp2, Long speciesId1, Long speciesId2,
                                                   String name1, String name2) {
        // Normalizacja nazw temperamentów (obsługa różnych wariantów)
        String t1 = normalizeTemperament(temp1);
        String t2 = normalizeTemperament(temp2);

        // Wszystkie spokojne mogą ze sobą nawzajem być
        if (t1.equals("spokojne") && t2.equals("spokojne")) {
            return null; // OK
        }

        // Agresywne nie mogą być ani ze spokojnymi ani z półagresywnymi
        if (t1.equals("agresywne") || t2.equals("agresywne")) {
            if (t1.equals("spokojne") || t2.equals("spokojne")) {
                return String.format("Nieprawidłowe: %s (agresywne) nie może być z %s (spokojne).", 
                    t1.equals("agresywne") ? name1 : name2,
                    t1.equals("spokojne") ? name1 : name2);
            }
            if (t1.equals("pół-agresywne") || t2.equals("pół-agresywne")) {
                return String.format("Nieprawidłowe: %s (agresywne) nie może być z %s (pół-agresywne).", 
                    t1.equals("agresywne") ? name1 : name2,
                    t1.equals("pół-agresywne") ? name1 : name2);
            }
            // Agresywne z agresywnymi spoza swojego gatunku wymagają dodatkowego sprawdzenia
            if (t1.equals("agresywne") && t2.equals("agresywne") && !speciesId1.equals(speciesId2)) {
                return String.format("Ostrzeżenie: %s i %s (oba agresywne, różne gatunki) wymagają dodatkowego sprawdzenia.", 
                    name1, name2);
            }
        }

        // Półagresywne ze spokojnymi wymagają dodatkowego sprawdzenia
        if ((t1.equals("pół-agresywne") && t2.equals("spokojne")) || 
            (t1.equals("spokojne") && t2.equals("pół-agresywne"))) {
            return String.format("Ostrzeżenie: %s (pół-agresywne) z %s (spokojne) wymaga dodatkowego sprawdzenia.", 
                t1.equals("pół-agresywne") ? name1 : name2,
                t1.equals("spokojne") ? name1 : name2);
        }

        // Półagresywne ze półagresywnymi spoza swojego gatunku wymagają dodatkowego sprawdzenia
        if (t1.equals("pół-agresywne") && t2.equals("pół-agresywne") && !speciesId1.equals(speciesId2)) {
            return String.format("Ostrzeżenie: %s i %s (oba pół-agresywne, różne gatunki) wymagają dodatkowego sprawdzenia.", 
                name1, name2);
        }

        return null; // OK
    }

    /**
     * Normalizuje nazwę temperamentu do standardowej formy.
     */
    private String normalizeTemperament(String temperament) {
        if (temperament == null) {
            return "spokojne";
        }
        String normalized = temperament.toLowerCase().trim();
        if (normalized.contains("pół") || normalized.contains("pol") || normalized.contains("semi")) {
            return "pół-agresywne";
        }
        if (normalized.contains("agresywne") || normalized.contains("aggressive")) {
            return "agresywne";
        }
        return "spokojne";
    }
}
