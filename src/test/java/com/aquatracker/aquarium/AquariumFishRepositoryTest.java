package com.aquatracker.aquarium;

import com.aquatracker.fish.FishSpecies;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class AquariumFishRepositoryTest {

    @Autowired
    private AquariumFishRepository repository; 
    @Autowired
    private TestEntityManager entityManager; 

    @Test
    void shouldSaveAndFindAquariumFish() {
        // Cel: Sprawdzenie podstawowej operacji zapisu i odczytu.
        // Weryfikujemy, czy relacja Many-to-Many (lub encja asocjacyjna) poprawnie zapisuje się w bazie.

        // --- GIVEN ---
        Aquarium aquarium = createValidAquarium("Moje Główne Akwarium");
        FishSpecies species = createFishSpecies("Neon Innesa");
        AquariumFish aquariumFish = new AquariumFish();
        aquariumFish.setAquarium(aquarium);
        aquariumFish.setFishSpecies(species);
        aquariumFish.setFishCount(15);

        // --- WHEN ---
        AquariumFish savedFish = repository.save(aquariumFish);

        // --- THEN ---
        assertThat(savedFish.getId()).isNotNull(); 
        assertThat(savedFish.getFishCount()).isEqualTo(15);
        assertThat(savedFish.getAquarium().getName()).isEqualTo("Moje Główne Akwarium");
        assertThat(savedFish.getFishSpecies().getName()).isEqualTo("Neon Innesa");
    }

    @Test
    void shouldFindFishByAquariumId() {
        // Cel: Sprawdzenie wyszukiwania ryb przypisanych do konkretnego akwarium.

        // --- GIVEN ---
        Aquarium aquarium1 = createValidAquarium("Akwarium 1");
        Aquarium aquarium2 = createValidAquarium("Akwarium 2"); 

        FishSpecies species = createFishSpecies("Gupik");

        // Przypisujemy ryby TYLKO do pierwszego akwarium
        AquariumFish fish = new AquariumFish();
        fish.setAquarium(aquarium1);
        fish.setFishSpecies(species);
        fish.setFishCount(5);
        
        entityManager.persist(fish);
        entityManager.flush(); 

        // --- WHEN ---
        List<AquariumFish> results = repository.findByAquariumId(aquarium1.getId());

        // --- THEN ---
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getAquarium().getId()).isEqualTo(aquarium1.getId());
        
        List<AquariumFish> emptyResults = repository.findByAquariumId(aquarium2.getId());
        assertThat(emptyResults).isEmpty();
    }

    @Test
    void shouldFindByAquariumIdAndFishSpeciesId() {
        // Cel: Sprawdzenie precyzyjnego wyszukiwania konkretnego gatunku w konkretnym akwarium.

        // --- GIVEN ---
        Aquarium aquarium = createValidAquarium("Akwarium Roślinne");
        FishSpecies species = createFishSpecies("Bojownik");

        AquariumFish fish = new AquariumFish();
        fish.setAquarium(aquarium);
        fish.setFishSpecies(species);
        fish.setFishCount(1);
        entityManager.persist(fish);

        // --- WHEN ---
        List<AquariumFish> results = repository.findByAquariumIdAndFishSpeciesId(aquarium.getId(), species.getId());

        // --- THEN ---
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getFishSpecies().getId()).isEqualTo(species.getId());
    }

    @Test
    void shouldDeleteByAquariumIdAndFishSpeciesId() {
        // Cel: Sprawdzenie usuwania ryb z akwarium.
        // Weryfikujemy customową metodę delete w repozytorium.

        // --- GIVEN ---
        Aquarium aquarium = createValidAquarium("Krewetkarium");
        FishSpecies species = createFishSpecies("Krewetka Red Cherry");

        AquariumFish fish = new AquariumFish();
        fish.setAquarium(aquarium);
        fish.setFishSpecies(species);
        fish.setFishCount(50);
        entityManager.persist(fish);
        entityManager.flush(); 

        // --- WHEN ---
        repository.deleteByAquariumIdAndFishSpeciesId(aquarium.getId(), species.getId());

        // --- THEN ---
        Optional<AquariumFish> deletedFish = repository.findById(fish.getId());
        assertThat(deletedFish).isEmpty(); 
    }

    @Test
    void shouldThrowExceptionWhenSavingDuplicateFishInSameAquarium() {
        // Cel: Sprawdzenie integralności danych.
        // Weryfikujemy, czy baza danych (constrainty unikalności) blokuje dodanie
        // dwóch identycznych wpisów (ten sam gatunek w tym samym akwarium).
        
        // --- GIVEN ---
        Aquarium aquarium = createValidAquarium("Akwarium Ogólne");
        FishSpecies species = createFishSpecies("Skalar");

        // Pierwszy zapis (powinien się udać)
        AquariumFish fish1 = new AquariumFish();
        fish1.setAquarium(aquarium);
        fish1.setFishSpecies(species);
        fish1.setFishCount(2);
        repository.save(fish1);
        repository.flush(); 

        // Próba dodania tego samego gatunku do tego samego akwarium jako nowy wpis
        AquariumFish fish2 = new AquariumFish();
        fish2.setAquarium(aquarium);
        fish2.setFishSpecies(species);
        fish2.setFishCount(5);

        // --- WHEN & THEN ---
        // Oczekujemy wyjątku DataIntegrityViolationException (naruszenie constraintów bazy)
        assertThrows(DataIntegrityViolationException.class, () -> {
            repository.save(fish2);
            repository.flush(); 
        });
    }

    // --- METODY POMOCNICZE (Helpers) ---
    private Aquarium createValidAquarium(String name) {
        Aquarium aquarium = new Aquarium();
        aquarium.setName(name);
        aquarium.setVolumeLiters(100);       
        aquarium.setTemperatureC(25.0);      
        aquarium.setPh(7.0);                 
        aquarium.setWaterType("Słodkowodne");
        aquarium.setBiotope("Amazonka");
        return entityManager.persist(aquarium);
    }

    private FishSpecies createFishSpecies(String name) {
        FishSpecies species = new FishSpecies();
        species.setName(name);
        species.setTempMinC(20);
        species.setTempMaxC(28);
        species.setPhMin(6.0);
        species.setPhMax(7.5);
        return entityManager.persist(species);
    }
}