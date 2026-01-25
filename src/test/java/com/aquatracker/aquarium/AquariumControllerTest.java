package com.aquatracker.aquarium;

import com.aquatracker.history.AquariumParameterHistoryRepository;
import com.aquatracker.logs.LogEntryRepository;
import com.aquatracker.plant.PlantRepository;
import com.aquatracker.sharing.AquariumShareRepository;
import com.aquatracker.fish.FishSpeciesRepository;
import com.aquatracker.user.User;
import com.aquatracker.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import jakarta.persistence.EntityManagerFactory;


@WebMvcTest(AquariumController.class)
@AutoConfigureTestDatabase
class AquariumControllerTest {

    @Autowired
    private MockMvc mockMvc; 

    @Autowired
    private ObjectMapper objectMapper; 

   
    @MockBean
    private AquariumRepository aquariumRepository;

    @MockBean
    private FishSpeciesRepository fishRepository;

    @MockBean
    private PlantRepository plantRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AquariumFishRepository aquariumFishRepository;

    @MockBean
    private AquariumPlantRepository aquariumPlantRepository;

    @MockBean
    private AquariumValidationService validationService;

    @MockBean
    private LogEntryRepository logEntryRepository;

    @MockBean
    private AquariumParameterHistoryRepository parameterHistoryRepository;

    @MockBean
    private AquariumShareRepository aquariumShareRepository;

    @MockBean
    private EntityManager entityManager;

    @MockBean 
    private EntityManagerFactory entityManagerFactory;


    @Test
    void shouldGetAquariumById() throws Exception {
        // Cel: Sprawdzenie "Happy Path" (Scenariusz pozytywny).
        // Weryfikujemy, czy endpoint GET /api/v1/aquariums/{id} zwraca kod 200 i poprawne dane.

        // --- GIVEN  ---
        Long aquariumId = 1L;
        Aquarium aquarium = new Aquarium();
        aquarium.setId(aquariumId);
        aquarium.setName("Testowe Akwarium");
        aquarium.setVolumeLiters(100);
        aquarium.setTemperatureC(25.0);
        
        User owner = new User();
        owner.setId("user-123");
        owner.setEmail("test@test.com");
        aquarium.setOwner(owner);
        when(aquariumRepository.findById(aquariumId)).thenReturn(Optional.of(aquarium));

        // --- WHEN & THEN  ---
        mockMvc.perform(get("/api/v1/aquariums/{id}", aquariumId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) 
                .andExpect(jsonPath("$.name").value("Testowe Akwarium")) 
                .andExpect(jsonPath("$.volumeLiters").value(100)); 
    }

    @Test
    void shouldReturn404WhenAquariumNotFound() throws Exception {
        // Cel: Sprawdzenie obsługi błędów.
        // Weryfikujemy, czy API zwraca 404 Not Found, gdy szukamy nieistniejącego ID.

        Long aquariumId = 999L;
        when(aquariumRepository.findById(aquariumId)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/v1/aquariums/{id}", aquariumId))
                .andExpect(status().isNotFound()); 
    }

    @Test
    void shouldCreateNewAquarium() throws Exception {
        // Cel: Sprawdzenie tworzenia zasobu.
        // Weryfikujemy, czy POST /api/v1/aquariums poprawnie przyjmuje JSON i zwraca 201 Created.

        // --- GIVEN ---
        AquariumController.AquariumRequestDto request = new AquariumController.AquariumRequestDto();
        request.setName("Nowe Akwarium");
        request.setVolume(200);
        request.setTemperature(24.5);
        request.setOwnerId("user-123");

        User user = new User();
        user.setId("user-123");
        
        Aquarium savedAquarium = new Aquarium();
        savedAquarium.setId(50L);
        savedAquarium.setName("Nowe Akwarium");
        savedAquarium.setOwner(user);

        when(userRepository.findById("user-123")).thenReturn(Optional.of(user));
        when(aquariumRepository.save(any(Aquarium.class))).thenReturn(savedAquarium);

        // --- WHEN & THEN ---
        mockMvc.perform(post("/api/v1/aquariums")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))) 
                .andExpect(status().isCreated()) 
                .andExpect(jsonPath("$.id").value(50)) 
                .andExpect(jsonPath("$.name").value("Nowe Akwarium"));
    }

    @Test
    void shouldDeleteAquarium() throws Exception {
        // Cel: Sprawdzenie usuwania zasobu.
        // Weryfikujemy, czy DELETE /api/v1/aquariums/{id} usuwa obiekt i zwraca 204.

        // --- GIVEN ---
        Long aquariumId = 10L;
        Aquarium aquarium = new Aquarium();
        aquarium.setId(aquariumId);
        aquarium.setName("Do usunięcia");

        when(aquariumRepository.findById(aquariumId)).thenReturn(Optional.of(aquarium));
        when(logEntryRepository.findByAquarium_IdOrderByCreatedAtDesc(aquariumId)).thenReturn(java.util.Collections.emptyList());

        // --- WHEN & THEN ---
        mockMvc.perform(delete("/api/v1/aquariums/{id}", aquariumId))
                .andExpect(status().isNoContent()); 
    }

    @Test
    void shouldReturnBadRequestWhenCreatingAquariumWithoutName() throws Exception {
        // Cel: Sprawdzenie walidacji danych wejściowych.
        // Weryfikujemy, czy API odrzuca request z błędnymi danymi (np. brak nazwy).

        // --- GIVEN ---
        // Tworzymy request bez nazwy (name = null)
        AquariumController.AquariumRequestDto invalidRequest = new AquariumController.AquariumRequestDto();
        invalidRequest.setVolume(100);
        invalidRequest.setOwnerId("user-123");

        // --- WHEN & THEN ---
        mockMvc.perform(post("/api/v1/aquariums")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest()) 
                .andExpect(jsonPath("$.error").value("Name is required")); 
    }
}