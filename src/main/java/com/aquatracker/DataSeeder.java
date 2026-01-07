package com.aquatracker;

import com.aquatracker.fish.FishSpecies;
import com.aquatracker.fish.FishSpeciesRepository;
import com.aquatracker.plant.Plant;
import com.aquatracker.plant.PlantRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final FishSpeciesRepository fishRepository;
    private final PlantRepository plantRepository;

    public DataSeeder(FishSpeciesRepository fishRepository, PlantRepository plantRepository) {
        this.fishRepository = fishRepository;
        this.plantRepository = plantRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (fishRepository.count() == 0) {
            List<FishSpecies> fishList = List.of(
                    new FishSpecies("Welonka (Złota rybka)", "Słodkowodna", 18, 24, "Azja", 6.0, 8.0, 5, 19, "spokojne", 1, "10-15 lat",
                            "Welonka to klasyczna, spokojna ryba akwariowa, idealna dla początkujących. Jest odporna i łatwa w utrzymaniu.",
                            "/fish/Welonka__Złota_rybka.png"),
                    new FishSpecies("Gupik (Głupik)", "Słodkowodna", 22, 28, "Ameryka Południowa", 6.8, 8.0, 10, 30, "spokojne", 5, "2-3 lata",
                            "Gupik to mała, kolorowa ryba, która najlepiej czuje się w grupie. Jest bardzo aktywna i łatwa w hodowli.",
                            "/fish/Gupik__Głupik.png"),
                    new FishSpecies("Bojownik syjamski", "Słodkowodna", 25, 30, "Azja", 6.0, 8.0, 1, 19, "pół-agresywne", 1, "3-5 lat",
                            "Bojownik syjamski to efektowna, majestatyczna ryba znana z długich, falujących płetw i intensywnych barw. Samce są terytorialne i potrafią być agresywne wobec innych samców oraz ryb o podobnych płetwach, dlatego zwykle trzyma się je pojedynczo.",
                            "/fish/Bojownik_syjamski.png"),
                    new FishSpecies("Neon Innesa", "Słodkowodna", 22, 26, "Ameryka Południowa", 6.5, 7.5, 1, 12, "spokojne", 10, "3-5 lat",
                            "Neon Innesa to drobna, energiczna ryba ławicowa, znana z intensywnego niebieskiego połysku widocznego nawet w słabym oświetleniu.",
                            "/fish/Neon_Innesa.png"),
                    new FishSpecies("Skalar (Żaglowiec)", "Słodkowodna", 25, 29, "Ameryka Południowa", 6.0, 7.4, 5, 13, "pół-agresywne", 5, "10-15 lat",
                            "Skalar (Żaglowiec) to ryba pół-agresywna, która najlepiej czuje się w grupie. Lubi dużo miejsca do pływania i rośliny, przy których może się chować. Może pokazywać dominujące zachowania wobec innych ryb, dlatego najlepiej trzymać ją z gatunkami o podobnym temperamencie.",
                            "/fish/Skalar__Żaglowiec.png"),
                    new FishSpecies("Mieczyk Hellera", "Słodkowodna", 24, 28, "Ameryka Północna", 6.0, 8.5, 10, 30, "pół-agresywne", 8, "3-5 lat",
                            "Mieczyk Hellera to żyworodna, wyrazista ryba znana z charakterystycznego 'mieczyka' na ogonie samców. Jest ruchliwa, wytrzymała i dobrze odnajduje się w większych akwariach. Choć generalnie towarzyska, potrafi wykazywać lekko dominujące zachowania, zwłaszcza samce między sobą, dlatego najlepiej trzymać ją w większej grupie.",
                            "/fish/Mieczyk_Hellera.png"),
                    new FishSpecies("Molinezja", "Słodkowodna", 24, 28, "Ameryka Południowa", 7.5, 8.5, 15, 30, "spokojne", 3, "3-5 lat",
                            "Molinezja to spokojna ryba, która najlepiej czuje się w grupie. Jest aktywna i lubi pływać wśród roślin. Jest odporna i łatwa w utrzymaniu, dobrze nadaje się do akwarium z innymi spokojnymi rybami.",
                            "/fish/Molinezja.png"),
                    new FishSpecies("Gurami mozaikowy", "Słodkowodna", 24, 28, "Azja", 5.5, 8.0, 2, 30, "spokojne", 2, "3-5 lat",
                            "Gurami mozaikowy to spokojna ryba o charakterystycznym, drobnym, mozaikowym wzorze na ciele. Porusza się powoli i często wykorzystuje wydłużone płetwy piersiowe do badania otoczenia.",
                            "/fish/Gurami_mozaikowy.png"),
                    new FishSpecies("Danio pręgowany", "Słodkowodna", 20, 25, "Azja", 6.0, 8.0, 5, 20, "spokojne", 8, "3-5 lat",
                            "Danio pręgowany to szybka, energiczna ryba ławicowa o smukłym ciele i wyraźnych, poziomych pręgach. Jest bardzo odporna i dobrze adaptuje się do różnych warunków, dzięki czemu świetnie nadaje się dla początkujących.",
                            "/fish/Danio_pręgowany.png"),
                    new FishSpecies("Kardynałek chiński", "Słodkowodna", 18, 24, "Azja", 6.0, 8.5, 5, 25, "spokojne", 6, "4-5 lat",
                            "Kardynałek chiński to niewielka, żywa i spokojna ryba o metalicznym połysku i czerwonym zabarwieniu płetw. Jest wyjątkowo odporna i dobrze czuje się nawet w chłodniejszych akwariach. W grupie prezentuje naturalne, harmonijne zachowania, tworząc efektowne mini-ławice.",
                            "/fish/Kardynałek_chiński.png"),
                    new FishSpecies("Razbora klinowa", "Słodkowodna", 24, 27, "Azja", 5.0, 7.5, 1, 12, "spokojne", 10, "5-8 lat",
                            "Razbora klinowa to spokojna ryba ławicowa, która najlepiej czuje się w grupie. Jest aktywna i porusza się wśród roślin, tworząc efektowne grupy. Lubi dobrze oświetlone akwaria z miejscami do pływania i kryjówkami.",
                            "/fish/Razbora_klinowa.png"),
                    new FishSpecies("Tęczanka neonowa", "Słodkowodna", 25, 28, "Austalia/Oceania", 6.5, 7.5, 5, 15, "spokojne", 6, "4-6 lat",
                            "Tęczanka neonowa to spokojna ryba ławicowa, która najlepiej czuje się w grupie. Ma kolorowe, metaliczne ubarwienie i lubi poruszać się wśród roślin. Jest odporna i łatwa w utrzymaniu, dobrze nadaje się do akwarium z innymi spokojnymi rybami.",
                            "/fish/Tęczanka_neonowa.png"),
                    new FishSpecies("Kirys pstry", "Słodkowodna", 23, 27, "Ameryka Południowa", 6.0, 7.0, 5, 15, "spokojne", 6, "3-5 lat",
                            "Kirysek pstry to spokojna ryba, która lubi przebywać przy dnie akwarium i chować się między roślinami. Najlepiej czuje się w grupie, wtedy porusza się naturalnie i aktywnie.",
                            "/fish/Kirys_pstry.png"),
                    new FishSpecies("Glonojad (Zbrojnik)", "Słodkowodna", 23, 28, "Ameryka Południowa", 6.5, 7.5, 1, 15, "spokojne", 1, "3-7 lat",
                            "Glonojad / Zbrojnik to spokojna ryba, która pomaga utrzymać akwarium w czystości, zjadając glony z roślin i szybów. Lubi kryjówki i spokojne miejsca w zbiorniku. Jest odporna i łatwa w utrzymaniu, dobrze nadaje się do akwarium z innymi spokojnymi rybami.",
                            "/fish/GlonojadZbrojnik-.png"),
                    new FishSpecies("Błazenek pomarańczowy", "Słonowodna", 25, 27, "Azja", 7.8, 8.4, 8, 25, "spokojne", 2, "1 rok",
                            "Błazenek pomarańczowy to spokojna ryba, która najlepiej czuje się w parze. Lubi miejsca do ukrycia, np. między skałami lub wśród korali. Jest odporna, ale wymaga stabilnych warunków wody słonowodnej i odpowiedniej temperatury.",
                            "/fish/Błazenek_pomarańczowy.png"),
                    new FishSpecies("Pirania czerwona", "Słodkowodna", 25, 30, "Ameryka Południowa", 6.0, 7.0, 0, 18, "agresywne", 5, "10-13 lat",
                            "Pirania czerwona to agresywna ryba, która najlepiej żyje w grupie. Potrzebuje dużo miejsca do pływania i odpowiedniego akwarium, aby mogła wykazywać naturalne zachowania.",
                            "/fish/Pirania_czerwona.png"),
                    new FishSpecies("Pokolec królewski", "Słonowodna", 25, 27, "Azja", 8.1, 8.5, 8, 12, "spokojne", 1, "8-12 lat",
                            "Pokolec królewski to spokojna ryba, która najlepiej trzymać pojedynczo. Lubi mieć miejsca do ukrycia, np. między skałami lub koralami. Jest odporna i może żyć długo w akwarium słonowodnym przy stabilnych warunkach wody.",
                            "/fish/Pokolec_królewski.png"),
                    new FishSpecies("Proporczykowiec", "Słodkowodna", 22, 26, "Afryka", 6.0, 7.5, 2, 10, "pół-agresywne", 4, "10-15 lat",
                            "Proporczykowiec to ryba pół-agresywna, która najlepiej czuje się w grupie. Lubi mieć kryjówki i miejsca do pływania. Może wykazywać dominujące zachowania wobec innych ryb, dlatego najlepiej trzymać ją z gatunkami o podobnym temperamencie.",
                            "/fish/Proporczykowiec.png"),
                    new FishSpecies("Pyszczak (Malawi)", "Słodkowodna", 25, 28, "Afryka", 7.6, 8.8, 10, 25, "agresywne", 1, "8-10 lat",
                            "Pyszczak (Malawi) to agresywna ryba, która najlepiej czuje się w swoim terytorium. Lubi mieć kryjówki i przestrzeń do pływania.",
                            "/fish/Pyszczak__Malawi.png"),
                    new FishSpecies("Księżniczka z Burundi", "Słodkowodna", 24, 28, "Afryka", 7.5, 9.0, 9, 19, "agresywne", 6, "5-10 lat",
                            "Księżniczka z Burundi to agresywna ryba, która najlepiej czuje się w grupie. Lubi mieć kryjówki i dużo miejsca do pływania.",
                            "/fish/Księżniczka_z_Burundi.png"),
                    new FishSpecies("Kolcobrzuch karłowaty", "Słodkowodna", 24, 28, "Azja", 6.8, 8.0, 5, 25, "agresywne", 1, "2-3 lata",
                            "Kolcobrzuch karłowaty to agresywna ryba, która najlepiej trzymać pojedynczo. Ma mocny charakter i potrafi bronić swojego terytorium. Lubi kryjówki i miejsca do ukrycia. Jest odporna, ale wymaga stabilnych warunków wody.",
                            "/fish/Kolcobrzuch_karłowaty.png"),
                    new FishSpecies("Mandaryn wspaniały", "Słonowodna", 25, 27, "Azja", 8.1, 8.4, 4, 16, "spokojne", 1, "4-5 lat",
                            "Mandaryn wspaniały to spokojna ryba, która najlepiej trzymać pojedynczo. Lubi miejsca do ukrycia i rośliny lub koralowce, w których może się poruszać. Jest wrażliwa na warunki wody, dlatego wymaga stabilnego akwarium słonowodnego.",
                            "/fish/Mandaryn_wspaniały.png"),
                    new FishSpecies("Ustnik słoneczny", "Słonowodna", 24, 27, "Azja", 8.1, 8.3, 5, 15, "spokojne", 1, "8-10 lat",
                            "Ustnik słoneczny to spokojna ryba, którą najlepiej trzymać pojedynczo. Lubi mieć miejsca do ukrycia, np. między skałami lub koralami. Jest odporna i może żyć długo w akwarium słonowodnym przy stabilnych warunkach wody.",
                            "/fish/Ustnik_żółty_ryba.png"),
                    new FishSpecies("Babka złota", "Słodkowodna", 24, 28, "Azja", 6.5, 7.5, 5, 15, "spokojne", 6, "4-6 lat",
                            "Babka złota to spokojna ryba, która najlepiej czuje się w grupie. Jest aktywna i lubi pływać wśród roślin oraz kryjówek. Jest odporna i łatwa w utrzymaniu, dobrze nadaje się do akwarium z innymi spokojnymi rybami.",
                            "/fish/Babka_złota.png")
            );

            fishRepository.saveAll(fishList);
            System.out.println("--- BAZA DANYCH RYB ZOSTAŁA ZASILONA DANYMI ---");
        }

        // Seeder roślin
        if (plantRepository.count() == 0) {
            List<Plant> plantList = List.of(
                    new Plant("Nurzaniec", "Nurzaniec", "Afryka, Azja, Europa", 20, 28, 6.8, 9.5, 5, 15, "bez znaczenia", "CO2", "łatwa"),
                    new Plant("Anubias", "Anubias", "Afryka Zachodnia", 22, 28, 6.0, 8.0, 2, 12, "słabe", "CO2", "średnia"),
                    new Plant("Mech Jawajski", "Mech Jawajski", "Azja", 15, 27, 5.0, 8.0, 2, 30, "słabe", "brak", "łatwa"),
                    new Plant("Ludwigia", "Ludwigia", "Ameryka Północna", 18, 28, 5.5, 8.0, 2, 15, "umiarkowane", "brak", "łatwa"),
                    new Plant("Rogatek", "Rogatek", "Ameryka Południowa, Północna", 15, 30, 5.0, 8.0, 3, 15, "słabe", "brak", "łatwa"),
                    new Plant("Kryptokoryna", "Kryptokoryna", "Azja", 22, 30, 6.5, 8.0, 2, 15, "umiarkowane", "Fe", "łatwa"),
                    new Plant("Lotos tygrysi", "Lotos tygrysi", "Afryka, Azja", 22, 28, 5.5, 7.5, 2, 10, "umiarkowane", "brak", "łatwa"),
                    new Plant("Żabienica", "Żabienica", "Ameryka Południowa", 22, 28, 6.0, 7.5, 2, 15, "umiarkowane", "CO2", "trudna"),
                    new Plant("Limnofila", "Limnofila", "Azja", 22, 26, 6.0, 7.0, 3, 14, "mocne", "CO2, Fe", "średnia"),
                    new Plant("Rotala", "Rotala", "Azja", 18, 28, 5.5, 7.5, 2, 15, "mocne", "Fe, P, NO3", "łatwa"),
                    new Plant("Duży Heniek", "Duży Heniek", "Ameryka Północna", 20, 26, 5.5, 8.0, 4, 18, "mocne", "CO2", "średnia"),
                    new Plant("Ponikło maleńkie", "Ponikło maleńkie", "Wszędzie", 19, 28, 5.5, 8.0, 2, 12, "bez znaczenia", "CO2", "łatwa"),
                    new Plant("Gałęzatka Kulista", "Gałęzatka Kulista", "Azja, Europa", 19, 28, 6.0, 8.5, 2, 12, "bez znaczenia", "CO2", "łatwa"),
                    new Plant("Heniek Mały", "Heniek Mały", "Ameryka Północna", 20, 28, 5.0, 8.0, 1, 15, "mocne", "CO2, Fe", "średnia"),
                    new Plant("Monte Carlo", "Monte Carlo", "Ameryka Południowa", 21, 28, 6.0, 7.5, 4, 20, "mocne", "CO2", "średnia")
            );

            plantRepository.saveAll(plantList);
            System.out.println("--- BAZA DANYCH ROŚLIN ZOSTAŁA ZASILONA DANYMI ---");
        }
    }
}
