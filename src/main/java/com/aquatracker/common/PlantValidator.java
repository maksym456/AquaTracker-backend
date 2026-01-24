package com.aquatracker.common;

import com.aquatracker.plant.Plant;

import java.util.ArrayList;
import java.util.List;

/**
 * Walidacja parametrów roślin (temperatura, pH, twardość).
 * Używane przy tworzeniu i edycji gatunków roślin.
 */
public final class PlantValidator {

    private static final int TEMP_MIN_ALLOWED = 5;
    private static final int TEMP_MAX_ALLOWED = 35;
    private static final double PH_MIN_ALLOWED = 4.0;
    private static final double PH_MAX_ALLOWED = 10.0;
    private static final int GH_MIN_ALLOWED = 0;
    private static final int GH_MAX_ALLOWED = 35;

    private PlantValidator() {}

    /**
     * Waliduje parametry rośliny przed zapisem (create/update).
     * Zwraca listę błędów; pusta lista = OK.
     */
    public static List<String> validatePlant(Plant plant) {
        List<String> errors = new ArrayList<>();
        if (plant == null) {
            errors.add("Plant is null");
            return errors;
        }

        String name = plant.getName();
        if (name == null || name.trim().isEmpty()) {
            errors.add("Nazwa rośliny jest wymagana");
        }

        int tMin = plant.getTempMinC();
        int tMax = plant.getTempMaxC();
        if (tMin > tMax) {
            errors.add("Temperatura minimalna nie może być większa od maksymalnej");
        }
        if (tMin < TEMP_MIN_ALLOWED || tMin > TEMP_MAX_ALLOWED) {
            errors.add(String.format("Temperatura minimalna musi być w zakresie %d–%d °C", TEMP_MIN_ALLOWED, TEMP_MAX_ALLOWED));
        }
        if (tMax < TEMP_MIN_ALLOWED || tMax > TEMP_MAX_ALLOWED) {
            errors.add(String.format("Temperatura maksymalna musi być w zakresie %d–%d °C", TEMP_MIN_ALLOWED, TEMP_MAX_ALLOWED));
        }

        double phMin = plant.getPhMin();
        double phMax = plant.getPhMax();
        if (phMin > phMax) {
            errors.add("pH minimalne nie może być większe od maksymalnego");
        }
        if (phMin < PH_MIN_ALLOWED || phMin > PH_MAX_ALLOWED) {
            errors.add(String.format("pH minimalne musi być w zakresie %.1f–%.1f", PH_MIN_ALLOWED, PH_MAX_ALLOWED));
        }
        if (phMax < PH_MIN_ALLOWED || phMax > PH_MAX_ALLOWED) {
            errors.add(String.format("pH maksymalne musi być w zakresie %.1f–%.1f", PH_MIN_ALLOWED, PH_MAX_ALLOWED));
        }

        int ghMin = plant.getGhMin();
        int ghMax = plant.getGhMax();
        if (ghMin > ghMax) {
            errors.add("Twardość minimalna (dGH) nie może być większa od maksymalnej");
        }
        if (ghMin < GH_MIN_ALLOWED || ghMin > GH_MAX_ALLOWED) {
            errors.add(String.format("Twardość minimalna musi być w zakresie %d–%d °dGH", GH_MIN_ALLOWED, GH_MAX_ALLOWED));
        }
        if (ghMax < GH_MIN_ALLOWED || ghMax > GH_MAX_ALLOWED) {
            errors.add(String.format("Twardość maksymalna musi być w zakresie %d–%d °dGH", GH_MIN_ALLOWED, GH_MAX_ALLOWED));
        }

        return errors;
    }
}
