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
                    new FishSpecies("Welonka", "Słodkowodna", 18, 24, "Azja", 6.0, 8.0, 5, 19, "spokojne", 1, "10-15 lat",
                            "Welonka to klasyczna, spokojna ryba akwariowa, idealna dla początkujących. Jest odporna i łatwa w utrzymaniu.",
                            "/fish/Welonka__Złota_rybka.png"),
                    new FishSpecies("Gupik", "Słodkowodna", 22, 28, "Ameryka Południowa", 6.8, 8.0, 10, 30, "spokojne", 5, "2-3 lata",
                            "Gupik to mała, kolorowa ryba, która najlepiej czuje się w grupie. Jest bardzo aktywna i łatwa w hodowli.",
                            "/fish/Gupik__Głupik.png"),
                    new FishSpecies("Bojownik syjamski", "Słodkowodna", 25, 30, "Azja", 6.0, 8.0, 1, 19, "pół-agresywne", 1, "3-5 lat",
                            "Bojownik syjamski to efektowna, majestatyczna ryba znana z długich, falujących płetw i intensywnych barw. Samce są terytorialne i potrafią być agresywne wobec innych samców oraz ryb o podobnych płetwach, dlatego zwykle trzyma się je pojedynczo.",
                            "/fish/Bojownik_syjamski.png"),
                    new FishSpecies("Neon Innesa", "Słodkowodna", 22, 26, "Ameryka Południowa", 6.5, 7.5, 1, 12, "spokojne", 10, "3-5 lat",
                            "Neon Innesa to drobna, energiczna ryba ławicowa, znana z intensywnego niebieskiego połysku widocznego nawet w słabym oświetleniu.",
                            "/fish/Neon_Innesa.png"),
                    new FishSpecies("Skalar", "Słodkowodna", 25, 29, "Ameryka Południowa", 6.0, 7.4, 5, 13, "pół-agresywne", 5, "10-15 lat",
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
                    new FishSpecies("Glonojad", "Słodkowodna", 23, 28, "Ameryka Południowa", 6.5, 7.5, 1, 15, "spokojne", 1, "3-7 lat",
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
                    new FishSpecies("Pyszczak", "Słodkowodna", 25, 28, "Afryka", 7.6, 8.8, 10, 25, "agresywne", 1, "8-10 lat",
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
        } else {
            // Aktualizacja opisów dla istniejących ryb
            System.out.println("--- AKTUALIZACJA OPISÓW RYB ---");
            java.util.Map<String, String> fishDescriptions = new java.util.HashMap<>();
            fishDescriptions.put("Welonka", "Welonka to klasyczna, spokojna ryba akwariowa, idealna dla początkujących. Jest odporna i łatwa w utrzymaniu.");
            fishDescriptions.put("Gupik", "Gupik to mała, kolorowa ryba, która najlepiej czuje się w grupie. Jest bardzo aktywna i łatwa w hodowli.");
            fishDescriptions.put("Bojownik syjamski", "Bojownik syjamski to efektowna, majestatyczna ryba znana z długich, falujących płetw i intensywnych barw. Samce są terytorialne i potrafią być agresywne wobec innych samców oraz ryb o podobnych płetwach, dlatego zwykle trzyma się je pojedynczo.");
            fishDescriptions.put("Neon Innesa", "Neon Innesa to drobna, energiczna ryba ławicowa, znana z intensywnego niebieskiego połysku widocznego nawet w słabym oświetleniu.");
            fishDescriptions.put("Skalar", "Skalar (Żaglowiec) to ryba pół-agresywna, która najlepiej czuje się w grupie. Lubi dużo miejsca do pływania i rośliny, przy których może się chować. Może pokazywać dominujące zachowania wobec innych ryb, dlatego najlepiej trzymać ją z gatunkami o podobnym temperamencie.");
            fishDescriptions.put("Mieczyk Hellera", "Mieczyk Hellera to żyworodna, wyrazista ryba znana z charakterystycznego 'mieczyka' na ogonie samców. Jest ruchliwa, wytrzymała i dobrze odnajduje się w większych akwariach. Choć generalnie towarzyska, potrafi wykazywać lekko dominujące zachowania, zwłaszcza samce między sobą, dlatego najlepiej trzymać ją w większej grupie.");
            fishDescriptions.put("Molinezja", "Molinezja to spokojna ryba, która najlepiej czuje się w grupie. Jest aktywna i lubi pływać wśród roślin. Jest odporna i łatwa w utrzymaniu, dobrze nadaje się do akwarium z innymi spokojnymi rybami.");
            fishDescriptions.put("Gurami mozaikowy", "Gurami mozaikowy to spokojna ryba o charakterystycznym, drobnym, mozaikowym wzorze na ciele. Porusza się powoli i często wykorzystuje wydłużone płetwy piersiowe do badania otoczenia.");
            fishDescriptions.put("Danio pręgowany", "Danio pręgowany to szybka, energiczna ryba ławicowa o smukłym ciele i wyraźnych, poziomych pręgach. Jest bardzo odporna i dobrze adaptuje się do różnych warunków, dzięki czemu świetnie nadaje się dla początkujących.");
            fishDescriptions.put("Kardynałek chiński", "Kardynałek chiński to niewielka, żywa i spokojna ryba o metalicznym połysku i czerwonym zabarwieniu płetw. Jest wyjątkowo odporna i dobrze czuje się nawet w chłodniejszych akwariach. W grupie prezentuje naturalne, harmonijne zachowania, tworząc efektowne mini-ławice.");
            fishDescriptions.put("Razbora klinowa", "Razbora klinowa to spokojna ryba ławicowa, która najlepiej czuje się w grupie. Jest aktywna i porusza się wśród roślin, tworząc efektowne grupy. Lubi dobrze oświetlone akwaria z miejscami do pływania i kryjówkami.");
            fishDescriptions.put("Tęczanka neonowa", "Tęczanka neonowa to spokojna ryba ławicowa, która najlepiej czuje się w grupie. Ma kolorowe, metaliczne ubarwienie i lubi poruszać się wśród roślin. Jest odporna i łatwa w utrzymaniu, dobrze nadaje się do akwarium z innymi spokojnymi rybami.");
            fishDescriptions.put("Kirys pstry", "Kirysek pstry to spokojna ryba, która lubi przebywać przy dnie akwarium i chować się między roślinami. Najlepiej czuje się w grupie, wtedy porusza się naturalnie i aktywnie.");
            fishDescriptions.put("Glonojad", "Glonojad / Zbrojnik to spokojna ryba, która pomaga utrzymać akwarium w czystości, zjadając glony z roślin i szybów. Lubi kryjówki i spokojne miejsca w zbiorniku. Jest odporna i łatwa w utrzymaniu, dobrze nadaje się do akwarium z innymi spokojnymi rybami.");
            fishDescriptions.put("Błazenek pomarańczowy", "Błazenek pomarańczowy to spokojna ryba, która najlepiej czuje się w parze. Lubi miejsca do ukrycia, np. między skałami lub wśród korali. Jest odporna, ale wymaga stabilnych warunków wody słonowodnej i odpowiedniej temperatury.");
            fishDescriptions.put("Pirania czerwona", "Pirania czerwona to agresywna ryba, która najlepiej żyje w grupie. Potrzebuje dużo miejsca do pływania i odpowiedniego akwarium, aby mogła wykazywać naturalne zachowania.");
            fishDescriptions.put("Pokolec królewski", "Pokolec królewski to spokojna ryba, która najlepiej trzymać pojedynczo. Lubi mieć miejsca do ukrycia, np. między skałami lub koralami. Jest odporna i może żyć długo w akwarium słonowodnym przy stabilnych warunkach wody.");
            fishDescriptions.put("Proporczykowiec", "Proporczykowiec to ryba pół-agresywna, która najlepiej czuje się w grupie. Lubi mieć kryjówki i miejsca do pływania. Może wykazywać dominujące zachowania wobec innych ryb, dlatego najlepiej trzymać ją z gatunkami o podobnym temperamencie.");
            fishDescriptions.put("Pyszczak", "Pyszczak (Malawi) to agresywna ryba, która najlepiej czuje się w swoim terytorium. Lubi mieć kryjówki i przestrzeń do pływania.");
            fishDescriptions.put("Księżniczka z Burundi", "Księżniczka z Burundi to agresywna ryba, która najlepiej czuje się w grupie. Lubi mieć kryjówki i dużo miejsca do pływania.");
            fishDescriptions.put("Kolcobrzuch karłowaty", "Kolcobrzuch karłowaty to agresywna ryba, która najlepiej trzymać pojedynczo. Ma mocny charakter i potrafi bronić swojego terytorium. Lubi kryjówki i miejsca do ukrycia. Jest odporna, ale wymaga stabilnych warunków wody.");
            fishDescriptions.put("Mandaryn wspaniały", "Mandaryn wspaniały to spokojna ryba, która najlepiej trzymać pojedynczo. Lubi miejsca do ukrycia i rośliny lub koralowce, w których może się poruszać. Jest wrażliwa na warunki wody, dlatego wymaga stabilnego akwarium słonowodnego.");
            fishDescriptions.put("Ustnik słoneczny", "Ustnik słoneczny to spokojna ryba, którą najlepiej trzymać pojedynczo. Lubi mieć miejsca do ukrycia, np. między skałami lub koralami. Jest odporna i może żyć długo w akwarium słonowodnym przy stabilnych warunkach wody.");
            fishDescriptions.put("Babka złota", "Babka złota to spokojna ryba, która najlepiej czuje się w grupie. Jest aktywna i lubi pływać wśród roślin oraz kryjówek. Jest odporna i łatwa w utrzymaniu, dobrze nadaje się do akwarium z innymi spokojnymi rybami.");

            List<FishSpecies> allFishes = fishRepository.findAll();
            int updatedCount = 0;
            for (FishSpecies fish : allFishes) {
                String correctDescription = fishDescriptions.get(fish.getName());
                if (correctDescription != null) {
                    // Aktualizuj zawsze, jeśli opis jest inny niż oczekiwany (naprawia błędne opisy w bazie)
                    if (fish.getDescription() == null || fish.getDescription().isEmpty() || 
                        !fish.getDescription().equals(correctDescription)) {
                        fish.setDescription(correctDescription);
                        fishRepository.save(fish);
                        updatedCount++;
                    }
                }
            }
            if (updatedCount > 0) {
                System.out.println("--- ZAKTUALIZOWANO OPISY DLA " + updatedCount + " RYB ---");
            } else {
                System.out.println("--- WSZYSTKIE RYBY MAJĄ JUŻ POPRAWNE OPISY ---");
            }
        }

        // Seeder roślin
        if (plantRepository.count() == 0) {
            List<Plant> plantList = List.of(
                    new Plant("Moczarka", "Moczarka", "Ameryka Północna", 12, 20, 6.0, 8.0, 5, 20, "słabe", "brak", "łatwa",
                            "Szybko rosnąca roślina łodygowa (tlenowa), świetna na start akwarium - mocno pobiera azotany i ogranicza glony. Może rosnąć posadzona w podłożu lub pływająca. Łatwa w uprawie, dobrze znosi słabsze światło; przycina się i sadzi ponownie wierzchołki."),
                    new Plant("Nurzaniec", "Nurzaniec", "Afryka, Azja, Europa", 20, 28, 6.8, 9.5, 5, 15, "bez znaczenia", "CO2", "łatwa",
                            "Roślina rozetowa o długich, taśmowatych liściach, idealna na tło. Szybko się rozrasta przez rozłogi. Lubi stabilne warunki; nie zakopuj nasady (korony). W razie przerostu skracaj liście i usuwaj najstarsze."),
                    new Plant("Anubias", "Anubias", "Afryka Zachodnia", 22, 28, 6.0, 8.0, 2, 12, "słabe", "CO2", "średnia",
                            "Wolno rosnąca roślina kłączowa do cienia; najlepiej przywiązać/przykleić do korzenia lub kamienia. Nie zakopuj kłącza w podłożu (gnije). Dobrze rośnie w słabszym świetle bez CO2, ale przy mocnym świetle łatwo łapie glony; rozmnażanie przez podział kłącza."),
                    new Plant("Mech Jawajski", "Mech Jawajski", "Azja", 15, 27, 5.0, 8.0, 2, 30, "słabe", "brak", "łatwa",
                            "Popularny mech do obsadzania dekoracji; tworzy gęste kępy i daje schronienie krewetkom oraz narybkowi. Toleruje słabe-średnie światło i zwykle nie wymaga CO2. Wymaga regularnego przycinania, bo łatwo zbiera detrytus; można go wiązać nitką/żyłką."),
                    new Plant("Ludwigia", "Ludwigia", "Ameryka Północna", 18, 28, 5.5, 8.0, 2, 15, "umiarkowane", "brak", "łatwa",
                            "Roślina łodygowa o zielono-czerwonych liściach; kolor mocno zależy od światła i nawożenia. Najlepiej rośnie przy średnim-mocnym świetle, z CO2 staje się gęstsza. Przycinaj wierzchołki, a odcięte sadzonki wsadzaj ponownie w podłoże."),
                    new Plant("Rogatek", "Rogatek", "Ameryka Południowa, Ameryka Północna", 15, 30, 5.0, 8.0, 3, 15, "słabe", "brak", "łatwa",
                            "Bardzo szybko rosnąca roślina pływająca lub luźno zakotwiczona (bez prawdziwych korzeni). Świetnie wyjada nadmiar składników z wody, pomaga w walce z glonami i jako roślina \"tlenowa\". Lubi częste przycinki; potrafi gubić igiełki przy słabym świetle lub dużych wahaniach parametrów."),
                    new Plant("Kryptokoryna", "Kryptokoryna", "Azja", 22, 30, 6.5, 8.0, 2, 15, "umiarkowane", "Fe", "łatwa",
                            "Roślina rozetowa do pierwszego lub środkowego planu; lubi spokojne, stabilne warunki. Po zmianach w akwarium może wystąpić tzw. \"crypt melt\" (zrzucanie liści) - zwykle odbija z korzeni. Dobrze reaguje na nawożenie pod korzeń (kulki/tabsy) i rozmnaża się przez rozłogi."),
                    new Plant("Lotos tygrysi", "Lotos tygrysi", "Afryka, Azja", 22, 28, 5.5, 7.5, 2, 10, "umiarkowane", "brak", "łatwa",
                            "Roślina cebulowa o dużych liściach (zielonych lub czerwonych), często jako soliter w środkowym/tylnym planie. Silny żarłok korzeniowy - lubi żyzne podłoże i nawożenie pod korzeń. Sadź cebulę częściowo odsłoniętą; przycinaj liście pędzące do tafli, jeśli chcesz utrzymać formę podwodną (inaczej mocno zacienia)."),
                    new Plant("Żabienica", "Żabienica", "Ameryka Południowa", 22, 28, 6.0, 7.5, 2, 15, "umiarkowane", "CO2", "trudna",
                            "Duża roślina rozetowa (tzw. mieczyk amazoński), często jako centralna ozdoba lub tło w większych zbiornikach. Wymaga składników w podłożu (tabsy, ziemia) i regularnego nawożenia - to roślina silnie korzeniąca się. Może szybko przerosnąć małe akwaria; rozmnaża się przez młode roślinki na pędzie kwiatostanowym."),
                    new Plant("Limnofila", "Limnofila", "Azja", 22, 26, 6.0, 7.0, 3, 14, "mocne", "CO2, Fe", "średnia",
                            "Szybko rosnąca roślina łodygowa o pierzastych liściach, świetna na tło i do stabilizacji świeżego akwarium. Rośnie w średnim świetle, a CO2 zwiększa gęstość i tempo wzrostu. Przycinaj i sadź ponownie wierzchołki, aby uzyskać gęste kępy; bardzo dobrze redukuje azotany."),
                    new Plant("Rotala", "Rotala", "Azja", 18, 28, 5.5, 7.5, 2, 15, "mocne", "Fe, P, NO3", "łatwa",
                            "Roślina łodygowa o drobnych liściach; przy mocnym świetle potrafi wybarwiać się na różowo-czerwono. Najlepiej rośnie w średnim-mocnym świetle, z CO2 i zbilansowanym nawożeniem. Regularne przycinanie pobudza krzewienie; sadzonki z wierzchołków można łatwo ponownie posadzić."),
                    new Plant("Duży Heniek", "Duży Heniek", "Ameryka Północna", 20, 26, 5.5, 8.0, 4, 18, "mocne", "CO2", "średnia",
                            "Roślina łodygowa o jasnozielonych listkach, tworząca zwarte grupy. Wymaga dobrego światła i CO2, aby rosnąć nisko i gęsto. Przy słabszym świetle wyciąga się w górę."),
                    new Plant("Ponikło maleńkie", "Ponikło maleńkie", "Wszędzie", 19, 28, 5.5, 8.0, 2, 12, "bez znaczenia", "CO2", "łatwa",
                            "Trawiasta roślina pierwszego planu (trawnikowa), przypominająca igiełki. Rośnie wolno, ale tworzy gęsty dywan przy dobrym świetle i CO2. Rozmnaża się przez podziemne rozłogi."),
                    new Plant("Gałęzatka Kulista", "Gałęzatka Kulista", "Azja, Europa", 19, 28, 6.0, 8.5, 2, 12, "bez znaczenia", "CO2", "łatwa",
                            "Kulista kolonia glonów (nie roślina!), bardzo dekoracyjna i niezwykle łatwa w utrzymaniu. Nie zakopuj w podłożu; wystarczy położyć na dnie. Lubi chłodniejszą wodę. Co jakiś czas \"turlaj\" ją, aby zachowała kulisty kształt."),
                    new Plant("Heniek Mały", "Heniek Mały", "Ameryka Północna", 20, 28, 5.0, 8.0, 1, 15, "mocne", "CO2, Fe", "średnia",
                            "Jedna z najmniejszych roślin akwariowych, idealna na trawnik. Wymaga bardzo mocnego światła, CO2 i żyznego podłoża. Ma bardzo płytki system korzeniowy, więc trudno ją posadzić (często wypływa)."),
                    new Plant("Monte Carlo", "Monte Carlo", "Ameryka Południowa", 21, 28, 6.0, 7.5, 4, 20, "mocne", "CO2", "średnia",
                            "Roślina okrywowa (trawnikowa) o drobnych, okrągłych listkach. Łatwiejsza w uprawie niż \"Mały Heniek\", lepiej się ukorzenia i szybciej rośnie. Wymaga dobrego światła i CO2, aby stworzyć gęsty dywan. Przycinanie stymuluje zagęszczanie.")
            );

            plantRepository.saveAll(plantList);
            System.out.println("--- BAZA DANYCH ROŚLIN ZOSTAŁA ZASILONA DANYMI ---");
        } else {
            // Aktualizacja opisów dla istniejących roślin
            System.out.println("--- AKTUALIZACJA OPISÓW ROŚLIN ---");
            java.util.Map<String, String> plantDescriptions = new java.util.HashMap<>();
            plantDescriptions.put("Moczarka", "Szybko rosnąca roślina łodygowa (tlenowa), świetna na start akwarium - mocno pobiera azotany i ogranicza glony. Może rosnąć posadzona w podłożu lub pływająca. Łatwa w uprawie, dobrze znosi słabsze światło; przycina się i sadzi ponownie wierzchołki.");
            plantDescriptions.put("Nurzaniec", "Roślina rozetowa o długich, taśmowatych liściach, idealna na tło. Szybko się rozrasta przez rozłogi. Lubi stabilne warunki; nie zakopuj nasady (korony). W razie przerostu skracaj liście i usuwaj najstarsze.");
            plantDescriptions.put("Anubias", "Wolno rosnąca roślina kłączowa do cienia; najlepiej przywiązać/przykleić do korzenia lub kamienia. Nie zakopuj kłącza w podłożu (gnije). Dobrze rośnie w słabszym świetle bez CO2, ale przy mocnym świetle łatwo łapie glony; rozmnażanie przez podział kłącza.");
            plantDescriptions.put("Mech Jawajski", "Popularny mech do obsadzania dekoracji; tworzy gęste kępy i daje schronienie krewetkom oraz narybkowi. Toleruje słabe-średnie światło i zwykle nie wymaga CO2. Wymaga regularnego przycinania, bo łatwo zbiera detrytus; można go wiązać nitką/żyłką.");
            plantDescriptions.put("Ludwigia", "Roślina łodygowa o zielono-czerwonych liściach; kolor mocno zależy od światła i nawożenia. Najlepiej rośnie przy średnim-mocnym świetle, z CO2 staje się gęstsza. Przycinaj wierzchołki, a odcięte sadzonki wsadzaj ponownie w podłoże.");
            plantDescriptions.put("Rogatek", "Bardzo szybko rosnąca roślina pływająca lub luźno zakotwiczona (bez prawdziwych korzeni). Świetnie wyjada nadmiar składników z wody, pomaga w walce z glonami i jako roślina \"tlenowa\". Lubi częste przycinki; potrafi gubić igiełki przy słabym świetle lub dużych wahaniach parametrów.");
            plantDescriptions.put("Kryptokoryna", "Roślina rozetowa do pierwszego lub środkowego planu; lubi spokojne, stabilne warunki. Po zmianach w akwarium może wystąpić tzw. \"crypt melt\" (zrzucanie liści) - zwykle odbija z korzeni. Dobrze reaguje na nawożenie pod korzeń (kulki/tabsy) i rozmnaża się przez rozłogi.");
            plantDescriptions.put("Lotos tygrysi", "Roślina cebulowa o dużych liściach (zielonych lub czerwonych), często jako soliter w środkowym/tylnym planie. Silny żarłok korzeniowy - lubi żyzne podłoże i nawożenie pod korzeń. Sadź cebulę częściowo odsłoniętą; przycinaj liście pędzące do tafli, jeśli chcesz utrzymać formę podwodną (inaczej mocno zacienia).");
            plantDescriptions.put("Żabienica", "Duża roślina rozetowa (tzw. mieczyk amazoński), często jako centralna ozdoba lub tło w większych zbiornikach. Wymaga składników w podłożu (tabsy, ziemia) i regularnego nawożenia - to roślina silnie korzeniąca się. Może szybko przerosnąć małe akwaria; rozmnaża się przez młode roślinki na pędzie kwiatostanowym.");
            plantDescriptions.put("Limnofila", "Szybko rosnąca roślina łodygowa o pierzastych liściach, świetna na tło i do stabilizacji świeżego akwarium. Rośnie w średnim świetle, a CO2 zwiększa gęstość i tempo wzrostu. Przycinaj i sadź ponownie wierzchołki, aby uzyskać gęste kępy; bardzo dobrze redukuje azotany.");
            plantDescriptions.put("Rotala", "Roślina łodygowa o drobnych liściach; przy mocnym świetle potrafi wybarwiać się na różowo-czerwono. Najlepiej rośnie w średnim-mocnym świetle, z CO2 i zbilansowanym nawożeniem. Regularne przycinanie pobudza krzewienie; sadzonki z wierzchołków można łatwo ponownie posadzić.");
            plantDescriptions.put("Duży Heniek", "Roślina łodygowa o jasnozielonych listkach, tworząca zwarte grupy. Wymaga dobrego światła i CO2, aby rosnąć nisko i gęsto. Przy słabszym świetle wyciąga się w górę.");
            plantDescriptions.put("Ponikło maleńkie", "Trawiasta roślina pierwszego planu (trawnikowa), przypominająca igiełki. Rośnie wolno, ale tworzy gęsty dywan przy dobrym świetle i CO2. Rozmnaża się przez podziemne rozłogi.");
            plantDescriptions.put("Gałęzatka Kulista", "Kulista kolonia glonów (nie roślina!), bardzo dekoracyjna i niezwykle łatwa w utrzymaniu. Nie zakopuj w podłożu; wystarczy położyć na dnie. Lubi chłodniejszą wodę. Co jakiś czas \"turlaj\" ją, aby zachowała kulisty kształt.");
            plantDescriptions.put("Heniek Mały", "Jedna z najmniejszych roślin akwariowych, idealna na trawnik. Wymaga bardzo mocnego światła, CO2 i żyznego podłoża. Ma bardzo płytki system korzeniowy, więc trudno ją posadzić (często wypływa).");
            plantDescriptions.put("Monte Carlo", "Roślina okrywowa (trawnikowa) o drobnych, okrągłych listkach. Łatwiejsza w uprawie niż \"Mały Heniek\", lepiej się ukorzenia i szybciej rośnie. Wymaga dobrego światła i CO2, aby stworzyć gęsty dywan. Przycinanie stymuluje zagęszczanie.");

            List<Plant> allPlants = plantRepository.findAll();
            int updatedCount = 0;
            for (Plant plant : allPlants) {
                String description = plantDescriptions.get(plant.getName());
                if (description != null && (plant.getDescription() == null || plant.getDescription().isEmpty())) {
                    plant.setDescription(description);
                    plantRepository.save(plant);
                    updatedCount++;
                }
            }
            if (updatedCount > 0) {
                System.out.println("--- ZAKTUALIZOWANO OPISY DLA " + updatedCount + " ROŚLIN ---");
            } else {
                System.out.println("--- WSZYSTKIE ROŚLINY MAJĄ JUŻ OPISY ---");
            }
        }
    }
}
