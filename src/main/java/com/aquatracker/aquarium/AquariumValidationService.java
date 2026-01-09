package com.aquatracker.aquarium;

import com.aquatracker.fish.FishSpecies;
import com.aquatracker.plant.Plant;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        // Sprawdzanie zgodności temperamentów między rybami - grupowanie konfliktów
        if (aquarium.getFishInAquarium() != null && !aquarium.getFishInAquarium().isEmpty()) {
            List<AquariumFish> fishList = aquarium.getFishInAquarium().stream()
                    .filter(af -> af.getFishSpecies() != null && af.getFishSpecies().getTemperament() != null)
                    .toList();

            // Zbierz wszystkie ryby agresywne i spokojne (dla konfliktu agresywne vs spokojne)
            Set<String> aggressiveFish = new HashSet<>();
            Set<String> peacefulFishForAggressive = new HashSet<>();
            
            // Zbierz wszystkie ryby pół-agresywne i spokojne (dla konfliktu pół-agresywne vs spokojne)
            Set<String> semiAggressiveFish = new HashSet<>();
            Set<String> peacefulFishForSemiAggressive = new HashSet<>();
            
            // Zbierz konflikty: agresywne vs spokojne i pół-agresywne vs spokojne
            for (int i = 0; i < fishList.size(); i++) {
                AquariumFish fish1 = fishList.get(i);
                FishSpecies species1 = fish1.getFishSpecies();
                String temperament1 = normalizeTemperament(species1.getTemperament());
                String name1 = species1.getName();

                for (int j = i + 1; j < fishList.size(); j++) {
                    AquariumFish fish2 = fishList.get(j);
                    FishSpecies species2 = fish2.getFishSpecies();
                    String temperament2 = normalizeTemperament(species2.getTemperament());
                    String name2 = species2.getName();

                    // Agresywne vs spokojne
                    if ((temperament1.equals("agresywne") && temperament2.equals("spokojne")) ||
                        (temperament1.equals("spokojne") && temperament2.equals("agresywne"))) {
                        if (temperament1.equals("agresywne")) {
                            aggressiveFish.add(name1);
                            peacefulFishForAggressive.add(name2);
                        } else {
                            aggressiveFish.add(name2);
                            peacefulFishForAggressive.add(name1);
                        }
                    }
                    
                    // Pół-agresywne vs spokojne
                    if ((temperament1.equals("pół-agresywne") && temperament2.equals("spokojne")) ||
                        (temperament1.equals("spokojne") && temperament2.equals("pół-agresywne"))) {
                        if (temperament1.equals("pół-agresywne")) {
                            semiAggressiveFish.add(name1);
                            peacefulFishForSemiAggressive.add(name2);
                        } else {
                            semiAggressiveFish.add(name2);
                            peacefulFishForSemiAggressive.add(name1);
                        }
                    }
                }
            }

            // Utwórz zgrupowane komunikaty dla agresywnych vs spokojnych
            if (!aggressiveFish.isEmpty() && !peacefulFishForAggressive.isEmpty()) {
                String aggressiveList = String.join(", ", aggressiveFish);
                String peacefulList = String.join(", ", peacefulFishForAggressive);
                String message = String.format("Ostrzeżenie: %s (agresywne) nie mogą być z %s (spokojne). Konflikt może spowodować pożarcie łagodnego osobnika.",
                    aggressiveList, peacefulList);
                issues.add(new AquariumStatusDto.StatusIssueDto("TEMPERAMENT_INCOMPATIBILITY", message));
                level = "WARNING";
            }

            // Utwórz zgrupowane komunikaty dla pół-agresywnych vs spokojnych
            if (!semiAggressiveFish.isEmpty() && !peacefulFishForSemiAggressive.isEmpty()) {
                String semiAggressiveList = String.join(", ", semiAggressiveFish);
                String peacefulList = String.join(", ", peacefulFishForSemiAggressive);
                String message = String.format("Ostrzeżenie: %s (pół-agresywne) nie mogą być z %s (spokojne). Konflikt może spowodować pożarcie łagodnego osobnika.",
                    semiAggressiveList, peacefulList);
                issues.add(new AquariumStatusDto.StatusIssueDto("TEMPERAMENT_INCOMPATIBILITY", message));
                if (!level.equals("WARNING")) {
                    level = "WARNING";
                }
            }
        }

        status.setLevel(level);
        status.setIssues(issues);
        status.setLastCheckedAt(LocalDateTime.now());

        return status;
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
