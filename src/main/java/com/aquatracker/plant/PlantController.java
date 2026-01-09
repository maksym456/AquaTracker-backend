package com.aquatracker.plant;

import com.aquatracker.common.ErrorResponseDto;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Profile("!dev")
@RestController
@RequestMapping("/api/v1/plants")
public class PlantController {

    private final PlantRepository plantRepository;

    public PlantController(PlantRepository plantRepository) {
        this.plantRepository = plantRepository;
    }

    @GetMapping
    public List<PlantResponseDto> getAllPlants(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String biotope,
            @RequestParam(required = false) Integer tempMin,
            @RequestParam(required = false) Integer tempMax,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset) {
        return plantRepository.findAll().stream()
                .filter(plant -> {
                    if (q != null && !q.isEmpty()) {
                        if (plant.getName() == null || !plant.getName().toLowerCase().contains(q.toLowerCase())) {
                            return false;
                        }
                    }
                    if (biotope != null && !biotope.isEmpty()) {
                        if (plant.getBiotope() == null || !plant.getBiotope().equalsIgnoreCase(biotope)) {
                            return false;
                        }
                    }
                    if (tempMin != null && plant.getTempMaxC() < tempMin) {
                        return false;
                    }
                    if (tempMax != null && plant.getTempMinC() > tempMax) {
                        return false;
                    }
                    return true;
                })
                .skip(offset != null ? offset : 0)
                .limit(limit != null ? limit : Integer.MAX_VALUE)
                .map(PlantResponseDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{plantId}")
    public ResponseEntity<?> getPlantById(@PathVariable Long plantId) {
        return plantRepository.findById(plantId)
                .map(plant -> ResponseEntity.ok(new PlantResponseDto(plant)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createPlant(@RequestBody PlantRequestDto request) {
        try {
            if (request.getName() == null || request.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Name is required"));
            }

            Plant plant = new Plant();
            plant.setName(request.getName());
            plant.setSpecies(request.getSpecies() != null ? request.getSpecies() : request.getName());
            if (request.getBiotope() != null) plant.setBiotope(request.getBiotope());
            if (request.getTempMinC() != null) plant.setTempMinC(request.getTempMinC());
            if (request.getTempMaxC() != null) plant.setTempMaxC(request.getTempMaxC());
            if (request.getPhMin() != null) plant.setPhMin(request.getPhMin());
            if (request.getPhMax() != null) plant.setPhMax(request.getPhMax());
            if (request.getGhMin() != null) plant.setGhMin(request.getGhMin());
            if (request.getGhMax() != null) plant.setGhMax(request.getGhMax());
            if (request.getLightRequirements() != null) plant.setLightRequirements(request.getLightRequirements());
            if (request.getCo2Requirements() != null) plant.setCo2Requirements(request.getCo2Requirements());
            if (request.getDifficulty() != null) plant.setDifficulty(request.getDifficulty());
            if (request.getDescription() != null) plant.setDescription(request.getDescription());
            if (request.getIconName() != null) plant.setIconName(request.getIconName());

            plant = plantRepository.save(plant);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new PlantResponseDto(plant));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create plant: " + e.getMessage()));
        }
    }

    @PutMapping("/{plantId}")
    public ResponseEntity<?> updatePlant(@PathVariable Long plantId, @RequestBody PlantRequestDto request) {
        try {
            return plantRepository.findById(plantId)
                    .map(plant -> {
                        if (request.getName() != null && !request.getName().trim().isEmpty()) {
                            plant.setName(request.getName());
                        }
                        if (request.getSpecies() != null) {
                            plant.setSpecies(request.getSpecies());
                        }
                        if (request.getBiotope() != null) {
                            plant.setBiotope(request.getBiotope());
                        }
                        if (request.getTempMinC() != null) {
                            plant.setTempMinC(request.getTempMinC());
                        }
                        if (request.getTempMaxC() != null) {
                            plant.setTempMaxC(request.getTempMaxC());
                        }
                        if (request.getPhMin() != null) {
                            plant.setPhMin(request.getPhMin());
                        }
                        if (request.getPhMax() != null) {
                            plant.setPhMax(request.getPhMax());
                        }
                        if (request.getGhMin() != null) {
                            plant.setGhMin(request.getGhMin());
                        }
                        if (request.getGhMax() != null) {
                            plant.setGhMax(request.getGhMax());
                        }
                        if (request.getLightRequirements() != null) {
                            plant.setLightRequirements(request.getLightRequirements());
                        }
                        if (request.getCo2Requirements() != null) {
                            plant.setCo2Requirements(request.getCo2Requirements());
                        }
                        if (request.getDifficulty() != null) {
                            plant.setDifficulty(request.getDifficulty());
                        }
                        if (request.getDescription() != null) {
                            plant.setDescription(request.getDescription());
                        }
                        if (request.getIconName() != null) {
                            plant.setIconName(request.getIconName());
                        }

                        plant = plantRepository.save(plant);
                        return ResponseEntity.ok(new PlantResponseDto(plant));
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update plant: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{plantId}")
    public ResponseEntity<?> deletePlant(@PathVariable Long plantId) {
        try {
            if (plantRepository.existsById(plantId)) {
                plantRepository.deleteById(plantId);
                return ResponseEntity.noContent().build(); // 204 No Content zgodnie z OpenAPI
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponseDto("InternalServerError", "Failed to delete plant: " + e.getMessage()));
        }
    }

    public static class PlantRequestDto {
        private String name;
        private String species;
        private String biotope;
        private Integer tempMinC;
        private Integer tempMaxC;
        private Double phMin;
        private Double phMax;
        private Integer ghMin;
        private Integer ghMax;
        private String lightRequirements;
        private String co2Requirements;
        private String difficulty;
        private String description;
        private String iconName;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSpecies() {
            return species;
        }

        public void setSpecies(String species) {
            this.species = species;
        }

        public String getBiotope() {
            return biotope;
        }

        public void setBiotope(String biotope) {
            this.biotope = biotope;
        }

        public Integer getTempMinC() {
            return tempMinC;
        }

        public void setTempMinC(Integer tempMinC) {
            this.tempMinC = tempMinC;
        }

        public Integer getTempMaxC() {
            return tempMaxC;
        }

        public void setTempMaxC(Integer tempMaxC) {
            this.tempMaxC = tempMaxC;
        }

        public Double getPhMin() {
            return phMin;
        }

        public void setPhMin(Double phMin) {
            this.phMin = phMin;
        }

        public Double getPhMax() {
            return phMax;
        }

        public void setPhMax(Double phMax) {
            this.phMax = phMax;
        }

        public Integer getGhMin() {
            return ghMin;
        }

        public void setGhMin(Integer ghMin) {
            this.ghMin = ghMin;
        }

        public Integer getGhMax() {
            return ghMax;
        }

        public void setGhMax(Integer ghMax) {
            this.ghMax = ghMax;
        }

        public String getLightRequirements() {
            return lightRequirements;
        }

        public void setLightRequirements(String lightRequirements) {
            this.lightRequirements = lightRequirements;
        }

        public String getCo2Requirements() {
            return co2Requirements;
        }

        public void setCo2Requirements(String co2Requirements) {
            this.co2Requirements = co2Requirements;
        }

        public String getDifficulty() {
            return difficulty;
        }

        public void setDifficulty(String difficulty) {
            this.difficulty = difficulty;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIconName() {
            return iconName;
        }

        public void setIconName(String iconName) {
            this.iconName = iconName;
        }
    }
}
