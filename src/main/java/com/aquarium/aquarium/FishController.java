package com.aquarium.aquarium;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/fish")
public class FishController {

    private final FishSpeciesRepository fishRepository;

    public FishController(FishSpeciesRepository fishRepository) {
        this.fishRepository = fishRepository;
    }

    @GetMapping
    public List<FishResponseDto> getAllFishes() {
        return fishRepository.findAll().stream()
                .map(FishResponseDto::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFishById(@PathVariable String id) {
        Long fishId = IdMapper.fromFishId(id);
        if (fishId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid fish ID format"));
        }
        return fishRepository.findById(fishId)
                .map(fish -> ResponseEntity.ok(new FishResponseDto(fish)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<FishResponseDto> searchFishes(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String waterType,
            @RequestParam(required = false) String temperament,
            @RequestParam(required = false) String biotope,
            @RequestParam(required = false) Integer tempMin,
            @RequestParam(required = false) Integer tempMax,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset) {
        
        List<FishSpecies> allFishes = fishRepository.findAll();
        
        return allFishes.stream()
                .filter(fish -> {
                    if (q != null && !q.isEmpty()) {
                        if (fish.getName() == null || !fish.getName().toLowerCase().contains(q.toLowerCase())) {
                            return false;
                        }
                    }
                    if (waterType != null && !waterType.isEmpty()) {
                        // Mapowanie: frontend wysyła "freshwater"/"saltwater", baza ma "Słodkowodna"/"Słonowodna"
                        String fishWaterType = fish.getWaterType();
                        if (fishWaterType == null) {
                            return false;
                        }
                        if (waterType.equals("freshwater") && !fishWaterType.equals("Słodkowodna")) {
                            return false;
                        }
                        if (waterType.equals("saltwater") && !fishWaterType.equals("Słonowodna") && !fishWaterType.equals("Słonawowodna")) {
                            return false;
                        }
                        // Jeśli frontend wysyła polską nazwę, sprawdź bezpośrednio
                        if (!waterType.equals("freshwater") && !waterType.equals("saltwater") && !fishWaterType.equals(waterType)) {
                            return false;
                        }
                    }
                    if (temperament != null && !temperament.isEmpty()) {
                        if (!temperament.equals(fish.getTemperament())) {
                            return false;
                        }
                    }
                    if (biotope != null && !biotope.isEmpty()) {
                        if (fish.getBiotype() == null || !fish.getBiotype().equalsIgnoreCase(biotope)) {
                            return false;
                        }
                    }
                    if (tempMin != null && fish.getTempMaxC() < tempMin) {
                        return false;
                    }
                    if (tempMax != null && fish.getTempMinC() > tempMax) {
                        return false;
                    }
                    return true;
                })
                .skip(offset != null ? offset : 0)
                .limit(limit != null ? limit : Integer.MAX_VALUE)
                .map(FishResponseDto::new)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<?> createFish(@RequestBody FishRequestDto request) {
        try {
            if (request.getName() == null || request.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Name is required"));
            }

            String waterType = "Słodkowodna";
            if (request.getWaterType() != null && request.getWaterType().equals("saltwater")) {
                waterType = "Słonowodna";
            }

            FishSpecies fish = new FishSpecies();
            fish.setName(request.getName());
            fish.setDescription(request.getDescription() != null ? request.getDescription() : "");
            fish.setImage(request.getImage() != null ? request.getImage() : "");
            fish.setWaterType(waterType);
            fish.setTempMinC(request.getTempRange() != null && !request.getTempRange().isEmpty() 
                ? request.getTempRange().get(0) : 22);
            fish.setTempMaxC(request.getTempRange() != null && request.getTempRange().size() > 1 
                ? request.getTempRange().get(1) : 26);
            fish.setBiotype(request.getBiotope() != null ? request.getBiotope() : "");
            fish.setPhMin(request.getPhRange() != null && !request.getPhRange().isEmpty() 
                ? request.getPhRange().get(0) : 6.5);
            fish.setPhMax(request.getPhRange() != null && request.getPhRange().size() > 1 
                ? request.getPhRange().get(1) : 7.5);
            fish.setGhMin(request.getHardness() != null && !request.getHardness().isEmpty() 
                ? request.getHardness().get(0) : 5);
            fish.setGhMax(request.getHardness() != null && request.getHardness().size() > 1 
                ? request.getHardness().get(1) : 15);
            fish.setTemperament(request.getTemperament() != null ? request.getTemperament() : "spokojne");
            fish.setMinSchoolSize(request.getMinSchoolSize() != null ? request.getMinSchoolSize() : 1);
            fish.setLifespan(request.getLifespan() != null ? request.getLifespan() : "3-5 lat");

            fish = fishRepository.save(fish);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new FishResponseDto(fish));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create fish: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFish(@PathVariable String id, @RequestBody FishRequestDto request) {
        try {
            Long fishId = IdMapper.fromFishId(id);
            if (fishId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid fish ID format"));
            }
            return fishRepository.findById(fishId)
                    .map(fish -> {
                        if (request.getName() != null && !request.getName().trim().isEmpty()) {
                            fish.setName(request.getName());
                        }
                        if (request.getDescription() != null) {
                            fish.setDescription(request.getDescription());
                        }
                        if (request.getImage() != null) {
                            fish.setImage(request.getImage());
                        }
                        if (request.getWaterType() != null) {
                            String waterType = request.getWaterType().equals("saltwater") ? "Słonowodna" : "Słodkowodna";
                            fish.setWaterType(waterType);
                        }
                        if (request.getTempRange() != null && !request.getTempRange().isEmpty()) {
                            fish.setTempMinC(request.getTempRange().get(0));
                            if (request.getTempRange().size() > 1) {
                                fish.setTempMaxC(request.getTempRange().get(1));
                            }
                        }
                        if (request.getBiotope() != null) {
                            fish.setBiotype(request.getBiotope());
                        }
                        if (request.getPhRange() != null && !request.getPhRange().isEmpty()) {
                            fish.setPhMin(request.getPhRange().get(0));
                            if (request.getPhRange().size() > 1) {
                                fish.setPhMax(request.getPhRange().get(1));
                            }
                        }
                        if (request.getHardness() != null && !request.getHardness().isEmpty()) {
                            fish.setGhMin(request.getHardness().get(0));
                            if (request.getHardness().size() > 1) {
                                fish.setGhMax(request.getHardness().get(1));
                            }
                        }
                        if (request.getTemperament() != null) {
                            fish.setTemperament(request.getTemperament());
                        }
                        if (request.getMinSchoolSize() != null) {
                            fish.setMinSchoolSize(request.getMinSchoolSize());
                        }
                        if (request.getLifespan() != null) {
                            fish.setLifespan(request.getLifespan());
                        }

                        fish = fishRepository.save(fish);
                        return ResponseEntity.ok(new FishResponseDto(fish));
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update fish: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFish(@PathVariable String id) {
        try {
            Long fishId = IdMapper.fromFishId(id);
            if (fishId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid fish ID format"));
            }
            if (fishRepository.existsById(fishId)) {
                fishRepository.deleteById(fishId);
                return ResponseEntity.ok(Map.of("message", "Fish deleted successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete fish: " + e.getMessage()));
        }
    }

    @PostMapping("/update-descriptions")
    public ResponseEntity<?> updateFishDescriptions() {
        try {
            Map<String, Map<String, String>> fishData = new HashMap<>();
            
            // Welonka (Złota rybka)
            Map<String, String> welonka = new HashMap<>();
            welonka.put("description", "Welonka to klasyczna, spokojna ryba akwariowa, idealna dla początkujących. Jest odporna i łatwa w utrzymaniu.");
            welonka.put("image", "/fish/Welonka__Złota_rybka.png");
            welonka.put("iconName", "Welonka__Złota_rybka.png");
            fishData.put("Welonka (Złota rybka)", welonka);
            
            // Gupik (Głupik)
            Map<String, String> gupik = new HashMap<>();
            gupik.put("description", "Gupik to mała, kolorowa ryba, która najlepiej czuje się w grupie. Jest bardzo aktywna i łatwa w hodowli.");
            gupik.put("image", "/fish/Gupik__Głupik.png");
            gupik.put("iconName", "Gupik__Głupik.png");
            fishData.put("Gupik (Głupik)", gupik);
            
            // Bojownik syjamski
            Map<String, String> bojownik = new HashMap<>();
            bojownik.put("description", "Bojownik syjamski to efektowna, majestatyczna ryba znana z długich, falujących płetw i intensywnych barw. Samce są terytorialne i potrafią być agresywne wobec innych samców oraz ryb o podobnych płetwach, dlatego zwykle trzyma się je pojedynczo.");
            bojownik.put("image", "/fish/Bojownik_syjamski.png");
            bojownik.put("iconName", "Bojownik_syjamski.png");
            fishData.put("Bojownik syjamski", bojownik);
            
            // Neon Innesa
            Map<String, String> neon = new HashMap<>();
            neon.put("description", "Neon Innesa to drobna, energiczna ryba ławicowa, znana z intensywnego niebieskiego połysku widocznego nawet w słabym oświetleniu.");
            neon.put("image", "/fish/Neon_Innesa.png");
            neon.put("iconName", "Neon_Innesa.png");
            fishData.put("Neon Innesa", neon);
            
            // Skalar (Żaglowiec)
            Map<String, String> skalar = new HashMap<>();
            skalar.put("description", "Skalar (Żaglowiec) to ryba pół-agresywna, która najlepiej czuje się w grupie. Lubi dużo miejsca do pływania i rośliny, przy których może się chować. Może pokazywać dominujące zachowania wobec innych ryb, dlatego najlepiej trzymać ją z gatunkami o podobnym temperamencie.");
            skalar.put("image", "/fish/Skalar__Żaglowiec.png");
            skalar.put("iconName", "Skalar__Żaglowiec.png");
            fishData.put("Skalar (Żaglowiec)", skalar);
            
            // Mieczyk Hellera
            Map<String, String> mieczyk = new HashMap<>();
            mieczyk.put("description", "Mieczyk Hellera to żyworodna, wyrazista ryba znana z charakterystycznego ''mieczyka'' na ogonie samców. Jest ruchliwa, wytrzymała i dobrze odnajduje się w większych akwariach. Choć generalnie towarzyska, potrafi wykazywać lekko dominujące zachowania, zwłaszcza samce między sobą, dlatego najlepiej trzymać ją w większej grupie.");
            mieczyk.put("image", "/fish/Mieczyk_Hellera.png");
            mieczyk.put("iconName", "Mieczyk_Hellera.png");
            fishData.put("Mieczyk Hellera", mieczyk);
            
            // Molinezja
            Map<String, String> molinezja = new HashMap<>();
            molinezja.put("description", "Molinezja to spokojna ryba, która najlepiej czuje się w grupie. Jest aktywna i lubi pływać wśród roślin. Jest odporna i łatwa w utrzymaniu, dobrze nadaje się do akwarium z innymi spokojnymi rybami.");
            molinezja.put("image", "/fish/Molinezja.png");
            molinezja.put("iconName", "Molinezja.png");
            fishData.put("Molinezja", molinezja);
            
            // Gurami mozaikowy
            Map<String, String> gurami = new HashMap<>();
            gurami.put("description", "Gurami mozaikowy to spokojna ryba o charakterystycznym, drobnym, mozaikowym wzorze na ciele. Porusza się powoli i często wykorzystuje wydłużone płetwy piersiowe do badania otoczenia.");
            gurami.put("image", "/fish/Gurami_mozaikowy.png");
            gurami.put("iconName", "Gurami_mozaikowy.png");
            fishData.put("Gurami mozaikowy", gurami);
            
            // Danio pręgowany
            Map<String, String> danio = new HashMap<>();
            danio.put("description", "Danio pręgowany to szybka, energiczna ryba ławicowa o smukłym ciele i wyraźnych, poziomych pręgach. Jest bardzo odporna i dobrze adaptuje się do różnych warunków, dzięki czemu świetnie nadaje się dla początkujących.");
            danio.put("image", "/fish/Danio_pręgowany.png");
            danio.put("iconName", "Danio_pręgowany.png");
            fishData.put("Danio pręgowany", danio);
            
            // Kardynałek chiński
            Map<String, String> kardynalek = new HashMap<>();
            kardynalek.put("description", "Kardynałek chiński to niewielka, żywa i spokojna ryba o metalicznym połysku i czerwonym zabarwieniu płetw. Jest wyjątkowo odporna i dobrze czuje się nawet w chłodniejszych akwariach. W grupie prezentuje naturalne, harmonijne zachowania, tworząc efektowne mini-ławice.");
            kardynalek.put("image", "/fish/Kardynałek_chiński.png");
            kardynalek.put("iconName", "Kardynałek_chiński.png");
            fishData.put("Kardynałek chiński", kardynalek);
            
            // Razbora klinowa
            Map<String, String> razbora = new HashMap<>();
            razbora.put("description", "Razbora klinowa to spokojna ryba ławicowa, która najlepiej czuje się w grupie. Jest aktywna i porusza się wśród roślin, tworząc efektowne grupy. Lubi dobrze oświetlone akwaria z miejscami do pływania i kryjówkami.");
            razbora.put("image", "/fish/Razbora_klinowa.png");
            razbora.put("iconName", "Razbora_klinowa.png");
            fishData.put("Razbora klinowa", razbora);
            
            // Tęczanka neonowa
            Map<String, String> teczanka = new HashMap<>();
            teczanka.put("description", "Tęczanka neonowa to spokojna ryba ławicowa, która najlepiej czuje się w grupie. Ma kolorowe, metaliczne ubarwienie i lubi poruszać się wśród roślin. Jest odporna i łatwa w utrzymaniu, dobrze nadaje się do akwarium z innymi spokojnymi rybami.");
            teczanka.put("image", "/fish/Tęczanka_neonowa.png");
            teczanka.put("iconName", "Tęczanka_neonowa.png");
            fishData.put("Tęczanka neonowa", teczanka);
            
            // Kirys pstry
            Map<String, String> kirys = new HashMap<>();
            kirys.put("description", "Kirys pstry to spokojna ryba, która lubi przebywać przy dnie akwarium i chować się między roślinami. Najlepiej czuje się w grupie, wtedy porusza się naturalnie i aktywnie.");
            kirys.put("image", "/fish/Kirys_pstry.png");
            kirys.put("iconName", "Kirys_pstry.png");
            fishData.put("Kirys pstry", kirys);
            
            // Glonojad (Zbrojnik)
            Map<String, String> glonojad = new HashMap<>();
            glonojad.put("description", "Glonojad / Zbrojnik to spokojna ryba, która pomaga utrzymać akwarium w czystości, zjadając glony z roślin i szybów. Lubi kryjówki i spokojne miejsca w zbiorniku. Jest odporna i łatwa w utrzymaniu, dobrze nadaje się do akwarium z innymi spokojnymi rybami.");
            glonojad.put("image", "/fish/GlonojadZbrojnik-.png");
            glonojad.put("iconName", "GlonojadZbrojnik-.png");
            fishData.put("Glonojad (Zbrojnik)", glonojad);
            
            // Błazenek pomarańczowy
            Map<String, String> blazenek = new HashMap<>();
            blazenek.put("description", "Błazenek pomarańczowy to spokojna ryba, która najlepiej czuje się w parze. Lubi miejsca do ukrycia, np. między skałami lub wśród korali. Jest odporna, ale wymaga stabilnych warunków wody słonowodnej i odpowiedniej temperatury.");
            blazenek.put("image", "/fish/Błazenek_pomarańczowy.png");
            blazenek.put("iconName", "Błazenek_pomarańczowy.png");
            fishData.put("Błazenek pomarańczowy", blazenek);
            
            // Pirania czerwona
            Map<String, String> pirania = new HashMap<>();
            pirania.put("description", "Pirania czerwona to agresywna ryba, która najlepiej żyje w grupie. Potrzebuje dużo miejsca do pływania i odpowiedniego akwarium, aby mogła wykazywać naturalne zachowania.");
            pirania.put("image", "/fish/Pirania_czerwona.png");
            pirania.put("iconName", "Pirania_czerwona.png");
            fishData.put("Pirania czerwona", pirania);
            
            // Pokolec królewski
            Map<String, String> pokolec = new HashMap<>();
            pokolec.put("description", "Pokolec królewski to spokojna ryba, którą najlepiej trzymać pojedynczo. Lubi mieć miejsca do ukrycia, np. między skałami lub koralami. Jest odporna i może żyć długo w akwarium słonowodnym przy stabilnych warunkach wody.");
            pokolec.put("image", "/fish/Pokolec_królewski.png");
            pokolec.put("iconName", "Pokolec_królewski.png");
            fishData.put("Pokolec królewski", pokolec);
            
            // Proporczykowiec
            Map<String, String> proporczykowiec = new HashMap<>();
            proporczykowiec.put("description", "Proporczykowiec to ryba pół-agresywna, która najlepiej czuje się w grupie. Lubi mieć kryjówki i miejsca do pływania. Może wykazywać dominujące zachowania wobec innych ryb, dlatego najlepiej trzymać ją z gatunkami o podobnym temperamencie.");
            proporczykowiec.put("image", "/fish/Proporczykowiec.png");
            proporczykowiec.put("iconName", "Proporczykowiec.png");
            fishData.put("Proporczykowiec", proporczykowiec);
            
            // Pyszczak (Malawi)
            Map<String, String> pyszczak = new HashMap<>();
            pyszczak.put("description", "Pyszczak (Malawi) to agresywna ryba, która najlepiej czuje się w swoim terytorium. Lubi mieć kryjówki i przestrzeń do pływania.");
            pyszczak.put("image", "/fish/Pyszczak__Malawi.png");
            pyszczak.put("iconName", "Pyszczak__Malawi.png");
            fishData.put("Pyszczak (Malawi)", pyszczak);
            
            // Księżniczka z Burundi
            Map<String, String> ksiezniczka = new HashMap<>();
            ksiezniczka.put("description", "Księżniczka z Burundi to agresywna ryba, która najlepiej czuje się w grupie. Lubi mieć kryjówki i dużo miejsca do pływania.");
            ksiezniczka.put("image", "/fish/Księżniczka_z_Burundi.png");
            ksiezniczka.put("iconName", "Księżniczka_z_Burundi.png");
            fishData.put("Księżniczka z Burundi", ksiezniczka);
            
            // Kolcobrzuch karłowaty
            Map<String, String> kolcobrzuch = new HashMap<>();
            kolcobrzuch.put("description", "Kolcobrzuch karłowaty to agresywna ryba, którą najlepiej trzymać pojedynczo. Ma mocny charakter i potrafi bronić swojego terytorium. Lubi kryjówki i miejsca do ukrycia. Jest odporna, ale wymaga stabilnych warunków wody.");
            kolcobrzuch.put("image", "/fish/Kolcobrzuch_karłowaty.png");
            kolcobrzuch.put("iconName", "Kolcobrzuch_karłowaty.png");
            fishData.put("Kolcobrzuch karłowaty", kolcobrzuch);
            
            // Mandaryn wspaniały
            Map<String, String> mandaryn = new HashMap<>();
            mandaryn.put("description", "Mandaryn wspaniały to spokojna ryba, którą najlepiej trzymać pojedynczo. Lubi miejsca do ukrycia i rośliny lub koralowce, w których może się poruszać. Jest wrażliwa na warunki wody, dlatego wymaga stabilnego akwarium słonowodnego.");
            mandaryn.put("image", "/fish/Mandaryn_wspaniały.png");
            mandaryn.put("iconName", "Mandaryn_wspaniały.png");
            fishData.put("Mandaryn wspaniały", mandaryn);
            
            // Ustnik słoneczny
            Map<String, String> ustnik = new HashMap<>();
            ustnik.put("description", "Ustnik słoneczny to spokojna ryba, którą najlepiej trzymać pojedynczo. Lubi mieć miejsca do ukrycia, np. między skałami lub koralami. Jest odporna i może żyć długo w akwarium słonowodnym przy stabilnych warunkach wody.");
            ustnik.put("image", "/fish/Ustnik_żółty_ryba.png");
            ustnik.put("iconName", "Ustnik_żółty_ryba.png");
            fishData.put("Ustnik słoneczny", ustnik);
            
            // Babka złota
            Map<String, String> babka = new HashMap<>();
            babka.put("description", "Babka złota to spokojna ryba, która najlepiej czuje się w grupie. Jest aktywna i lubi pływać wśród roślin oraz kryjówek. Jest odporna i łatwa w utrzymaniu, dobrze nadaje się do akwarium z innymi spokojnymi rybami.");
            babka.put("image", "/fish/Babka_złota.png");
            babka.put("iconName", "Babka_złota.png");
            fishData.put("Babka złota", babka);

            int updated = 0;
            int notFound = 0;

            for (Map.Entry<String, Map<String, String>> entry : fishData.entrySet()) {
                String fishName = entry.getKey();
                Map<String, String> data = entry.getValue();
                
                List<FishSpecies> fishes = fishRepository.findByName(fishName);
                if (!fishes.isEmpty()) {
                    for (FishSpecies fish : fishes) {
                        fish.setDescription(data.get("description"));
                        fish.setImage(data.get("image"));
                        fish.setIconName(data.get("iconName"));
                        fishRepository.save(fish);
                        updated++;
                    }
                } else {
                    notFound++;
                }
            }

            return ResponseEntity.ok(Map.of(
                "message", "Fish descriptions updated",
                "updated", updated,
                "notFound", notFound
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update fish descriptions: " + e.getMessage()));
        }
    }
}
