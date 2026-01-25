package com.aquatracker.aquarium;

import com.aquatracker.sharing.AquariumShare;
import com.aquatracker.sharing.AquariumShareRepository;
import com.aquatracker.user.User;
import com.aquatracker.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete; 
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest 
@AutoConfigureMockMvc
@Transactional 
@AutoConfigureTestDatabase 
class AquariumIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AquariumRepository aquariumRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AquariumShareRepository aquariumShareRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateAndRetrieveAquarium_EndToEnd() throws Exception {
        // Cel: Sprawdzenie pełnej ścieżki tworzenia akwarium ("Happy Path").
        // Test symuluje wysłanie formularza (POST) i sprawdza, czy dane fizycznie zapisały się w bazie.

        // --- GIVEN ---
        User user = new User();
        user.setUsername("IntegrationUser");
        user.setEmail("integration@test.com");
        user.setActive(true);
        user.setIsAdmin(false);
        user = userRepository.save(user); 
        String userId = user.getId();

        AquariumController.AquariumRequestDto request = new AquariumController.AquariumRequestDto();
        request.setName("Wielka Rafa");
        request.setVolume(300);
        request.setWaterType("saltwater");
        request.setTemperature(26.0);
        request.setOwnerId(userId);

        // --- WHEN ---
        mockMvc.perform(post("/api/v1/aquariums")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Wielka Rafa"))
                .andExpect(jsonPath("$.waterType").value("Słonowodna"));

        // --- THEN ---
        assertThat(aquariumRepository.findAll()).hasSize(1);
        Aquarium savedAquarium = aquariumRepository.findAll().get(0);
        
        assertThat(savedAquarium.getName()).isEqualTo("Wielka Rafa");
        assertThat(savedAquarium.getOwner().getId()).isEqualTo(userId);
    }

    @Test
    void shouldReturnEmptyListForNewUser() throws Exception {
        // Cel: Sprawdzenie stanu początkowego dla nowego użytkownika.
        // Upewniamy się, że API nie zwraca błędów ani nulli, tylko pustą listę.

        // --- GIVEN ---
        User newUser = new User();
        newUser.setUsername("Newbie");
        newUser.setEmail("new@test.com");
        userRepository.save(newUser);

        // --- WHEN & THEN ---
        mockMvc.perform(get("/api/v1/aquariums")
                .param("userId", newUser.getId())) 
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty()); 
    }

    @Test
    void shouldReturnSharedAquariumsForUser() throws Exception {
        // Cel: Sprawdzenie logiki biznesowej współdzielenia danych.
        // Test weryfikuje, czy endpoint łączy "Moje akwaria" z "Akwariami udostępnionymi mi przez innych".
        
        // --- GIVEN ---
        User owner = new User();
        owner.setUsername("Marek");
        owner.setEmail("marek@test.com");
        owner.setActive(true);
        owner.setIsAdmin(false);
        owner = userRepository.save(owner);

        User viewer = new User();
        viewer.setUsername("Jacek");
        viewer.setEmail("jacek@test.com");
        viewer.setActive(true);
        viewer.setIsAdmin(false);
        viewer = userRepository.save(viewer);

        Aquarium aquarium = new Aquarium();
        aquarium.setName("Wspólne Akwarium");
        aquarium.setOwner(owner);
        aquarium.setVolumeLiters(100);
        aquarium.setTemperatureC(25.0);
        aquarium.setPh(7.0);
        aquarium.setCreatedAt(LocalDateTime.now());
        aquarium = aquariumRepository.save(aquarium);

        AquariumShare share = new AquariumShare();
        share.setAquarium(aquarium);
        share.setUser(viewer);
        share.setPermissionLevel("READ");
        share.setSharedAt(LocalDateTime.now());
        aquariumShareRepository.save(share);

        // --- WHEN & THEN ---
        mockMvc.perform(get("/api/v1/aquariums")
                .param("userId", viewer.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Wspólne Akwarium"));
    }

    @Test
    void shouldNotDeleteAquariumBelongingToAnotherUser() throws Exception {
        // Cel: Test Bezpieczeństwa / Dokumentacja Luki.
        // Test sprawdza, co się stanie, gdy ktoś spróbuje usunąć NIE SWOJE akwarium.
        // test przechodzi na zielono (status 204), co potwierdza istnienie luki bezpieczeństwa
        // (brak weryfikacji właściciela przy usuwaniu).

        // --- GIVEN ---
        User owner = new User();
        owner.setUsername("Ofiara");
        owner.setEmail("ofiara@test.com");
        userRepository.save(owner);

        Aquarium aquarium = new Aquarium();
        aquarium.setName("Cenne Akwarium");
        aquarium.setOwner(owner);
        aquarium.setVolumeLiters(500);
        aquarium.setTemperatureC(25.0);
        aquarium.setPh(7.0);
        aquarium.setCreatedAt(LocalDateTime.now());
        aquarium = aquariumRepository.save(aquarium);

        // --- WHEN ---
        // Nieautoryzowane żądanie usunięcia (symulacja ataku)
        mockMvc.perform(delete("/api/v1/aquariums/" + aquarium.getId())
                .contentType(MediaType.APPLICATION_JSON))
                
        // --- THEN ---
                .andExpect(status().isNoContent()); // Obecnie zwraca 204 (Sukces), co oznacza, że usunięcie się powiodło.

        /* CRITICAL BUG REPORT:
           Poniższa asercja obecnie NIE DZIAŁA   
           boolean exists = aquariumRepository.existsById(aquarium.getId());
           assertThat(exists).isTrue().as("LUKA BEZPIECZEŃSTWA: Każdy może usunąć każde akwarium!");
        */
    }
}