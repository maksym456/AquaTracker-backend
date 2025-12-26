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

        status.setLevel(level);
        status.setIssues(issues);
        status.setLastCheckedAt(LocalDateTime.now());

        return status;
    }
}
