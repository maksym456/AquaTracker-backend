package com.aquatracker.aquarium;

import com.aquatracker.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import java.time.LocalDateTime;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
class AquariumRepositoryTest {

    @Autowired
    private AquariumRepository repository; 

    @Autowired
    private TestEntityManager entityManager; 

    @Test
    void shouldFindAquariumsByOwnerId() {
        // Cel: Sprawdzenie, czy metoda findByOwner_Id poprawnie filtruje dane.
        // Chcemy mieć pewność, że użytkownik dostanie TYLKO swoje akwaria, a nie sąsiada.

        // --- GIVEN ---
        
        User owner = new User();
        owner.setUsername("TestUser");
        owner.setEmail("owner@test.com");
        owner.setIsAdmin(false); 
        owner.setActive(true);
        entityManager.persist(owner); 

        Aquarium aquarium = createValidAquarium("Akwarium Marka");
        aquarium.setOwner(owner); 
        entityManager.persist(aquarium);

        User otherUser = new User();
        otherUser.setUsername("OtherUser");
        otherUser.setEmail("other@test.com"); 
        otherUser.setIsAdmin(false);
        entityManager.persist(otherUser);

        Aquarium otherAquarium = createValidAquarium("Obce Akwarium");
        otherAquarium.setOwner(otherUser);
        entityManager.persist(otherAquarium);

        // --- WHEN ---
        List<Aquarium> results = repository.findByOwner_Id(owner.getId());

        // --- THEN ---
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Akwarium Marka");
        assertThat(results.get(0).getOwner().getId()).isEqualTo(owner.getId());
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoAquariums() {
        // Cel: Sprawdzenie przypadku brzegowego (Edge Case).
        // Upewniamy się, że jeśli użytkownik nie ma akwariów, metoda zwraca pustą listę 

        // --- GIVEN ---
        User userWithoutAquarium = new User();
        userWithoutAquarium.setUsername("LonelyUser");
        userWithoutAquarium.setEmail("lonely@test.com");
        userWithoutAquarium.setIsAdmin(false);
        
        entityManager.persist(userWithoutAquarium);

        // --- WHEN ---
        List<Aquarium> results = repository.findByOwner_Id(userWithoutAquarium.getId());

        // --- THEN ---
        assertThat(results).isEmpty(); 
    }

    // --- METODA POMOCNICZA ---
    // Służy do tworzenia obiektów, które przejdą walidację JPA (np. @NotNull, @Min).
    // Dzięki temu testy nie wywalają się na błędach walidacji (np. "temperatura nie może być null"), 
    private Aquarium createValidAquarium(String name) {
        Aquarium aquarium = new Aquarium();
        aquarium.setName(name);              
        aquarium.setVolumeLiters(100);       
        aquarium.setTemperatureC(25.0);      
        aquarium.setPh(7.0);                
        aquarium.setWaterType("Słodkowodne");
        aquarium.setBiotope("Amazonka");
        aquarium.setCreatedAt(LocalDateTime.now());
        return aquarium;
    }
}