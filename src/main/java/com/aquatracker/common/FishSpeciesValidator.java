package com.aquatracker.common;

import com.aquatracker.fish.FishSpecies;

import java.util.ArrayList;
import java.util.List;

/**
 * Walidacja parametrów gatunków ryb (temperatura, pH, twardość, stado, długość życia).
 * Używane przy tworzeniu, edycji oraz przy dodawaniu ryby do akwarium.
 */
public final class FishSpeciesValidator {

    private static final int TEMP_MIN_ALLOWED = 5;
    private static final int TEMP_MAX_ALLOWED = 40;
    private static final double PH_MIN_ALLOWED = 4.0;
    private static final double PH_MAX_ALLOWED = 10.0;
    private static final int GH_MIN_ALLOWED = 0;
    private static final int GH_MAX_ALLOWED = 35;

    private FishSpeciesValidator() {}

    /**
     * Waliduje parametry ryby przed zapisem (create/update).
     * Zwraca listę błędów; pusta lista = OK.
     */
    public static List<String> validateFishSpecies(FishSpecies fish) {
        List<String> errors = new ArrayList<>();
        if (fish == null) {
            errors.add("Fish is null");
            return errors;
        }

        String name = fish.getName();
        if (name == null || name.trim().isEmpty()) {
            errors.add("Nazwa gatunku jest wymagana");
        }

        int tMin = fish.getTempMinC();
        int tMax = fish.getTempMaxC();
        if (tMin > tMax) {
            errors.add("Temperatura minimalna nie może być większa od maksymalnej");
        }
        if (tMin < TEMP_MIN_ALLOWED || tMin > TEMP_MAX_ALLOWED) {
            errors.add(String.format("Temperatura minimalna musi być w zakresie %d–%d °C", TEMP_MIN_ALLOWED, TEMP_MAX_ALLOWED));
        }
        if (tMax < TEMP_MIN_ALLOWED || tMax > TEMP_MAX_ALLOWED) {
            errors.add(String.format("Temperatura maksymalna musi być w zakresie %d–%d °C", TEMP_MIN_ALLOWED, TEMP_MAX_ALLOWED));
        }

        double phMin = fish.getPhMin();
        double phMax = fish.getPhMax();
        if (phMin > phMax) {
            errors.add("pH minimalne nie może być większe od maksymalnego");
        }
        if (phMin < PH_MIN_ALLOWED || phMin > PH_MAX_ALLOWED) {
            errors.add(String.format("pH minimalne musi być w zakresie %.1f–%.1f", PH_MIN_ALLOWED, PH_MAX_ALLOWED));
        }
        if (phMax < PH_MIN_ALLOWED || phMax > PH_MAX_ALLOWED) {
            errors.add(String.format("pH maksymalne musi być w zakresie %.1f–%.1f", PH_MIN_ALLOWED, PH_MAX_ALLOWED));
        }

        int ghMin = fish.getGhMin();
        int ghMax = fish.getGhMax();
        if (ghMin > ghMax) {
            errors.add("Twardość minimalna (dGH) nie może być większa od maksymalnej");
        }
        if (ghMin < GH_MIN_ALLOWED || ghMin > GH_MAX_ALLOWED) {
            errors.add(String.format("Twardość minimalna musi być w zakresie %d–%d °dGH", GH_MIN_ALLOWED, GH_MAX_ALLOWED));
        }
        if (ghMax < GH_MIN_ALLOWED || ghMax > GH_MAX_ALLOWED) {
            errors.add(String.format("Twardość maksymalna musi być w zakresie %d–%d °dGH", GH_MIN_ALLOWED, GH_MAX_ALLOWED));
        }

        int minSchool = fish.getMinSchoolSize();
        if (minSchool < 1) {
            errors.add("Minimalna liczebność stada musi być nie mniejsza niż 1");
        }

        String lifespan = fish.getLifespan();
        if (lifespan == null || lifespan.trim().isEmpty()) {
            errors.add("Długość życia jest wymagana");
        }

        return errors;
    }
}
