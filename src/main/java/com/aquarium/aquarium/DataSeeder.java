package com.aquarium.aquarium;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final FishSpeciesRepository fishRepository;

    public DataSeeder(FishSpeciesRepository fishRepository) {
        this.fishRepository = fishRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Sprawdzamy, czy baza jest pusta. Jeśli tak, ładujemy dane.
        if (fishRepository.count() == 0) {
            List<FishSpecies> fishList = List.of(
                    new FishSpecies("Neon Innesa", "Słodkowodna", 22, 26, "Ameryka Południowa", 6.5, 7.5, 1, 12, "spokojne", 10, "3-5 lat"),
                    new FishSpecies("Gupik (Głupik)", "Słodkowodna", 24, 28, "Ameryka Południowa", 6.0, 8.0, 10, 30, "spokojne", 5, "2-3 lata"),
                    new FishSpecies("Mieczyk Hellera", "Słodkowodna", 24, 28, "Ameryka Północna", 6.0, 8.5, 10, 30, "pół-agresywne", 8, "3-5 lat"),
                    new FishSpecies("Danio pręgowany", "Słodkowodna", 20, 25, "Azja", 6.0, 8.0, 5, 20, "spokojne", 8, "3-5 lat"),
                    new FishSpecies("Kardynałek chiński", "Słodkowodna", 18, 24, "Azja", 6.0, 8.5, 5, 25, "spokojne", 6, "4-5 lat"),
                    new FishSpecies("Bojownik syjamski", "Słodkowodna", 25, 30, "Azja", 6.0, 8.0, 1, 19, "pół-agresywne", 1, "3-5 lat"),
                    new FishSpecies("Gurami mozaikowy", "Słodkowodna", 24, 28, "Azja", 5.5, 8.0, 2, 30, "spokojne", 2, "3-5 lat"),
                    new FishSpecies("Kirysek pstry", "Słodkowodna", 23, 27, "Ameryka Południowa", 6.0, 7.0, 5, 15, "spokojne", 6, "3-5 lat"),
                    new FishSpecies("Glonojad / Zbrojnik", "Słodkowodna", 23, 28, "Ameryka Południowa", 6.5, 7.5, 1, 15, "spokojne", 1, "3-7 lat"),
                    new FishSpecies("Pyszczak (Malawi)", "Słodkowodna", 25, 28, "Afryka", 7.6, 8.8, 10, 25, "agresywne", 1, "8-10 lat"),
                    new FishSpecies("Pirania czerwona", "Słodkowodna", 25, 30, "Ameryka Południowa", 6.0, 7.0, 0, 18, "agresywne", 5, "10-13 lat"),
                    new FishSpecies("Księżniczka z Burundi", "Słodkowodna", 24, 28, "Afryka", 7.5, 9.0, 9, 19, "agresywne", 6, "5-10 lat"),
                    new FishSpecies("Welonka (Złota rybka)", "Słodkowodna", 18, 22, "Azja", 6.0, 7.5, 5, 20, "spokojne", 2, "5-8 lat"),
                    new FishSpecies("Razbora klinowa", "Słodkowodna", 24, 27, "Azja", 5.0, 7.5, 1, 12, "spokojne", 10, "5-8 lat"),
                    new FishSpecies("Skalar (Żaglowiec)", "Słodkowodna", 25, 29, "Ameryka Południowa", 6.0, 7.4, 5, 13, "pół-agresywne", 5, "10-15 lat"),
                    new FishSpecies("Tęczanka neonowa", "Słodkowodna", 25, 28, "Australia/Oceania", 6.5, 7.5, 5, 15, "spokojne", 6, "4-6 lat"),
                    new FishSpecies("Proporczykowiec", "Słodkowodna", 22, 26, "Afryka", 6.0, 7.5, 2, 10, "pół-agresywne", 4, "10-15 lat"),
                    new FishSpecies("Molinezja", "Słodkowodna", 24, 28, "Ameryka Południowa", 7.5, 8.5, 15, 30, "spokojne", 3, "3-5 lat"),
                    new FishSpecies("Kolcobrzuch karłowaty", "Słodkowodna", 24, 28, "Azja", 6.8, 8.0, 5, 25, "agresywne", 1, "2-3 lata"),
                    new FishSpecies("Babka złota", "Słodkowodna", 24, 28, "Azja", 7.0, 8.5, 8, 20, "spokojne", 6, "2-3 lata"),
                    new FishSpecies("Błazenek pomarańczowy", "Słonowodna", 25, 27, "Azja", 7.8, 8.4, 8, 25, "spokojne", 2, "1 rok"),
                    new FishSpecies("Pokolec królewski", "Słonowodna", 25, 27, "Azja", 8.1, 8.5, 8, 12, "spokojne", 1, "8-12 lat"),
                    new FishSpecies("Mandaryn wspaniały", "Słonowodna", 25, 27, "Azja", 8.1, 8.4, 4, 16, "spokojne", 1, "4-5 lat"),
                    new FishSpecies("Ustnik słoneczny", "Słonowodna", 24, 27, "Azja", 8.1, 8.3, 5, 15, "spokojne", 1, "8-10 lat")
            );

            fishRepository.saveAll(fishList);
            System.out.println("--- BAZA DANYCH AWS ZOSTAŁA ZASILONA DANYMI Z EXCELA ---");
        }
    }
}