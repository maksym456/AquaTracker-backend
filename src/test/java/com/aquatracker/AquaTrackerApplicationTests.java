package com.aquatracker;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase; 
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureTestDatabase 
class AquaTrackerApplicationTests {

    @Test
    void contextLoads() {
        // "Sanity Check".
        // Sprawdza czy kontekst aplikacji Spring Boot (wszystkie beany, serwisy, repozytoria)
        // jest w stanie uruchomić się poprawnie bez rzucania wyjątków.
    }

}